package uj.java.pwj2019.battleships;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

public class BattleShipsUser {

    private String modeName;
    private int portNumber;
    private File mapFile;

    private String[] map;
    private String[] initMap;
    private String[] winMap;

    public int correctFieldCount;
    private Thread modeThread;

    public BattleShipsUser(String[] args){
        setStartParams(args);
        loadMap();
        setMode();
        initMap=map.clone();
    }

    private void setStartParams(String[] args){
        if(args.length!=6){
            System.out.println("Bad params. Too few params");
            System.exit(1);
        }

        var params=new HashMap<String, String>();

        for(int i=0; i<args.length; i=i+2){
            if(args[i].charAt(0)=='-' && i+1<args.length && args[i+1].charAt(0)!='-')
                params.put(args[i], args[i+1]);
        }

        if(params.containsKey("-mode") && params.containsKey("-port") && params.containsKey("-map")) {

            modeName =params.get("-mode");
            if(!modeName.equals("server") && !modeName.equals("client")){
                System.out.println("Bad params. Correct param: -mode[server|client]");
                System.exit(1);
            }

            try{
                portNumber =Integer.parseInt(params.get("-port"));
            }catch(Exception e){
                System.out.println("Bad params. Port need to be number");
                System.exit(1);
            }

            mapFile =new File(params.get("-map"));
            if(!mapFile.exists() || !mapFile.isFile()){
                System.out.println("Bad params. File not exists or it is not file");
                System.exit(1);
            }

        }else{
            System.out.println("Bad params. You need define -mode, -port and -map params");
            System.exit(1);
        }
    }

    private void loadMap(){
        try {
            map=new String[10];
            Scanner mapScanner = new Scanner(mapFile);
            int mapLineNr=0;
            for(int line=0; line<10; line++){
                if (mapScanner.hasNextLine()) {
                    String mapLine = mapScanner.nextLine();
                    if (mapLine.length() != 10) {
                        System.out.println("Map not correct");
                        mapScanner.close();
                        System.exit(2);
                    }
                    map[mapLineNr] = mapLine;
                    mapLineNr++;
                }else{
                    System.out.println("Map not correct");
                    mapScanner.close();
                    System.exit(2);
                }
            }
            if(mapScanner.hasNextLine() && mapScanner.nextLine().length()>0){
                System.out.println("Map not correct");
                mapScanner.close();
                System.exit(2);
            }
            mapScanner.close();
            correctFieldCount=20;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setMode(){
        if(modeName.equals("server")){
            Server server = new Server(portNumber, this);
            modeThread=new Thread(server, "battleShipsServer");
            modeThread.start();
        }else{
            Client client = new Client(portNumber, this);
            modeThread=new Thread(client, "battleShipsClient");
            modeThread.start();
        }
    }

    public void displayMap(){
        for(int line=0; line<10; line++)
            System.out.println(map[line]);
    }

    public String getFieldStatus(String field){
        int column=field.charAt(0)-'A';
        int row=Integer.parseInt(field.substring(1));
        row--;
        if(map[row].charAt(column)=='#' || map[row].charAt(column)=='@'){
            if(map[row].charAt(column)=='#')
                correctFieldCount--;
            map[row]=map[row].substring(0, column)+'@'+map[row].substring(column+1);
            if(correctFieldCount==0){
                return "ostatni zatopiony";
            }
            String[] mapCopy=map.clone();
            if(trafionyZatopiony(row, column, mapCopy))
                return "trafiony zatopiony";
            return "trafiony";
        }else{
            map[row]=map[row].substring(0, column)+'~'+map[row].substring(column+1);
            return "pudło";
        }
    }

    private boolean trafionyZatopiony(int row, int column, String[] mapCopy){
        mapCopy[row]=mapCopy[row].substring(0, column)+'x'+mapCopy[row].substring(column+1);

        boolean zatopiony=true;

        if(column>0 && mapCopy[row].charAt(column-1)=='#')
            return false;
        if(column<9 && mapCopy[row].charAt(column+1)=='#')
            return false;
        if(row>0 && mapCopy[row-1].charAt(column)=='#')
            return false;
        if(row<9 && mapCopy[row+1].charAt(column)=='#')
            return false;

        if(column>0 && mapCopy[row].charAt(column-1)=='@')
            zatopiony=trafionyZatopiony(row, column-1, mapCopy);
        if(column<9 && mapCopy[row].charAt(column+1)=='@')
            zatopiony=trafionyZatopiony(row, column+1, mapCopy);
        if(row>0 && mapCopy[row-1].charAt(column)=='@')
            zatopiony=trafionyZatopiony(row-1, column, mapCopy);
        if(row<9 && mapCopy[row+1].charAt(column)=='@')
            zatopiony=trafionyZatopiony(row+1, column, mapCopy);

        return zatopiony;
    }

    String getInitMapLine(int line){
        return initMap[line];
    }

    public boolean trafionyZatopionySasiad(int i, int j){
        String[] mapCopy=map.clone();

        if(i>0 && map[i-1].charAt(j)=='@' && trafionyZatopiony(i-1, j, mapCopy))
            return true;
        if(i<9 && map[i+1].charAt(j)=='@' && trafionyZatopiony(i+1, j, mapCopy))
            return true;
        if(j>0 && map[i].charAt(j-1)=='@' && trafionyZatopiony(i, j-1, mapCopy))
            return true;
        if(j<9 && map[i].charAt(j+1)=='@' && trafionyZatopiony(i, j+1, mapCopy))
            return true;

        if(i>0 && j>0 && map[i-1].charAt(j-1)=='@' && trafionyZatopiony(i-1, j-1, mapCopy))
            return true;
        if(i>0 && j<9 && map[i-1].charAt(j+1)=='@' && trafionyZatopiony(i-1, j+1, mapCopy))
            return true;
        if(i<9 && j>0 && map[i+1].charAt(j-1)=='@' && trafionyZatopiony(i+1, j-1, mapCopy))
            return true;
        if(i<9 && j<9 && map[i+1].charAt(j+1)=='@' && trafionyZatopiony(i+1, j+1, mapCopy))
            return true;
        return false;
    }

    public void createWinMap(){
        winMap=map.clone();
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                if(map[i].charAt(j)=='~')
                    winMap[i]=winMap[i].substring(0, j)+'.'+winMap[i].substring(j+1);
                else if(map[i].charAt(j)=='@')
                    winMap[i]=winMap[i].substring(0, j)+'#'+winMap[i].substring(j+1);
                else if(map[i].charAt(j)=='.'){
                    if(trafionyZatopionySasiad(i, j))
                        winMap[i]=winMap[i].substring(0, j)+'.'+winMap[i].substring(j+1);
                    else
                        winMap[i]=winMap[i].substring(0, j)+'?'+winMap[i].substring(j+1);
                }else
                    winMap[i]=winMap[i].substring(0, j)+'?'+winMap[i].substring(j+1);
            }
        }
    }

    String getWinMapLine(int line){
        return winMap[line];
    }

}
