package com.robusta.commons.async.test;

import com.robusta.commons.async.api.AsynchronousActivity;

public interface TestOperation extends AsynchronousActivity<TestOperation.TestParameters, TestOperation.TestResults> {
    public static class TestParameters {
        private final String parameter1;
        private final String parameter2;

        public TestParameters(String parameter1, String parameter2) {
            this.parameter1 = parameter1;
            this.parameter2 = parameter2;
        }

        public String getParameter1() {
            return parameter1;
        }

        public String getParameter2() {
            return parameter2;
        }
    }

    public static class TestResults {
        private final String result1;
        private final String result2;

        public String getResult1() {
            return result1;
        }

        public String getResult2() {
            return result2;
        }

        public TestResults(String result1, String result2) {
            this.result1 = result1;
            this.result2 = result2;
        }
    }
}
