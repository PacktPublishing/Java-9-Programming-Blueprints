package com.steeplesoft.cloudnotice.function;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 *
 * @author jason
 */
public class TestContext implements Context {

    private CognitoIdentity identity;
    private ClientContext clientContext;

    public TestContext() {
    }

    @Override
    public String getAwsRequestId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLogGroupName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLogStreamName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFunctionName() {
        return "TestFunction";
    }

    @Override
    public String getFunctionVersion() {
        return "1.0";
    }

    @Override
    public String getInvokedFunctionArn() {
        return "0000-0000-0000";
    }

    @Override
    public CognitoIdentity getIdentity() {
        return identity;
    }

    @Override
    public ClientContext getClientContext() {
        return clientContext;
    }

    @Override
    public int getRemainingTimeInMillis() {
        return 1000;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 512;
    }

    @Override
    public LambdaLogger getLogger() {
        return new TestLogger();
    }

    private static class TestLogger implements LambdaLogger {

        public TestLogger() {
        }

        @Override
        public void log(String string) {
            System.err.println(string);
        }
    }

}
