package org.smslib.test;

import org.smslib.*;
import org.smslib.AGateway;
import org.smslib.helper.Logger;
import org.smslib.notify.InboundMessageNotification;
import org.smslib.test.DeviceSimulator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestDeviceGateway extends AGateway {
    private int refCounter = 0;
    private DeviceSimulator[] devices;

    /**
     * Duration between incoming messages in milliseconds
     */
    protected int receiveCycle;
    public TestDeviceGateway(String id) {
        super(id);
        setAttributes(GatewayAttributes.SEND | GatewayAttributes.RECEIVE);
        setInbound(true);
        setOutbound(true);
        this.receiveCycle = 6000;
        devices = new DeviceSimulator[5];

        for (int i = 0; i < 5; i ++) {
            devices[i] = new DeviceSimulator("id" + i);
        }
    }

    /* (non-Javadoc)
     * @see org.smslib.AGateway#deleteMessage(org.smslib.InboundMessage)
     */
    @Override
    public boolean deleteMessage(InboundMessage msg) throws
            TimeoutException,
            GatewayException,
            IOException,
            InterruptedException {
        //NOOP
        return true;
    }

    List<InboundMessage> fetchMessage() {
        List<InboundMessage> result = new ArrayList<>();
        for (DeviceSimulator device : devices) {
            result.addAll(device.fetchMessage());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.smslib.AGateway#startGateway()
     */
    @Override
    public void startGateway() throws
            TimeoutException,
            GatewayException,
            IOException,
            InterruptedException {
        super.startGateway();
    }

    /* (non-Javadoc)
     * @see org.smslib.AGateway#stopGateway()
     */
    @Override
    public void stopGateway() throws
            TimeoutException,
            GatewayException,
            IOException,
            InterruptedException {
        super.stopGateway();
    }

    /* (non-Javadoc)
     * @see org.smslib.AGateway#readMessages(java.util.List, org.smslib.MessageClasses)
     */
    @Override
    public void readMessages (
        Collection<InboundMessage> msgList,
        InboundMessage.MessageClasses
        msgClass
    ) throws
        TimeoutException,
        GatewayException,
        IOException,
        InterruptedException {
        // Return a new generated message
        List<InboundMessage> messages = fetchMessage();
        if (messages != null && messages.size() > 0) {
            msgList.addAll(messages);
        }
    }

    @Override
    public boolean sendMessage(OutboundMessage msg) throws
            TimeoutException,
            GatewayException,
            IOException,
            InterruptedException {
        //if (getGatewayId().equalsIgnoreCase("Test3"))
        // throw new IOException("Dummy Exception!!!");
        // simulate delay
        Logger.getInstance()
            .logInfo(
                "Sending to: " + msg.getRecipient() + " via: " + msg.getGatewayId(),
                null,
                getGatewayId()
            );

        String id = msg.getRecipient();
        String content = msg.getText();

        for (DeviceSimulator device : devices) {
            if (device.getId().equals(id)) {
                device.receiveMessage(content);
                msg.setFrom(getFrom());
                msg.setDispatchDate(new Date());
                msg.setMessageStatus(OutboundMessage.MessageStatuses.SENT);
                msg.setRefNo(Integer.toString(++this.refCounter));
                msg.setGatewayId(getGatewayId());
                Logger.getInstance()
                    .logInfo(
                        "Sent to: " + msg.getRecipient() + " via: " + msg.getGatewayId(),
                        null,
                        getGatewayId()
                    );
                incOutboundMessageCount();
                return true;
            }
        }


        throw new IllegalArgumentException("Invalid ID");
    }

    @Override
    public int getQueueSchedulingInterval() {
        return 200;
    }

    @Override
    public boolean isInbound() {
        return true;
    }
}
