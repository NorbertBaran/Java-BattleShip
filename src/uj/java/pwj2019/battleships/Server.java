package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server extends Communication{
    private ServerSocket serverSocket;

    @Override
    public void run() {
        while (true){
            if(!getAction())
                break;
            if(!postAction())
                break;
        }
        endOfGame();
    }

    Server(int port, BattleShipsUser battleShipsUser){
        super(battleShipsUser);
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
