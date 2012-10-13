package com.bulain.multicast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MulticastDemo {
    private MulticastSocket ms;
    private InetAddress mcastaddr;
    private String cluster = "cluster";
    private int port = 3000;

    private ReceiveThread thread;

    @Before
    public void setUp() throws IOException {
        //init
        ms = new MulticastSocket(port);
        mcastaddr = InetAddress.getByName("228.8.8.8");//from 224.0.0.0 to 239.255.255.255
        ms.joinGroup(mcastaddr);

        //start
        thread = new ReceiveThread();
    }

    @After
    public void tearDown() throws IOException {
        //stop thread
        thread.setRunning(false);

        //leaveGroup
        ms.leaveGroup(mcastaddr);
        ms.close();
    }

    @Test
    @SuppressWarnings("static-access")
    public void testMulticast() throws IOException, ClassNotFoundException {
        System.out.println("testReceive");
        thread.start();

        System.out.println("testSend");
        for (int i = 0; i < 20; i++) {
            String str = String.format("Test%3d", i);
            sendObject(str);
            System.out.println("Send: " + str);
        }

        //sleep
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendObject(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeUTF(cluster);
        oos.writeObject(obj);
        oos.flush();
        byte[] data = bos.toByteArray();
        DatagramPacket dp = new DatagramPacket(data, data.length, mcastaddr, port);
        ms.send(dp);
    }

    public class ReceiveThread extends Thread {
        private boolean running = true;

        public void run() {
            byte[] buf = new byte[4096];
            while (running) {
                try {
                    DatagramPacket p = new DatagramPacket(buf, buf.length);
                    ms.receive(p);
                    ByteArrayInputStream bin = new ByteArrayInputStream(buf);
                    ObjectInputStream oin = new ObjectInputStream(bin);
                    String clname = oin.readUTF();
                    if (cluster.equals(clname)) {
                        Object ro = oin.readObject();
                        System.out.println("Receive: " + ro);
                    }
                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                }
            }
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

    }
}
