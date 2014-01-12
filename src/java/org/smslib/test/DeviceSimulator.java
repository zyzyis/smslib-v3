package org.smslib.test;

import org.smslib.InboundMessage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 */
public class DeviceSimulator {
    private Queue<InboundMessage> pendingMessages;
    private String id;
    private int power;
    private int automode;
    private byte[] state;

    public String getId() {
        return id;
    }

    public synchronized List<InboundMessage> fetchMessage() {
        List<InboundMessage> result = new ArrayList<>();
        while (!pendingMessages.isEmpty()) {
            result.add(pendingMessages.poll());
        }
        return result;
    }

    public synchronized void receiveMessage(String msg) {
        String content = createMessage(msg);
        InboundMessage newMessage = new InboundMessage(
            new java.util.Date(),
            id,
            content,
            0,
            null
        );
        pendingMessages.add(newMessage);
    }

    public DeviceSimulator(String id) {
        this.id = id;
        this.power = 0;
        this.automode = 1;
        this.state = new byte[20];
        pendingMessages = new LinkedBlockingDeque<>();
    }

    private String createRealtimeMessage() {
        Calendar calendar = Calendar.getInstance();
        final int[] cc = new int[] {0x158, 0x192, 0x1D0, 0x24E, 0x2BE, 0x330, 0x3A6};
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int vol = cc[(int)(Math.random() * (cc.length - 1))];
        int cur = 0x123;
        int temp = 0x020;
        int pwm = 2;
        int error = 0;

        byte[] tmp = ByteBuffer.allocate(2).putInt(hour).array();
        state[0] = tmp[0];
        state[1] = tmp[1];

        tmp = ByteBuffer.allocate(2).putInt(min).array();
        state[2] = tmp[0];
        state[3] = tmp[1];

        tmp = ByteBuffer.allocate(2).putInt(vol).array();
        state[4] = tmp[0];
        state[5] = tmp[1];

        tmp = ByteBuffer.allocate(2).putInt(cur).array();
        state[6] = tmp[0];
        state[7] = tmp[1];

        tmp = ByteBuffer.allocate(2).putInt(temp).array();
        state[8] = tmp[0];

        tmp = ByteBuffer.allocate(2).putInt(pwm).array();
        state[9] = tmp[0];

        tmp = ByteBuffer.allocate(2).putInt(error).array();
        state[10] = tmp[0];

        return new String(state);
    }

    private String createMessage(String cmd) {
        String result = "defaultvalue";
        String[] tokens = cmd.split("[ ,]");
        switch (tokens[0]) {
            case "order0" :
                // set power
                power = Integer.parseInt(tokens[1].toLowerCase());
                break;
            case "order1" :
                // set auto mode
                automode = 1;
                break;
            case "order2" :
                // remote reset
                break;
            case "order3" :
                // return setting
                break;
            case "order4" :
                // return realtime data
                result = createRealtimeMessage();
                break;
            case "order5" :
                // show log data
                break;
            case "order6":
                break;
        }
        return result;
    }
}
