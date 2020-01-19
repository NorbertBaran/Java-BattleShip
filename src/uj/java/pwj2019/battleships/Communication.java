package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Communication {
    protected Socket socket;
    protected BattleShipsUser battleShipsUser;
    protected boolean win;
    protected String myStatus;
    protected Scanner scanner;

    public void post(String content){
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(content);
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

    public boolean lastThrown(String status){
        return true;
    }

    public void endOfGame(){
        if(win){
            battleShipsUser.createWinMap();
            for(int i=0; i<10; i++){
                post(battleShipsUser.getWinMapLine(i)+"\n");
                get();
            }
        }else{
            for(int i=0; i<10; i++){
                post(battleShipsUser.getInitMapLine(i)+"\n");
                get();
            }
        }
        System.out.println();
        battleShipsUser.displayMap();
    }

    public boolean postAction(){
        if(myStatus.equals("ostatni zatopiony")){
            System.out.println("Pzegrana");
            post(myStatus+";\n");
            win=false;
            return false;
        }

        String hisField=scanner.nextLine();
        post(myStatus+";"+hisField+"\n");

        return true;
    }

    public boolean getAction(){
        String line = get();
        String hisStatus=line.substring(0, line.indexOf(';'));

        if(hisStatus.equals("ostatni zatopiony")){
            System.out.println("Wygrana");
            win=true;
            return false;
        }

        String myField=line.substring(line.indexOf(';')+1);
        myStatus=battleShipsUser.getFieldStatus(myField);

        return true;
    }

}
