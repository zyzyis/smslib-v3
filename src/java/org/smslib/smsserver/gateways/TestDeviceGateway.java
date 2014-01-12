package org.smslib.smsserver.gateways;

import java.util.Properties;

/**
 * Gateway mapped to org.smslib.test.TestGateway. Used for internal testing.
 */
public class TestDeviceGateway extends AGateway {
    public TestDeviceGateway (
            String myGatewayId,
            Properties myProps,
            org.smslib.smsserver.SMSServer myServer
    ) {
        super(myGatewayId, myProps, myServer);
        setDescription("Default Test Gateway.");
    }

    @Override
    public void create() throws Exception {
        String propName;
        propName = getGatewayId() + ".";
        setGateway(new org.smslib.test.TestDeviceGateway(getGatewayId()));
        if (getProperties().getProperty(propName + "inbound").equalsIgnoreCase("yes"))
            getGateway().setInbound(true);
        else if (getProperties().getProperty(propName + "inbound").equalsIgnoreCase("no"))
            getGateway().setInbound(false);
        else throw new Exception("Incorrect parameter: " + propName + "inbound");
        if (getProperties().getProperty(propName + "outbound").equalsIgnoreCase("yes"))
            getGateway().setOutbound(true);
        else if (getProperties().getProperty(propName + "outbound").equalsIgnoreCase("no"))
            getGateway().setOutbound(false);
        else throw new Exception("Incorrect parameter: " + propName + "outbound");
    }
}
