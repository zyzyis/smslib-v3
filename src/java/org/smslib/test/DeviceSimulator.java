package org.smslib.test;

import org.smslib.*;
import org.smslib.helper.Logger;
import org.smslib.notify.InboundMessageNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 */
public class DeviceSimulator extends AGateway {

    private ConcurrentLinkedQueue<String> cmds;
    private int refCounter = 0;
    private int power;
    private int automode;

    /**
     * Duration between incoming messages in milliseconds
     */
    protected int receiveCycle;
    public DeviceSimulator(String id) {
        super(id);
        setAttributes(GatewayAttributes.SEND | GatewayAttributes.RECEIVE);
        setInbound(true);
        setOutbound(true);
        this.receiveCycle = 6000;
        cmds = new ConcurrentLinkedQueue<String>();
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
        while (!cmds.isEmpty()) {
            incInboundMessageCount();
            String cmd = cmds.poll();
            String content = createMessage(cmd);
            InboundMessage msg =
                new InboundMessage(
                    new java.util.Date(),
                    "+1234567890",
                    content,
                    0,
                    null
                );
            msg.setGatewayId(this.getGatewayId());
            result.add(msg);
        }
        return result;
    }

    private String createMessage(String cmd) {
        String result = null;
        switch (cmd) {
            case "order0" : // power adjust
                break;
            case "order1" :
                break;
            case "order2" :
                break;
            case "order3" :
                break;
            case "order4" :
                break;
            case "order5" :
                break;
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
    public void readMessages(
        Collection<InboundMessage> msgList,
        InboundMessage.MessageClasses
        msgClass
    ) throws
        TimeoutException,
        GatewayException,
        IOException,
        InterruptedException {
        // Return a new generated message
        if (!cmds.isEmpty()) {
            msgList.addAll(fetchMessage());
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

        String content = msg.getText();
        cmds.add(content);

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

    @Override
    public int getQueueSchedulingInterval() {
        return 200;
    }

    @Override
    public boolean isInbound() {
        return true;
    }
}
