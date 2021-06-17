package network.client;

import Elements.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;

public class ChatRead extends Thread {
    private ObjectInputStream objectInputStream;
    private Client client;

    public ChatRead(ObjectInputStream objectInputStream, Client client) {
        this.objectInputStream = objectInputStream;
        this.client = client;
    }

    public void run() {
        while (true) {
            //objectOutputStream.writeObject("update")
            try {
                Message msg = (Message) objectInputStream.readObject();
                if (msg.getTopic().equals("bye")) {
                    client.setChatResume(false);
                    break;
                }

                client.printMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // topic update send mikne
            //
        }
    }
}
