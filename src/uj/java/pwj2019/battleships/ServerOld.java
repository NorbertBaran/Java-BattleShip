package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

class ServerOld implements Runnable{
    private final ServerSocket serverSocket;

    ServerOld(InetAddress address, int port) throws IOException {
        this.serverSocket = new ServerSocket(port, 100, address);
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                showRequestInfo(socket);
                var answer = getAnswerFor(socket);
                var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                out.write(answer);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showRequestInfo(Socket s) throws IOException {
        var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            if (line.isEmpty()) break;
        }
    }

    private static String getAnswerFor(Socket s) {
        var b = new StringBuffer();
        b.append("HTTP/1.1 200 OK\r\n")
                .append("Date: " + Instant.now() + "\r\n")
                .append("Server: HttpJavaTestSrv v0.1\r\n")
                .append("Content-Type: text/html\r\n")
                .append("\r\n")
                .append("<title>HttpJavaTestSrv main page</title>\r\n")
                .append("<html><head></head><body>Hello there, at address ")
                .append(s.getRemoteSocketAddress())
                .append("!</body></html>");
        return b.toString();
    }
}
