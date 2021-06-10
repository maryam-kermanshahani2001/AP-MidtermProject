package network.client;

import Elements.Message;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String hostname;
    private int port;
    private String userName;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Socket socket;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
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
                        askClientForVote(cmd);
                        break;
                    case "-":
                        printMessage(cmd);
                        break;
                    case "chatroom":


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
            reply = sc.nextLine();
            objectOutputStream.write(reply.getBytes());
            //            String[] tokens = line.split("");
//            Message
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void chatroomMode() {

//        ChatRead cl = new ChatRead(objectOutputStream, objectInputStream);
//        cl.start();
        long t = System.currentTimeMillis();
        long end = t + 240000;
        while (System.currentTimeMillis() < end) {
            // update method -> in case 3 in while loop run and new thread for receiving msg
            // update start
            // a class with while loop fo
            ChatRead chatRead = new ChatRead(objectInputStream, this);
            chatRead.start();
            Scanner sc = new Scanner(System.in);
            String line;
//            if ((line = sc.nextLine()) == null) {
//
//            }
            line = sc.nextLine();
            String[] tokens = line.split(" ", 2);
            String topic = tokens[0];
            Message message;
            Message response;
            String resp;
            try {
                switch (topic) {

                    case "msg":
                        message = new Message(topic, userName, " all", tokens[1]);
                        objectOutputStream.writeObject(message);

                        break;
                    case "vote":
                        message = new Message(topic, userName, "gameManager", tokens[1]);
                        objectOutputStream.writeObject(message);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
//
//            try {
//                objectOutputStream.writeObject(msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }

    public void update() {

    }

    public static void main(String[] args) {


        String hostname = "localhost";
        System.out.println("Enter the port of the server that you want to connect");
        Scanner sc = new Scanner(System.in);
        int port = sc.nextInt();

        Client client = new Client(hostname, port);
        client.execute();
    }
}
