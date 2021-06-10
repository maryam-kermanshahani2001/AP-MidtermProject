package network.server;

import Elements.Message;
import utils.SharedData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServerStarter {
    private int port;
    private int numberOfPlayers;
    private static ServerStarter serverStarter;
    private SharedData sharedData;

    ///////do we nneed userNames?
    private Set<String> userNames = new HashSet<>();

    private ServerStarter(int port, int numberOfPlayers) {
        // private constructor
        this.port = port;
        this.numberOfPlayers = numberOfPlayers;
        sharedData = SharedData.getInstance();
        sharedData.clientHandlers = new ArrayList<>();
    }

    //method to return serverStarter of class
    public static ServerStarter getInstance(int port, int numberOfPlayers) {
        if (serverStarter == null) {
            // if serverStarter is null, initialize
            serverStarter = new ServerStarter(port, numberOfPlayers);
        }
        return serverStarter;
    }

    ////////////////////////////////////////////////////////is it correct check it make it better
    public ArrayList<ClientHandler> getClientHandlers() {
        return sharedData.clientHandlers;
    }
////////////////////////////////////////////////////////

    public void execute() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            sharedData.server = serverStarter;

            System.out.println("Chat Server is listening on port " + port);
            sharedData.playerCount = numberOfPlayers;

            for (int i = 0; i < numberOfPlayers; i++) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                ClientHandler newUser = new ClientHandler(socket, this);
                sharedData.clientHandlers.add(newUser);
                //serverSocket.accept
                newUser.start();


            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    public void broadcastWithExcludeUser(Message message, ClientHandler excludeUser) {
        for (ClientHandler aUser : sharedData.clientHandlers) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    public void broadcastAMessageViaAGroup(ArrayList<ClientHandler> clientGroups, Message message, ClientHandler excludeUser) {
        for (ClientHandler aUser : clientGroups) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }

    }

    public void broadcast(Message message) {
        for (ClientHandler aUser : sharedData.clientHandlers) {
            aUser.sendMessage(message);
        }
    }


    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconneted, removes the associated username and ClientHandler
     */
    void removeUser(String userName, ClientHandler aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            sharedData.clientHandlers.remove(aUser);
            System.out.println("The user " + userName + " quited");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }


}
