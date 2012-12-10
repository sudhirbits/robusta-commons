package com.robusta.commons.persistence;

import com.robusta.commons.context.UserContextHolder;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.robusta.commons.domain.user.UserFixture.aUser;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(JMock.class)
public class AuditListenerTest {
    private AuditListener auditListener;
    private NonEntityObject nonEntityObject;
    private EntityObject entityObject;
    private Mockery mockery = new JUnit4Mockery();
    private static final String USERNAME = "testUser";

    @Before
    public void setUp() throws Exception {
        UserContextHolder.setCurrentUser(aUser().withLoginName(USERNAME).build());
        nonEntityObject = mockery.mock(NonEntityObject.class);
        entityObject = mockery.mock(EntityObject.class);
        auditListener = new AuditListener();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreation_willNullObject_shouldThrowException() throws Exception {
        auditListener.creation(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdation_willNullObject_shouldThrowException() throws Exception {
        auditListener.updation(null);
    }

    @Test
    public void testCreation_nonEntityObject_shouldBeANoOp() throws Exception {
        auditListener.creation(nonEntityObject);
    }

    @Test
    public void testCreation_entityObject_shouldInsertAuditFields() throws Exception {
        expectingCreatedByAndCreatedDateSetterInvocationsOn(entityObject);
        auditListener.creation(entityObject);
    }

    @Test
    public void testUpdation_nonEntityObject_shouldBeANoOp() throws Exception {
        auditListener.updation(nonEntityObject);
    }

    @Test
    public void testUpdation_entityObject_shouldInsertAuditFields() throws Exception {
        expectingUpdatedByAndUpdatedDateSetterInvocationsOn(entityObject);
        auditListener.updation(entityObject);
    }

    private void expectingUpdatedByAndUpdatedDateSetterInvocationsOn(final EntityObject entityObject) {
        mockery.checking(new Expectations() {{
            oneOf(entityObject).setUpdatedBy(USERNAME);
            oneOf(entityObject).setUpdatedDate(with(notNullValue(Date.class)));
        }});
    }

    private void expectingCreatedByAndCreatedDateSetterInvocationsOn(final EntityObject entityObject) {
        mockery.checking(new Expectations() {{
            oneOf(entityObject).setCreatedBy(USERNAME);
            oneOf(entityObject).setCreatedDate(with(notNullValue(Date.class)));
        }});
    }

    public static interface NonEntityObject {}

    public static interface EntityObject extends Creatable, Updatable {}
}
