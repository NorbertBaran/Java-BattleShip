package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

    BattleShipsUser battleShipsUser;

    Socket socket;
    Scanner scanner;

    @Override
    public void run() {
            try {
                String myStatus="start";

                boolean win;

                while(true) {
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    String hisField;
                    if(!myStatus.equals("ostatni zatopiony"))
                        hisField = scanner.nextLine();
                    else{
                        System.out.println("Pzegrana");
                        writer.println(myStatus+";\n");
                        win=false;
                        break;
                    }

                    writer.println(myStatus+";"+hisField+"\n");

                    String line = reader.readLine();
                    System.out.println(line);

                    String hisStatus=line.substring(0, line.indexOf(';'));

                    if(hisStatus.equals("ostatni zatopiony")){
                        System.out.println("Wygrana");
                        win=true;
                        break;
                    }

                    String myField=line.substring(line.indexOf(';')+1);
                    //System.out.println("His status: "+hisStatus);
                    //System.out.println("My field: "+myField);
                    myStatus=battleShipsUser.getFieldStatus(myField);
                    //System.out.println(battleShipsUser.correctFieldCount);
                    //battleShipsUser.displayMap();

                }

                if(win)
                    battleShipsUser.createWinMap();

                for(int i=0; i<10; i++){
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    if(!win)
                        writer.println(battleShipsUser.getInitMapLine(i)+"\n");
                    else
                        writer.println(battleShipsUser.getWinMapLine(i)+"\n");

                    String line = reader.readLine();
                    System.out.println(line);
                }

                System.out.println();

                battleShipsUser.displayMap();


            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    Client(int portNumber, BattleShipsUser battleShipsUser){
        try {
            this.battleShipsUser=battleShipsUser;
            socket = new Socket("localhost", portNumber);
            scanner=new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
