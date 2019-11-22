package com.robusta.commons.messaging;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Objects.requireNonNull;
import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MAX_INFLIGHT_DEFAULT;

public abstract class AbstractMqttClient {

    private static final long FIVE_SECONDS = 5000;
    private IMqttAsyncClient mqttClient;
    private String 				brokerUrl;
    private String clientName;
    private int maxInflightMessage = MAX_INFLIGHT_DEFAULT;

    protected final Logger appLogger = LoggerFactory.getLogger(getClass());

    private MqttCallback mqttRequestHandler;

    public AbstractMqttClient(String clientName, String brokerUrl) {
        this.clientName = clientName;
        this.brokerUrl = brokerUrl;
    }

    public void setMaxInflightMessage(int maxInflightMessage) {
        this.maxInflightMessage = maxInflightMessage;
    }

    public boolean isConnected(){
        return mqttClient != null && mqttClient.isConnected();
    }

    public IMqttDeliveryToken doPublish(String topicName, int qos, byte[] payload, boolean retained) {
        MqttMessage message = buildMqttMessageWithPayload(qos, payload, retained);

        appLogger.debug("Starting publish Message: {} to Topic: {} using QoS: {}", new String(payload, defaultCharset()),
                topicName, qos);

        try {
            IMqttDeliveryToken pubToken = mqttClient.publish(topicName, message, null, new PublishActionListener());
            appLogger.debug("Mqtt Message: Id: {} - Published to topic :{} accepted", pubToken.getMessageId(), topicName);
            return pubToken;
        } catch (MqttException e) {
            appLogger.error("Mqtt Message Publish failed", e);
        }
        return null;
    }

    protected void setMqttRequestHandler(MqttCallback mqttRequestHandler) {
        this.mqttRequestHandler = mqttRequestHandler;
    }

    private class PublishActionListener implements IMqttActionListener {
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            appLogger.info("Mqtt Message was successfully published, message id: {}", iMqttToken.getMessageId());
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            appLogger.error("Mqtt Message publish failed, message id: {}", iMqttToken.getMessageId(), throwable);
        }
    }

    private MqttMessage buildMqttMessageWithPayload(int qos, byte[] payload, boolean retained) {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);
        message.setRetained(retained);
        return message;
    }

    public void connect() {
        connectInternal(defaultTmpFileDataStore(), buildMqttConnectOptions(), null);
    }

    protected void connectInternal(MqttClientPersistence dataStore, MqttConnectOptions conOpt, DisconnectedBufferOptions disBuffferOptions) {
        requireNonNull(mqttRequestHandler, "Cannot connect to MQTT Server, Null callback handler. Perhaps callback handler was not set using setter?");

        try {


            MqttAsyncClient mqttClient = new MqttAsyncClient(brokerUrl, clientName, dataStore);
            this.mqttClient = mqttClient;

            mqttClient.setCallback(mqttRequestHandler);
            if (disBuffferOptions != null) {
                mqttClient.setBufferOpts(disBuffferOptions);
            }

            IMqttToken conToken = mqttClient.connect(conOpt);
            appLogger.debug("Mqtt - Connecting to server: Message Id: {}", conToken.getMessageId());
            conToken.waitForCompletion();
            appLogger.info("SuccessFully connected to MQTTServer {}", System.currentTimeMillis());
        } catch (MqttException e) {
            appLogger.error("Unable to connect to MQTTServer", e);
        }
    }

    private MqttDefaultFilePersistence defaultTmpFileDataStore() {
        final String tmpDir = System.getProperty("java.io.tmpdir");
        appLogger.debug("Mqtt Default File based persistence with tmp dir: {}", tmpDir);
        return new MqttDefaultFilePersistence(tmpDir);
    }

    protected MqttConnectOptions buildMqttConnectOptions() {
        // Construct the object that contains connection parameters
        // such as cleanSession and LWT
        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setAutomaticReconnect(true);
        conOpt.setKeepAliveInterval(20);
        conOpt.setMaxInflight(this.maxInflightMessage);

        appLogger.debug("Mqtt connect options being used: {}", conOpt);
        return conOpt;
    }

    public void subscribeToTopic(String topicName, int qos) throws MqttException {
        IMqttToken subToken = mqttClient.subscribe(topicName, qos, null , new MqttActionListener("Subscribe"));
        appLogger.debug("Subscribed to topic: {} started with Message Id: {}", topicName, subToken.getMessageId());
        subToken.waitForCompletion();
        appLogger.info("Subscribed to topic: {}", topicName);

    }

    private class MqttActionListener implements IMqttActionListener {
        private String caller;

        public MqttActionListener(String caller) {
            this.caller = caller;
        }

        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            appLogger.debug("Action: {} was successful. Request Id: {}", caller, iMqttToken.getMessageId());
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            appLogger.debug("Action: {} was NOT successful. Request Id: {} failed", caller, iMqttToken.getMessageId());
        }
    }

    public void disconnect() throws MqttException {
        if (mqttClient != null) {
            appLogger.info("Disconnecting from MQTT");
            try {
                // This connected check is important here, if we call disconnect without this check,
                // Then server never gets stopped. It just hangs in there.
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect(0).waitForCompletion(FIVE_SECONDS);
                    mqttClient.close();
                } else {
                    mqttClient.close();
                }
            } catch (Exception e) {
                appLogger.warn("MQTT Disconnection failed. Will try force disconnect now", e);
                try {
                    // This connected check is important here, if we call disconnect without this check,
                    // Then server never gets stopped. It just hangs in there.
                    if (mqttClient.isConnected()) {
                        mqttClient.disconnectForcibly(0, FIVE_SECONDS);
                        closeForcibly();
                    } else {
                        appLogger.warn("MQTT client says not connected");
                        closeForcibly();
                    }
                } catch (Exception e1) {
                    appLogger.error("MQTT Force Disconnection failed", e1);
                }
            }
        } else {
            appLogger.warn("MQTT Client instance NULL, nothing to shutdown.");
        }
    }

    private void closeForcibly() throws MqttException {
        if (MqttClient.class.isAssignableFrom(mqttClient.getClass())) {
            MqttClient.class.cast(mqttClient).close(true);
        } else {
            mqttClient.close();
        }
    }

    public abstract class AbstractMqttRequestHandler implements MqttCallbackExtended {

        @Override
        public void connectionLost(Throwable throwable) {
            appLogger.info("Connection lost to the MQTT server at: {}", System.currentTimeMillis());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            try {
                appLogger.info("SuccessFully delivered the message: {}", iMqttDeliveryToken.getMessage());
            } catch (MqttException e) {
                appLogger.error("Exception in reading the delivered message", e);
            }
        }

        @Override
        public void connectComplete(boolean wasReconnect, String s) {
            if( wasReconnect ) {
                appLogger.info("SuccessFully reconnected");
            } else {
                appLogger.info("SuccessFully connected");
            }
        }

        protected void infoLogIncomingMessage(String topicName, MqttMessage mqttMessage, String payloadStr) {
            appLogger.info("Received MQTT message Message Id: {}, with payload: {} from topic: {}", mqttMessage.getId(),
                    payloadStr, topicName);
        }
    }

    protected String clientTopicName(String clientID) {
        return format("client/%s", clientID);
    }

    protected String machineTopicName(String sender) {
        return format("machine/%s", sender);
    }
}
