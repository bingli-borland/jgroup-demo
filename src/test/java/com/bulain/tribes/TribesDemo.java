package com.bulain.tribes;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.group.GroupChannel;
import org.junit.Test;

public class TribesDemo {

    @Test
    public void testDemo() throws Exception {
        //create a channel
        Channel channel = new GroupChannel();

        //create my listeners
        ChannelListener msgListener = new MyMessageListener();
        MembershipListener mbrListener = new MyMemberListener();

        //attach the listeners to the channel
        channel.addMembershipListener(mbrListener);
        channel.addChannelListener(msgListener);

        //start the channel
        channel.start(Channel.DEFAULT);
        
        //create a message to be sent, message must implement java.io.Serializable
        //for performance reasons you probably want them to implement java.io.Externalizable
        Serializable myMsg = new MyMessage(Long.valueOf(1L), "message");

        //retrieve my current members
        Member[] group = channel.getMembers();

        //send the message
        channel.send(group, myMsg, Channel.SEND_OPTIONS_DEFAULT);
        
        sleep(5);
    }

    private void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MyMessageListener implements ChannelListener {
        private final Logger logger = Logger.getLogger(MyMessageListener.class);

        public boolean accept(Serializable msg, Member sender) {
            boolean acpt = (msg instanceof MyMessage);
            return acpt;
        }

        public void messageReceived(Serializable msg, Member sender) {
            logger.info("messageReceived(Serializable, Member) - msg=" + msg + ", sender=" + sender);
        }

    }

    static class MyMemberListener implements MembershipListener {
        private List<Member> listMember = new ArrayList<Member>();

        public void memberAdded(Member member) {
            listMember.add(member);
        }

        public void memberDisappeared(Member member) {
            listMember.remove(member);
        }

        public List<Member> getListMember() {
            return listMember;
        }

    }

    static class MyMessage implements Serializable {
        private static final long serialVersionUID = -2671151021018367290L;

        private Long id;
        private String message;

        public MyMessage(Long id, String message) {
            this.id = id;
            this.message = message;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int hashCode() {
            return id.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof MyMessage) {
                MyMessage that = (MyMessage) obj;
                return id.equals(that.getId());
            }

            return false;
        }

        public String toString() {
            return String.format("%d - %s", id, message);
        }

    }

}
