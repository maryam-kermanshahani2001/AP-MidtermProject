package network.server;

import gameLogic.GameStarter;

import java.util.Scanner;

public class ServerMain {
    private static GameStarter gameStarter;
    private static ServerStarter server;


    public static void main(String[] args){
        int port = 7652;
        System.out.println("enter the number of players");
        Scanner sc = new Scanner(System.in);
        int numberOfPlayers = sc.nextInt();
        server = ServerStarter.getInstance(port, numberOfPlayers);
        server.execute();
        System.out.println("I'm in ServerMain for test");
        gameStarter = new GameStarter();

    }
}
