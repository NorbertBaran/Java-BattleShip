package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

    BattleShipsUser battleShipsUser;

    Socket socket;
    Scanner scanner;
    String myStatus;
    boolean win;

    public void send(String request){
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(){
        String line=null;
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            line=reader.readLine();
            System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public void run() {
        while(true) {
            if(myStatus.equals("ostatni zatopiony")){
                System.out.println("Pzegrana");
                send(myStatus+";\n");
                win=false;
                break;
            }

            String hisField = scanner.nextLine();
            send(myStatus+";"+hisField+"\n");
            String line = get();

            String hisStatus=line.substring(0, line.indexOf(';'));

            if(hisStatus.equals("ostatni zatopiony")){
                System.out.println("Wygrana");
                win=true;
                break;
            }

            String myField=line.substring(line.indexOf(';')+1);
            myStatus=battleShipsUser.getFieldStatus(myField);
        }

        if(win)
            battleShipsUser.createWinMap();
        for(int i=0; i<10; i++){
            if(!win)
                send(battleShipsUser.getInitMapLine(i)+"\n");
            else
                send(battleShipsUser.getWinMapLine(i)+"\n");
            get();
        }
        System.out.println();
        battleShipsUser.displayMap();
    }

    Client(int port, BattleShipsUser battleShipsUser){
        try {
            this.battleShipsUser=battleShipsUser;
            socket = new Socket("localhost", port);
            scanner=new Scanner(System.in);
            myStatus="start";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
