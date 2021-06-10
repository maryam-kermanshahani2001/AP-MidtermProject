package gameLogic;

import Elements.Character;
import Elements.Message;
import Elements.Player;
import network.server.ClientHandler;
import utils.SharedData;

import java.util.ArrayList;

public class GameManager {
    private SharedData sharedData;

    public GameManager() {
        sharedData = SharedData.getInstance();
    }

    public void firstNightRun() {
        ArrayList<ClientHandler> mafiaClients = new ArrayList<>();
        ArrayList<Player> mafiaPlayers = new ArrayList<>();

        for (int i = 0; i < sharedData.players.length; i++) {
            if (sharedData.players[i].isMafia()) {
                mafiaPlayers.add(sharedData.players[i]);
                mafiaClients.add(sharedData.clientHandlers.get(i));
            }
        }

        for (int i = 0; i < mafiaClients.size(); i++) {
            String txtOfMessage = mafiaClients.get(i).getNAME() + " is " + mafiaPlayers.get(i).getCHARACTER();
            Message msg = new Message("-", "gameManager", "Mafias", txtOfMessage);
            sharedData.server.broadcastAMessageViaAGroup(mafiaClients, msg, mafiaClients.get(i));
        }

        int flag = 1;
        for (int i = 0; i < sharedData.playerCount; i++) {
            if (flag == 0)
                break;
            if (sharedData.players[i].getCHARACTER() == Character.SHAHRDAR)
                for (int j = 0; j < sharedData.playerCount; j++) {
                    if (sharedData.players[j].getCHARACTER() == Character.DOCTORSHAHR) {
                        String txtOfMessage = sharedData.players[j].getNAME() + " is " + "DRSHAHR";
                        Message msg = new Message("-", "gameManager", "SHAHRDAR", txtOfMessage);
                        sharedData.clientHandlers.get(i).sendMessage(msg);
                        flag = 0;
                        break;
                    }

                }

        }

    }

    public void firstDayRun() {
        int index = sharedData.removedPlayers.size();

        for (int i = 0; i < sharedData.playerCount; i++) {
            String text = sharedData.removedPlayers.get(index - 1).getNAME() + " removed last night";
            Message message = new Message("-", "gameManager", "all", text);
            sharedData.server.broadcast(message);
            text = "chatroom mode";
            message = new Message("-", "gameManager", "all", text);
            sharedData.server.broadcast(message);
            sharedData.clientHandlers.get(i).sendMessage(message);
        }
    }
}
