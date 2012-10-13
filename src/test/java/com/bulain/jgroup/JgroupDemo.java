package com.bulain.jgroup;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.junit.Test;

public class JgroupDemo {

    @Test
    @SuppressWarnings("static-access")
    public void testDemo() throws Exception {
        JChannel channel = new JChannel();
        channel.setReceiver(new ReceiverAdapter() {
            public void receive(Message msg) {
                System.out.println("received msg from " + msg.getSrc() + ": " + msg.getObject());
            }
        });
        channel.connect("DemoCluster");
        channel.send(new Message(null, null, "hello world"));
        channel.close();

        //sleep
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
