package utils;

import Elements.Player;
import network.server.ClientHandler;
import network.server.ServerStarter;

import java.util.ArrayList;
import java.util.HashMap;

public class SharedData {

    public int playerCount;
    public Player[] players;
    public ArrayList<ClientHandler> clientHandlers;
    public ServerStarter server;
    private static SharedData instance;
    public HashMap<ClientHandler, Player> clientHandlerPlayerHashMap = new HashMap<>();
    public ArrayList<Player> removedPlayers;
    public HashMap<ClientHandler, String> clientHandlerVoteMap;

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }


}
