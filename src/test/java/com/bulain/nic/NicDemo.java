package com.bulain.nic;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

public class NicDemo {
    @Test
    public void testNic() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            displayNetworkInterface(networkInterface);
        }
    }

    private void displayNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        byte[] arrayOfByte = networkInterface.getHardwareAddress();
        if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
            System.out.println("===============================================");
            System.out.println(networkInterface.getDisplayName());
            System.out.println("Hardware address: " + Arrays.toString(arrayOfByte));

            System.out.println("Loop back: " + networkInterface.isLoopback());
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                System.out.println("IP address: [" + inetAddress.getHostAddress() + "]");
            }

            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                System.out.println("Subnet mask: [/" + interfaceAddress.getNetworkPrefixLength() + "]");
            }
        }
    }
}
