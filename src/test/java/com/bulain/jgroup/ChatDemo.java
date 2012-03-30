package com.bulain.jgroup;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class ChatDemo extends ReceiverAdapter {
    private JChannel channel;
    private String userName;

    public static void main(String[] args) throws Exception {
        ChatDemo demo = new ChatDemo();
        demo.start();
    }

    public void viewAccepted(View view) {
        System.out.println("** view: " + view);
    }

    public void receive(Message msg) {
        System.out.println("received msg from " + msg.getSrc() + ": " + msg.getObject());
    }

    private void start() throws Exception {
        userName = System.getProperty("user.name", "n/a");
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if (line.startsWith("quit") || line.startsWith("exit"))
                    break;
                line = "[" + userName + "] " + line;
                Message msg = new Message(null, null, line);
                channel.send(msg);
            } catch (Exception e) {
            }

        }

    }
}
