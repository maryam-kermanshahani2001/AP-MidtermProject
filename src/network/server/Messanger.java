package network.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Messanger {

    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    public Messanger(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }
    public void mafiaChatRoom() {

    }
}
