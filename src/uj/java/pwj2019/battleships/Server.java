package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable {

    BattleShipsUser battleShipsUser;

    ServerSocket serverSocket;
    Socket socket;
    Scanner scanner;
    boolean win;
    String myStatus;

    public void send(String response){
        OutputStream output = null;
        try {
            output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(response);
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
        try {
            socket = serverSocket.accept();
            while (true){
                String line = get();
                String hisStatus=line.substring(0, line.indexOf(';'));

                if(hisStatus.equals("ostatni zatopiony")){
                    System.out.println("Wygrana");
                    win=true;
                    break;
                }

                String myField=line.substring(line.indexOf(';')+1);
                myStatus=battleShipsUser.getFieldStatus(myField);

                if(myStatus.equals("ostatni zatopiony")){
                    System.out.println("Pzegrana");
                    send(myStatus+";\n");
                    win=false;
                    break;
                }

                String hisField=scanner.nextLine();
                send(myStatus+";"+hisField+"\n");
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Server(int port, BattleShipsUser battleShipsUser){
        try {
            this.battleShipsUser=battleShipsUser;
            serverSocket = new ServerSocket(port);
            scanner=new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
