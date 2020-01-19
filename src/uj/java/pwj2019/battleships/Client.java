package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.Socket;

public class Client extends Communication {

    @Override
    public void run() {
        while(true) {
            if(!postAction())
                break;
            if(!getAction())
                break;
        }
        endOfGame();
    }

    Client(int port, BattleShipsUser battleShipsUser){
        super(battleShipsUser);
        try {
            socket = new Socket("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
