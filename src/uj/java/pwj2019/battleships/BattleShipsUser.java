package uj.java.pwj2019.battleships;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BattleShipsUser {

    private Communication communication;
    private Integer port;
    private char[][] initMap;
    private char[][] map;
    private char[][] winMap;
    public int correctFieldCount;

    public static void main(String[] args){
        BattleShipsUser battleShipsUser=new BattleShipsUser(args);
        battleShipsUser.displayMap();
        battleShipsUser.run();
    }

    BattleShipsUser(String[] args){
        initGame(args);
    }

    private void run(){
        communication.run();
    }

    private void initGame(String[] args){

        Map<String, String> params=validParamsName(args);

        this.port=validPort(params.get("-port"));
        this.map=validMap(params.get("-map"));
        this.initMap=new char[10][10];
        clone(this.initMap, this.map);
        this.communication=validMode(params.get("-mode"));

    }

    private Map<String, String> validParamsName(String[] args){
        Map<String, String> params=new HashMap<>();
        if(args.length!=6){
            System.out.println("Bad params. Bad count of params.");
            System.exit(1);
        }
        for(int i=0; i<args.length; i=i+2){
            if(args[i].charAt(0)=='-' && i+1<args.length && args[i+1].charAt(0)!='-')
                params.put(args[i], args[i+1]);
            else{
                System.out.println("Bad params. You need define -mode, -port and -map params");
                System.exit(1);
            }
        }
        if(!params.containsKey("-mode") || !params.containsKey("-port") || !params.containsKey("-map")){
            System.out.println("Bad params. You need define -mode, -port and -map params");
            System.exit(1);
        }
        return params;
    }

    private Communication validMode(String modeName){
        Communication communication=null;
        if(modeName.equals("server"))
            communication=new Server(port, this);
        else if(modeName.equals("client"))
            communication=new Client(port, this);
        else{
            System.out.println("Bad params. Correct param: -mode[server|client]");
            System.exit(1);
        }
        return communication;
    }

    private Integer validPort(String port){
        Integer portNumber=null;
        try{
            portNumber=Integer.parseInt(port);
        }catch(Exception e){
            System.out.println("Bad params. Port need to be number");
            System.exit(1);
        }
        return portNumber;
    }

    private char[][] validMap(String mapSrc){
        char[][] map=null;
        File mapFile=new File(mapSrc);
        if(mapFile.exists() && mapFile.isFile())
            map=loadMap(mapFile);
        else{
            System.out.println("Bad params. File not exists or it is not file");
            System.exit(1);
        }
        return map;
    }

    private char[][] loadMap(File mapFile){
        char[][] map=null;
        try {
            map=new char[10][10];
            Scanner mapScanner = new Scanner(mapFile);
            for(int line=0; line<10; line++){
                if (mapScanner.hasNextLine()) {
                    String mapLine = mapScanner.nextLine();
                    if (mapLine.length() != 10) {
                        System.out.println("Map not correct");
                        mapScanner.close();
                        System.exit(2);
                    }
                    for(int i=0; i<10; i++)
                        map[line][i] = mapLine.charAt(i);
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
        return map;
    }

    public void displayMap(){
        for(int line=0; line<10; line++)
            System.out.println(map[line]);
    }

    public String getFieldStatus(String field){
        int column=field.charAt(0)-'A';
        int row=Integer.parseInt(field.substring(1));
        row--;
        if(map[row][column]=='#' || map[row][column]=='@'){
            if(map[row][column]=='#')
                correctFieldCount--;
            map[row][column]='@';
            if(correctFieldCount==0){
                return "ostatni zatopiony";
            }
            char[][] mapCopy=new char[10][10];
            clone(mapCopy, map);
            if(thrown(row, column, mapCopy))
                return "trafiony zatopiony";
            return "trafiony";
        }else{
            map[row][column]='~';
            return "pudło";
        }
    }

    private boolean thrown(int row, int column, char[][] mapCopy){
        mapCopy[row][column]='x';

        boolean zatopiony=true;

        if(column>0 && mapCopy[row][column-1]=='#')
            return false;
        if(column<9 && mapCopy[row][column+1]=='#')
            return false;
        if(row>0 && mapCopy[row-1][column]=='#')
            return false;
        if(row<9 && mapCopy[row+1][column]=='#')
            return false;

        if(column>0 && mapCopy[row][column-1]=='@')
            zatopiony= thrown(row, column-1, mapCopy);
        if(column<9 && mapCopy[row][column+1]=='@')
            zatopiony= thrown(row, column+1, mapCopy);
        if(row>0 && mapCopy[row-1][column]=='@')
            zatopiony= thrown(row-1, column, mapCopy);
        if(row<9 && mapCopy[row+1][column]=='@')
            zatopiony= thrown(row+1, column, mapCopy);

        return zatopiony;
    }

    String getInitMapLine(int line){
        return String.valueOf(initMap[line]);
    }

    public boolean thrownNeighbour(int i, int j){
        char[][] mapCopy=new char[10][10];
        clone(mapCopy, map);

        if(i>0 && map[i-1][j]=='@' && thrown(i-1, j, mapCopy))
            return true;
        if(i<9 && map[i+1][j]=='@' && thrown(i+1, j, mapCopy))
            return true;
        if(j>0 && map[i][j-1]=='@' && thrown(i, j-1, mapCopy))
            return true;
        if(j<9 && map[i][j+1]=='@' && thrown(i, j+1, mapCopy))
            return true;

        if(i>0 && j>0 && map[i-1][j-1]=='@' && thrown(i-1, j-1, mapCopy))
            return true;
        if(i>0 && j<9 && map[i-1][j+1]=='@' && thrown(i-1, j+1, mapCopy))
            return true;
        if(i<9 && j>0 && map[i+1][j-1]=='@' && thrown(i+1, j-1, mapCopy))
            return true;
        if(i<9 && j<9 && map[i+1][j+1]=='@' && thrown(i+1, j+1, mapCopy))
            return true;
        return false;
    }

    public void createWinMap(){
        winMap=new char[10][10];
        clone(winMap, map);
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                if(map[i][j]=='~')
                    winMap[i][j]='.';
                else if(map[i][j]=='@')
                    winMap[i][j]='#';
                else if(map[i][j]=='.'){
                    if(thrownNeighbour(i, j))
                        winMap[i][j]='.';
                    else
                        winMap[i][j]='?';
                }else
                    winMap[i][j]='?';
            }
        }
    }

    String getWinMapLine(int line){
        return String.valueOf(winMap[line]);
    }

    private void clone(char[][] to, char[][] from){
        for(int i=0; i<10; i++)
            for(int j=0; j<10; j++)
                to[i][j]=from[i][j];
    }

}
