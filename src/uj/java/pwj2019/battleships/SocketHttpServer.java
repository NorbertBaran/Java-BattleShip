package uj.java.pwj2019.battleships;

import java.net.*;

public class SocketHttpServer {
    public static void main(String[] args) throws Exception {
        final var address = findAddress();
        System.out.println("Address found: " + address);
        ServerOld server = new ServerOld(address, 8080);
        Thread srvThread = new Thread(server, "[HttpSrvThread]");
        srvThread.start();
        System.out.println("Running server at: " + address + ":8080");
    }

    private static InetAddress findAddress() throws SocketException, UnknownHostException {
        var en0 = NetworkInterface.getByName("enp3s0");
        return en0.inetAddresses()
                .filter(a -> a instanceof Inet4Address)
                .findFirst()
                .orElse(InetAddress.getLocalHost());
    }
}
