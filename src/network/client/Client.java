package network.client;

import Elements.Message;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.IntFunction;

public class Client {
    private String hostname;
    private int port;
    private String userName;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Socket socket;
    private Scanner sc;
    private boolean chatResume = true;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        sc = new Scanner(System.in);
        try {
            Socket socket = new Socket(hostname, port);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void execute() {

        getNameFromClient();
        try {
            System.out.println("Connected to the chat server");
            while (true) {
                Message cmd = (Message) objectInputStream.readObject();
                switch (cmd.getTopic()) {
                    case "?vote":
                        System.out.println(cmd.getTopic());
                        askClientForVote(cmd);
                        break;

                    case "-":
                        printMessage(cmd);
                        break;

                    case "chatroom":
                        System.out.println(cmd.getTopic());
                        chatroomMode();
                        break;

                    case "mafiaChat":
                        System.out.println(cmd.getTopic());
                        mafiaChatRoom();
                        break;
                    case "ravanShenasMode":
                        System.out.println(cmd.getTopic());
                        ravanShenasMode();
                        break;

                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void printMessage(Message msg) {
        System.out.println(msg.toString());
    }

    public void getNameFromClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter your name: ");
        userName = sc.nextLine();
        try {
            objectOutputStream.writeObject(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getUserName() {
        return userName;
    }

    public void askClientForVote(Message msg) {
        System.out.println(msg.getText());
        String reply;
        try {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("enter your vote");
                reply = sc.nextLine();
                String[] tokens = reply.split(" ", 2);
                String topic = tokens[0];
                if (topic.equals("vote")) {
                    reply = tokens[1];
                    break;
                }
            }
            Message message = new Message("vote", userName, "gameManager", reply);
            objectOutputStream.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void chatroomMode() {
        chatResume = true;
        ChatRead chatRead = new ChatRead(objectInputStream, this);
        chatRead.start();
        while (chatResume) {
            // update method -> in case 3 in while loop run and new thread for receiving msg
            // update start
            // a class with while loop fo
            if (!chatResume)
                continue;
            String line = sc.nextLine();
            if (!chatResume) {
                /*try {
                    objectOutputStream.writeObject(new Message("-", userName, "gameManager", "ending"));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                continue;
            }

            String[] tokens = line.split(" ", 2);
            String topic = tokens[0];
            Message message;
            //*Message response;
            //String resp;//*
            try {
                if ("msg".equals(topic)) {
                    message = new Message(topic, userName, "all", tokens[1]);
                    objectOutputStream.writeObject(message);

                    //case "vote":
                       /* message = new Message(topic, userName, "gameManager", tokens[1]);
                        objectOutputStream.writeObject(message);*/
                } else {
                    objectOutputStream.writeObject(new Message(topic, userName, "all", tokens[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        try {
        //objectOutputStream.writeObject(new Message("bye", userName, "gameManager", "end of the loop"));
        System.out.println("End of the chat");

//        } catch (IOException e) {
        //       e.printStackTrace();
        //  }
    }

    public void mafiaChatRoom() {

        chatResume = true;
        ChatRead chatRead = new ChatRead(objectInputStream, this);
        chatRead.start();

        while (chatResume) {
            if (!chatResume)
                continue;
            String line = sc.nextLine();
            if (!chatResume) {
                /*try {
                    objectOutputStream.writeObject(new Message("-", userName, "gameManager", "ending"));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                continue;
            }

            String[] tokens = line.split(" ", 2);
            String topic = tokens[0];
            Message message;

            try {
                if ("msg".equals(topic)) {
                    message = new Message(topic, userName, "mafias", tokens[1]);
                    objectOutputStream.writeObject(message);

                } else {
                    objectOutputStream.writeObject(new Message(topic, userName, "all", tokens[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("End of the chat");
    }

    public void ravanShenasMode() {
        while (true) {
            //objectOutputStream.writeObject("update")
            try {
                Message msg = (Message) objectInputStream.readObject();
                if (msg.getTopic().equals("bye")) {
                    break;
                }

                printMessage(msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();

            }
            System.out.println("End of the chat");
        }

    }

    public static void main(String[] args) {


        String hostname = "localhost";
        System.out.println("Enter the port of the server that you want to connect");
        Scanner sc = new Scanner(System.in);
        int port = sc.nextInt();

        Client client = new Client(hostname, port);
        client.execute();
    }

    public boolean isChatResume() {
        return chatResume;
    }

    public void setChatResume(boolean chatResume) {
        this.chatResume = chatResume;
    }
}
