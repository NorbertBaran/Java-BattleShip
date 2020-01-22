package uj.java.pwj2019.battleships;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Communication {
    Socket socket;
    private BattleShipsUser battleShipsUser;
    private boolean win;
    private String myStatus;
    private Scanner scanner;

    private int postRepeat;
    private String lastMessage;

    private String hisStatus;
    protected String myField;

    private List statusList;
    private String line;

    Communication(BattleShipsUser battleShipsUser){
        this.battleShipsUser=battleShipsUser;
        this.myStatus="start";
        this.hisStatus="start";
        this.scanner=new Scanner(System.in);
        this.postRepeat =1;
        this.lastMessage="start";
        this.statusList=List.of("start", "pudło", "trafiony", "trafiony zatopiony", "ostatni zatopiony");
    }

    public abstract void run();

    private void post(String content){
        try {
            this.lastMessage=content;
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String get(){
        String line;
        try {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    throw new Exception("Server not respond.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            line=reader.readLine();
        } catch (IOException e) {
            return "mistake";
        }
        if(line!=null)
            System.out.println(line);
        return line;
    }

    void endOfGame(){
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

    boolean postAction(){
        if(myStatus.equals("mistake")){
            if(postRepeat>=3){
                System.out.println("Błąd komunikacji");
                System.exit(0);
            }
            postRepeat++;
            post(lastMessage);
            return true;
        }
        postRepeat=1;
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

    boolean validGetAction(){
        try {
            line=get();
            if(line.equals("mistake"))
                return false;
            hisStatus=line.substring(0, line.indexOf(';'));
            myField=line.substring(line.indexOf(';')+1);
            if(!statusList.contains(hisStatus))
                return false;
            int column=myField.charAt(0)-'A';
            int row=Integer.parseInt(myField.substring(1))-1;
            if(column>=0 && column<=9 && row>=0 && row<=9)
                return true;
            return false;
        }catch (Exception e){
            return false;
        }
    }

    boolean getAction(){
        if(hisStatus.equals("ostatni zatopiony")){
            System.out.println("Wygrana");
            win=true;
            return false;
        }
        myStatus=battleShipsUser.getFieldStatus(myField);
        return true;
    }
}
