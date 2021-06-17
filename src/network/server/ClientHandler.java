package network.server;

import Elements.Message;
import utils.SharedData;

import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;

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
    private boolean whileCondition;
    private volatile String command;// command or state
    private CountDownLatch latch;
    private SharedData sharedData;
    private String clientVote;

    public ClientHandler(Socket socket, ServerStarter server) {
        this.socket = socket;
        this.server = server;
        this.whileCondition = true;
        this.command = "hold";
        sharedData = SharedData.getInstance();

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

    /*
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
        }*/
    public void handleLogin() {
        try {
            printUsers();
            Message message = new Message("-", "gameManager", "all", "New user connected: " + userName);
//            String serverMessage = "New user connected: " + userName;
            server.broadcastWithExcludeUser(message, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        command = "hold";
        whileCondition = true;
    }


    public void run() {

        while (whileCondition) {
            if (command.equals("login")) {
                /*printUsers();
                Message message = new Message("-", "gameManager", "all", "New user connected: " + userName);
//            String serverMessage = "New user connected: " + userName;
                server.broadcastWithExcludeUser(message, this);
                command = "hold";*/
                whileCondition = false;
                handleLogin();

            }
            //Thread.currentThread().interrupt();//stop the thread in interrupt exception
            else if (command.equals("chatroom")) {

                whileCondition = false;
                chatMode();
                latch.countDown();
                command = "hold";

            } else if (command.equals("mafiaChat")) {

                whileCondition = false;
                mafiaChat();
                latch.countDown();
                command = "hold";

            } else if (command.equals("?vote")) {

                whileCondition = false;
                voteHandler();
                latch.countDown();
                command = "hold";

            }


        }

//            Message clientMessage;
//            Message message;
//            String clientMessage;
           /* Message clientMessage;
            objectOutputStream.writeObject(new Message("chatroom", "clientHandler", "all", "chatroom"));
            do {
//                clientMessage = reader.readLine();
                clientMessage = (Message) objectInputStream.readObject();
//                clientMessage = (String) objectInputStream.readObject();
                if (clientMessage.getTopic().equals("-")) {
                    server.broadcastWithExcludeUser(clientMessage, this);
                }
//                message = new Message("-", "server", "all", "[" + userName + "]: " + clientMessage);
//                server.broadcastWithExcludeUser(message, this);
                else if (clientMessage.getTopic().equals("vote")) {
                    System.out.println("this is test from clientHandler chat mode");
                }
            } while (!clientMessage.equals("bye"));

            //server.removeUser(userName, this);
            socket.close();

            //message = new Message("-", "server", "all",  userName + " has quited.");
//            serverMessage = userName + " has quited.";
//            server.broadcast(serverMessage, this);
            //server.broadcastWithExcludeUser(message, this);

        }
*/
    }

    public void chatMode() {
        try {
            Message message;
//            String clientMessage;
            Message clientMessage;
            long t = System.currentTimeMillis();
            long end = t + 30000;
            do {
                //objectOutputStream.writeObject(new Message("chatroom", "clientHandler", "all", "chatroom"));
//                clientMessage = reader.readLine();
                clientMessage = (Message) objectInputStream.readObject();
//                clientMessage = (String) objectInputStream.readObject();
                if (clientMessage.getTopic().equals("msg")) {
                    message = new Message("-", "server", "all", "[" + userName + "]: " + clientMessage.getText());
                    server.broadcastWithExcludeUser(message, this);
                }
//                message = new Message("-", "server", "all", "[" + userName + "]: " + clientMessage);
//                server.broadcastWithExcludeUser(message, this);
               /* else if (clientMessage.getTopic().equals("vote")) {
                    System.out.println("this is test from clientHandler chat mode");
                }*/
            } while (System.currentTimeMillis() < end);
            //broadcast
            sendMessage(new Message("bye", userName, "gameManager", "end of the loop"));
            whileCondition = true;
            System.out.println("im here");
            //server.removeUser(userName, this);
            //message = new Message("-", "server", "all",  userName + " has quited.");
//            serverMessage = userName + " has quited.";
//            server.broadcast(serverMessage, this);
            //server.broadcastWithExcludeUser(message, this);

        } catch (IOException ex) {
            System.out.println("Error in ClientHandler: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void voteHandler() {
        try {

            Message clientMessage;
            clientMessage = (Message) objectInputStream.readObject();

            clientVote = clientMessage.getText();

            String txt = userName + " vote " + clientVote;
            server.broadcast(new Message("-", userName, "all", txt));

            server.broadcast(new Message("bye", userName, "gameManager", "end of the loop"));
            whileCondition = true;

        } catch (IOException ex) {
            System.out.println("Error in ClientHandler: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
            objectOutputStream.writeObject(new Message("-", "gameManager", "all", "No other users connected"));

        }
    }

    public void mafiaChat() {
        try {
            String txt = "Mafia team open your eyes and write down the message";
            server.broadcast(new Message("mafiaChat", "gameManager", "Mafias", txt));
            Message message;
            Message clientMessage;

            long t = System.currentTimeMillis();
            long end = t + 30000;

            do {
                clientMessage = (Message) objectInputStream.readObject();

                if (clientMessage.getTopic().equals("msg")) {
                    message = new Message("-", "server", "all", "[" + userName + "]: " + clientMessage.getText());
                    server.broadcastAMessageViaAGroup(sharedData.mafiaClients, message, this);
                }

            } while (System.currentTimeMillis() < end);

            sendMessage(new Message("bye", userName, "gameManager", "end of the loop"));
            whileCondition = true;
            System.out.println("im here");

        } catch (IOException ex) {
            System.out.println("Error in ClientHandler: " + ex.getMessage());
            ex.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setWhileConditionFalse() {
        this.whileCondition = false;
    }

    public void setWhileConditionTrue() {
        this.whileCondition = true;
    }

    public String getClientVote() {
        return clientVote;
    }
}