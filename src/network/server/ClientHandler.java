package network.server;

import Elements.Message;

import java.io.*;
import java.net.*;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private ServerStarter server;
    private PrintWriter writer;
    private String userName;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientHandler(Socket socket, ServerStarter server) {
        this.socket = socket;
        this.server = server;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

//            InputStream input = socket.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//
//            OutputStream output = socket.getOutputStream();
//            writer = new PrintWriter(output, true);
            userName = (String) objectInputStream.readObject();
//            userName = reader.readLine();
            server.addUserName(userName);

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error in ClientHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void run() {
        try {
            printUsers();
            Message message = new Message("-", "gameManager", "all", "New user connected: " + userName);
//            String serverMessage = "New user connected: " + userName;
            server.broadcastWithExcludeUser(message, this);
//            Message clientMessage;
            String clientMessage;
            do {
//                clientMessage = reader.readLine();
                clientMessage = (String) objectInputStream.readObject();
                message = new Message("-", "server", "all", "[" + userName + "]: " + clientMessage);
                server.broadcastWithExcludeUser(message, this);

            } while (!clientMessage.equals("bye"));

            server.removeUser(userName, this);
            socket.close();

            message = new Message("-", "server", "all",  userName + " has quited.");
//            serverMessage = userName + " has quited.";
//            server.broadcast(serverMessage, this);
            server.broadcastWithExcludeUser(message, this);

        } catch (IOException ex) {
            System.out.println("Error in ClientHandler: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void chatMode() {

    }


    /**
     * Sends a list of online users to the newly connected user.
     */
    public void printUsers() throws IOException {
        if (server.hasUsers()) {
            Message message = new Message("-", "gameManager", "all", "Connected users: " + server.getUserNames());
//            objectOutputStream.writeObject("Connected users: " + server.getUserNames());
//            writer.println("Connected users: " + server.getUserNames());
            objectOutputStream.writeObject(message);
        } else {
//            writer.println("No other users connected");
            objectOutputStream.writeObject("No other users connected");

        }
    }

    /**
     * Sends a message to the client.
     */
    public void sendMessage(Message message) {
//        writer.println(message);
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNAME() {
        return userName;
    }
}