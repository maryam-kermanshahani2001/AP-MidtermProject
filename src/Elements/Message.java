package Elements;

import java.io.Serializable;

public class Message implements Serializable {
    private final String topic;
    private final String sender;
    private final String receiver;
    private final String text;

    public Message(String topic, String sender, String receiver, String text) {
        this.topic = topic;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
    }

    public String getTopic() {
        return topic;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getText() {
        return text;
    }


    @Override
    public String toString() {
        return "Message :\n" +
                "topic = " + topic + ' ' +
                " sender= " + sender + ' ' +
                " receiver = " + receiver + "\n" +
                " text = " + text;
    }


    public String txtToString() {
        return "text " + this.text;
    }
}
