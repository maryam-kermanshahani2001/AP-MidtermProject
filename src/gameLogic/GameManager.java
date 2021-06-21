package gameLogic;

import Elements.Character;
import Elements.Message;
import Elements.Player;
import network.server.ClientHandler;
import utils.SharedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.function.IntFunction;

public class GameManager {
    private SharedData sharedData;
    private Boolean gameResume = true;
    private CountDownLatch latch;
    private GameState gameState;

    public GameManager() {
        sharedData = SharedData.getInstance();
        gameState = GameState.getInstance();
    }

    public void firstNightRun() {
        ArrayList<ClientHandler> mafiaClients = new ArrayList<>();
        ArrayList<Player> mafiaPlayers = new ArrayList<>();

        sharedData.citizenClients = new ArrayList<>();
        sharedData.citizenPlayers = new ArrayList<>();


        for (int i = 0; i < sharedData.players.size(); i++) {
            if (sharedData.players.get(i).isMafia()) {
                mafiaPlayers.add(sharedData.players.get(i));
                mafiaClients.add(sharedData.clientHandlers.get(i));
            } else {
                sharedData.citizenPlayers.add(sharedData.players.get(i));
                sharedData.citizenClients.add(sharedData.clientHandlers.get(i));
            }
        }
        sharedData.mafiaClients = mafiaClients;
        sharedData.mafiaPlayers = mafiaPlayers;


        for (int i = 0; i < mafiaClients.size(); i++) {
            String txtOfMessage = mafiaClients.get(i).getNAME() + " is " + mafiaPlayers.get(i).getCHARACTER();
            Message msg = new Message("-", "gameManager", "Mafias", txtOfMessage);
            sharedData.server.broadcastAMessageViaAGroup(mafiaClients, msg, mafiaClients.get(i));
        }

        int flag = 1;
        for (int i = 0; i < sharedData.playerCount; i++) {
            if (flag == 0)
                break;
            if (sharedData.players.get(i).getCHARACTER() == Character.SHAHRDAR)
                for (int j = 0; j < sharedData.playerCount; j++) {
                    if (sharedData.players.get(j).getCHARACTER() == Character.DOCTORSHAHR) {
                        String txtOfMessage = sharedData.players.get(j).getNAME() + " is " + "DRSHAHR";
                        Message msg = new Message("-", "gameManager", "SHAHRDAR", txtOfMessage);
                        sharedData.clientHandlers.get(i).sendMessage(msg);
                        flag = 0;
                        break;
                    }

                }

        }

    }

    public void firstDayRun() {
        String text;
        Message message;

        text = "Hello Everyone . welcome to the game";
        message = new Message("-", "gameManager", "all", text);

        text = "chatroom mode";
        message = new Message("chatroom", "gameManager", "all", text);
        sharedData.server.broadcast(message);
        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setCommand("chatroom");
            //sharedData.clientHandlers.get(i).start();
        }
        // while with thread sleep
//        CountDownLatch latch = new CountDownLatch(sharedData.playerCount);

        latch = new CountDownLatch(sharedData.playerCount);

        // latch = new CountDownLatch(sharedData.playerCount);

        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setLatch(latch);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("hello everyone");
        // the program should be stop here ? how ? with wait may be but how?

        // in set kardane bbin b moshkel mikhore ya na ; chon in dre ono set mikne onvar motmaen nistm k set mishe o vared if mishe ya na
        // nmishe az inja chatMode ro seda krd?  frqi mikne on run e tmum beshe?
        System.out.println("");

        text = "please enter your vote like above \n" + " vote <username> ";
        message = new Message("?vote", "gameManager", "all", text);
        sharedData.server.broadcast(message);


        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setCommand("?vote");
            //sharedData.clientHandlers.get(i).start();
        }

        latch = new CountDownLatch(sharedData.playerCount);
        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setLatch(latch);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HashMap<String, Integer> playerVoteMap = calculateVote();
        int max = -1;
        String playerName = "";
        for (HashMap.Entry<String, Integer> entry : playerVoteMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                playerName = entry.getKey();
            }
        }

        sharedData.server.broadcast(new Message("-", "gameManager", "all", " player " + playerName + " good bye ; you removed"));
        gameState.removedPlayers = new ArrayList<>();
        removePlayer(playerName);

        gameState.isJansakhtAsk = false;
        gameState.ravanshenasSilenced = false;
        gameState.voteCancelByShahrdar = false;
    }

    private void printRemovedPlayers() {
        int index = gameState.removedPlayers.size();
        String text;
        Message message;

        if (index >= 1) {
            text = gameState.removedPlayers.get(index - 1).getNAME() + " removed last night";
            message = new Message("-", "gameManager", "all", text);
            sharedData.server.broadcast(message);
        }
        if (index == 0) {
            text = gameState.removedPlayers.get(index - 1).getNAME() + "No one removed till now";
            message = new Message("-", "gameManager", "all", text);
            sharedData.server.broadcast(message);
        }
    }

    private void printRemovedCharacters() {
        int index = gameState.removedPlayers.size();
        String text;
        Message message;

        if (index >= 1) {
            for (Player p : gameState.removedPlayers) {
                text = p.getCHARACTER() + " removed last night";
                message = new Message("-", "gameManager", "all", text);
                sharedData.server.broadcast(message);
            }
        } else {
            text = gameState.removedPlayers.get(index - 1).getNAME() + "No one removed till now";
            message = new Message("-", "gameManager", "all", text);
            sharedData.server.broadcast(message);
        }
    }

    public void dayRun() {
        String text;
        Message message;

        printRemovedPlayers();

        if (gameState.isJansakhtAsk) {
            printRemovedCharacters();
        }


        ClientHandler silentClient = null;
        if (gameState.ravanshenasSilenced) {
            for (ClientHandler cl : sharedData.clientHandlers) {
                if (cl.getNAME().equals(gameState.ravanShenasVote)) {
                    silentClient = cl;
                    break;
                }
            }
        }

        // sharedData.server.broadcastWithExcludeUser(message, silentClient);

        if (gameState.ravanshenasSilenced && silentClient != null) {
            for (int i = 0; i < sharedData.playerCount; i++) {
                if (silentClient.getNAME().equals(sharedData.clientHandlers.get(i).getNAME())) {
                    sharedData.clientHandlers.get(i).setCommand("ravanShenasChatMode");
                    continue;
                }
                sharedData.clientHandlers.get(i).setCommand("chatroom");
            }
            //sharedData.clientHandlers.get(i).start();
        } else {
            for (int i = 0; i < sharedData.playerCount; i++) {
                sharedData.clientHandlers.get(i).setCommand("chatroom");
            }
            //sharedData.clientHandlers.get(i).start();
        }


        // while with thread sleep
//        CountDownLatch latch = new CountDownLatch(sharedData.playerCount);
        /*if (gameState.ravanshenasSilenced) {
            latch = new CountDownLatch(sharedData.playerCount - 1);
            if (silentClient != null) {
                silentClient.sendMessage(new Message("-", "gameManager", silentClient.getNAME(), "you cant chat"));
            }

        } else {
            latch = new CountDownLatch(sharedData.playerCount);
        }
*/
        latch = new CountDownLatch(sharedData.playerCount);

        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setLatch(latch);
        }

        try {
            latch.await();
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("hello everyone");
        // the program should be stop here ? how ? with wait may be but how?

        // in set kardane bbin b moshkel mikhore ya na ; chon in dre ono set mikne onvar motmaen nistm k set mishe o vared if mishe ya na
        // nmishe az inja chatMode ro seda krd?  frqi mikne on run e tmum beshe?
        System.out.println("");

        text = "please enter your vote like above \n" + " vote <username> ";
        message = new Message("?vote", "gameManager", "all", text);
        sharedData.server.broadcast(message);


        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setCommand("?vote");
            //sharedData.clientHandlers.get(i).start();
        }

        latch = new CountDownLatch(sharedData.playerCount);
        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setLatch(latch);
        }
        try {
            latch.await();
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }

        if (isExist(Character.SHAHRDAR)) {
            int shahrDarIndex = findCharacter(Character.SHAHRDAR);
            shahrdarTurn(shahrDarIndex);
        }

        if (!gameState.voteCancelByShahrdar) {
            HashMap<String, Integer> playerVoteMap = calculateVote();
            int max = -1;
            String playerName = "";
            for (HashMap.Entry<String, Integer> entry : playerVoteMap.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    playerName = entry.getKey();

                }

            }
            sharedData.server.broadcast(new Message("-", "gameManager", "all", " player " + playerName + " good bye ; you removed"));
            removePlayer(playerName);
        }


        gameState.isJansakhtAsk = false;
        gameState.ravanshenasSilenced = false;
        gameState.voteCancelByShahrdar = false;
    }

    public void nightRun() {
        gameState.ravanShenasVote = "";
        String txtOfMessage;

        int godFatherIndex = -1;
        int drShahrIndex = -1;
        int herfeiIndex = -1;
        int ravanShenasIndex = -1;
        int mafiaIndex = -1;
        int drLectorIndex = -1;
        int jansakhtIndex = -1;
        int karagahIndex = -1;

        String drShahrVote;
        String mafiaVote;


        sharedData.server.broadcast(new Message("-", "gameManager", "all", "close your eyes"));

        for (int i = 0; i < sharedData.playerCount; i++) {
            if (sharedData.players.get(i).isMafia()) {
                sharedData.clientHandlers.get(i).setCommand("mafiaChat");
            }

        }
        // while with thread sleep
        latch = new CountDownLatch(sharedData.mafiaPlayers.size());
        for (int i = 0; i < sharedData.playerCount; i++) {
            if (sharedData.players.get(i).isMafia()) {
                sharedData.clientHandlers.get(i).setLatch(latch);
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        godFatherIndex = findCharacter(Character.GODFATHER);
        mafiaIndex = findCharacter(Character.MAFIA);
        drLectorIndex = findCharacter(Character.DRLECTOR);

        herfeiIndex = findCharacter(Character.HERFEI);
//drshahr
        jansakhtIndex = findCharacter(Character.JANSAKHT);
        ravanShenasIndex = findCharacter(Character.RAVANSHENAS);
        karagahIndex = findCharacter(Character.KARAGAH);

        int mafiaGpIndex;
        if (isExist(Character.GODFATHER)) {
            mafiaVote = godFatherTurn(godFatherIndex);
            mafiaGpIndex = godFatherIndex;
        } else if (isExist(Character.MAFIA)) {
            mafiaVote = mafiaGroupTurn(mafiaIndex);
            mafiaGpIndex = godFatherIndex;
        } else {
            mafiaVote = mafiaGroupTurn(drLectorIndex);
            mafiaGpIndex = godFatherIndex;
        }

      /*  if (mafiaVote.equals(sharedData.players.get(findCharacter(Character.JANSAKHT)).getNAME())) {

        }*/
        if (isExist(Character.DOCTORSHAHR)) {
            drShahrIndex = findCharacter(Character.DOCTORSHAHR);
            drShahrVote = drShahrTurn(drShahrIndex);
            if (drShahrVote.equals("me")) {
                if (mafiaVote.equals(sharedData.players.get(findCharacter(Character.JANSAKHT)).getNAME())) {
                    if (gameState.jansakhtLives == 0)
                        removePlayer(mafiaVote);
                    else if (gameState.jansakhtLives == 1 || gameState.jansakhtLives == 2) {
                        gameState.jansakhtLives--;
                    }
                } else if ((!mafiaVote.equals(sharedData.players.get(drShahrIndex).getNAME()))) {
                    removePlayer(mafiaVote);
                }

            } else {
                if (mafiaVote.equals(sharedData.players.get(findCharacter(Character.JANSAKHT)).getNAME())) {
                    if (gameState.jansakhtLives == 0)
                        removePlayer(mafiaVote);
                    else if (gameState.jansakhtLives == 1 || gameState.jansakhtLives == 2) {
                        gameState.jansakhtLives--;
                    }
                } else if (!mafiaVote.equals(drShahrVote)) {
                    removePlayer(mafiaVote);
                }
            }
        } else {
            if (mafiaVote.equals(sharedData.players.get(findCharacter(Character.JANSAKHT)).getNAME())) {
                if (gameState.jansakhtLives <= 0)
                    removePlayer(mafiaVote);
                else if (gameState.jansakhtLives == 1 || gameState.jansakhtLives == 2) {
                    gameState.jansakhtLives--;
                }
            }
        }

//        if (drShahrIndex == -1 || !mafiaVote.equals(drShahrTurn()) ||
//                ((gameState.cityDoctorSelfReviveCount == 1) && mafiaVote.equals(sharedData.players.get(drShahrIndex).getNAME()))) {
//            removePlayer(mafiaVote);
//        } else if (mafiaVote.equals(sharedData.players.get(drShahrIndex).getNAME()))
//            gameState.cityDoctorSelfReviveCount++;


//        if (drLectorIndex == -1 || !drLectorTurn().equals(herfiVote) ||
//                ((gameState.drLectorSelfReviveCount == 1) && herfiVote.equals(sharedData.players.get(drLectorIndex).getNAME()))) {
//
//        }
//
//        if (herfeiIndex != -1 && !herfeiTurn(herfeiIndex).equals("no one")) {
//
//            Iterator<Player> it = sharedData.players.iterator();
//            while (it.hasNext()) {
//                Player p = it.next();
//                if (p.getNAME().equals(herfeiVote) && !p.isMafia()) {
//                    removePlayer(sharedData.players.get(herfeiIndex).getNAME());
//                    break;
//                } else {
//                    removePlayer(herfeiVote);
//                }
//            }
//        }

        if (isExist(Character.HERFEI)) {
            String herfeiVote = herfeiTurn(herfeiIndex);
            if (!herfeiVote.equals("no one")) {
         /*   if (!herfeiVote.equals("no one"))
                System.out.println("");*/
                if (isExist(Character.DRLECTOR)) {
                    String drlectorVote = drLectorTurn(drLectorIndex);
                    if (isCitizen(herfeiVote)) {
                        txtOfMessage = "you removed a citizen. so you lose";
                        sharedData.clientHandlers.get(herfeiIndex).sendMessage(new Message("-", "gameManager", "HERFEI", txtOfMessage));
                        removePlayer(sharedData.players.get(herfeiIndex).getNAME());
                    } else {
                        if (herfeiVote.equals(sharedData.players.get(drLectorIndex).getNAME())) {
                            if (!drlectorVote.equals("me")) {
                                removePlayer(sharedData.players.get(drLectorIndex).getNAME());
                            }
                        } else {
                            if (!drlectorVote.equals(herfeiVote)) {
                                removePlayer(herfeiVote);
                            }
                        }
                    }
                }
            } else {
                if (isCitizen(herfeiVote)) {
                    txtOfMessage = "you removed a citizen. so you lose";
                    sharedData.clientHandlers.get(herfeiIndex).sendMessage(new Message("-", "gameManager", "HERFEI", txtOfMessage));
                    removePlayer(sharedData.players.get(herfeiIndex).getNAME());
                } else {
                    removePlayer(herfeiVote);
                }
            }
        }

        if (isExist(Character.RAVANSHENAS)) {
            gameState.ravanShenasVote = ravanShenasTurn(ravanShenasIndex);
            if (!gameState.ravanShenasVote.equals("no one")) {
                gameState.ravanshenasSilenced = true;
            }
        }
        // is continued


        if (isExist(Character.JANSAKHT)) {
            if (gameState.jansakhtEstelamCount < 2) {
                janshakhtTurn(jansakhtIndex);
            }
        }
        // write handler in day

        if (isExist(Character.KARAGAH)) {
            String karagahVote = karagahTurn(karagahIndex);
            for (Player p : sharedData.players) {
                if (p.getNAME().equals(karagahVote)) {
                    if (p.getCHARACTER() == Character.MAFIA || p.getCHARACTER() == Character.DRLECTOR) {
                        txtOfMessage = p.getNAME() + " is " + p.getCHARACTER();
                        karagahResponse(txtOfMessage, karagahIndex);
                        break;
                    } else {
                        txtOfMessage = "I cant tell you who she/he is . he/she may be GODFATHER or CITIZEN.";
                        karagahResponse(txtOfMessage, karagahIndex);
                        break;
                    }
                }
            }
        }

    }


    public HashMap<String, Integer> calculateVote() {
        HashMap<String, Integer> playerVoteMap = new HashMap<>();

        for (Player p : sharedData.players) {
            playerVoteMap.put(p.getNAME(), 0);
        }
        int temp;
        for (ClientHandler cl : sharedData.clientHandlers) {
            if (playerVoteMap.containsKey(cl.getClientVote())) {
                temp = playerVoteMap.get(cl.getClientVote());
                temp++;
                playerVoteMap.replace(cl.getClientVote(), temp);
            }
        }
        return playerVoteMap;

    }

    public void removePlayer(String name) {
        Iterator<Player> it = sharedData.players.iterator();
        Iterator<Player> citizenIt = sharedData.citizenPlayers.iterator();
        Iterator<Player> mafiaIt = sharedData.mafiaPlayers.iterator();

        while (citizenIt.hasNext()) {
            if (citizenIt.next().getNAME().equals(name)) {
                citizenIt.remove();
                break;
            }
        }

        while (mafiaIt.hasNext()) {
            if (mafiaIt.next().getNAME().equals(name)) {
                mafiaIt.remove();
                break;
            }
        }

        for (Player p : sharedData.players) {
            if (p.getNAME().equals(name)) {
                gameState.removedPlayers.add(p);
                break;
            }
        }
        while (it.hasNext()) {
            if (it.next().getNAME().equals(name)) {
                it.remove();
                removeClient(name);
                break;
            }
        }

    }

    public void removeClient(String name) {
        Iterator<ClientHandler> it = sharedData.clientHandlers.iterator();
        Iterator<ClientHandler> citizenIt = sharedData.citizenClients.iterator();
        Iterator<ClientHandler> mafiaIt = sharedData.mafiaClients.iterator();

        while (citizenIt.hasNext()) {
            if (citizenIt.next().getNAME().equals(name)) {
                citizenIt.remove();
                break;
            }
        }

        while (mafiaIt.hasNext()) {
            if (mafiaIt.next().getNAME().equals(name)) {
                mafiaIt.remove();
                break;
            }
        }

        while (it.hasNext()) {
            if (it.next().getNAME().equals(name)) {
                it.remove();
                sharedData.playerCount--;
                break;
            }
        }


    }

    public int findCharacter(Character character) {
        int characterIndex = -1;
        for (int i = 0; i < sharedData.players.size(); i++) {
            if (sharedData.players.get(i).getCHARACTER() == character) {
                characterIndex = i;
                break;
            }
        }
        return characterIndex;
    }

    public boolean isExist(Character character) {
        if (findCharacter(character) == -1)
            return false;
        else
            return true;
    }

    private String mafiaGroupTurn(int mafiaGpIndex) {
        String text;
        Message message;
        String vote = "";

        text = "mafia is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "MAFIA GP please enter your vote like above \n" + " vote <username> ";


        message = new Message("?vote", "gameManager", "MAFIAGP", text);
        sharedData.clientHandlers.get(mafiaGpIndex).sendMessage(message);

        sharedData.clientHandlers.get(mafiaGpIndex).setCommand("?vote");

//            CountDownLatch latch = new CountDownLatch(1);
        latch = new CountDownLatch(1);
        sharedData.clientHandlers.get(mafiaGpIndex).setLatch(latch);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vote = sharedData.clientHandlers.get(mafiaGpIndex).getClientVote();

        return vote;
        // handle who has to be remove
    }

    public String godFatherTurn(int godFatherIndex) {
        String text;
        Message message;
        String vote = "";

        text = "GODFATHER is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "GODFATHER please enter your vote like above \n" + " vote <username> ";

        message = new Message("?vote", "gameManager", "GODFATHER", text);
        sharedData.clientHandlers.get(godFatherIndex).sendMessage(message);

        sharedData.clientHandlers.get(godFatherIndex).setCommand("?vote");

//            CountDownLatch latch = new CountDownLatch(1);
        latch = new CountDownLatch(1);
        sharedData.clientHandlers.get(godFatherIndex).setLatch(latch);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vote = sharedData.clientHandlers.get(godFatherIndex).getClientVote();

        return vote;
        // handle who has to be remove
    }

    public String drShahrTurn(int drShahrIndex) {
        String text;
        String vote = "";
        Message message;

        text = "DRSHAHR is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "DRSHAHR who do you want to heal?please enter your vote like above \n" + " vote <username> ";

        do {
            message = new Message("?vote", "gameManager", "DRSHAHR", text);
            sharedData.clientHandlers.get(drShahrIndex).sendMessage(message);

            sharedData.clientHandlers.get(drShahrIndex).setCommand("?vote");

            latch = new CountDownLatch(1);
            sharedData.clientHandlers.get(drShahrIndex).setLatch(latch);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            vote = sharedData.clientHandlers.get(drShahrIndex).getClientVote();
            if (vote.equals("me")) {
                if (gameState.cityDoctorSelfReviveCount > 0) {
                    String txt = "you have already save yourself ; enter some other value";
                    message = new Message("-", "gameManager", sharedData.players.get(drShahrIndex).getNAME(), txt);
                    sharedData.clientHandlers.get(drShahrIndex).sendMessage(message);
                } else {
                    gameState.cityDoctorSelfReviveCount++;
                    break;
                }
            } else
                break;
        } while (true);
        return vote;
    }

    public String herfeiTurn(int herfeiIndex) {
        String text;
        Message message;
        String vote = "";

        text = "HERFEI is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "Herfei who do you want to kill?please enter your vote like above \n" + " vote <username> ";

        message = new Message("?vote", "gameManager", "HERFEI", text);
        sharedData.clientHandlers.get(herfeiIndex).sendMessage(message);

        sharedData.clientHandlers.get(herfeiIndex).setCommand("?vote");

        latch = new CountDownLatch(1);
        sharedData.clientHandlers.get(herfeiIndex).setLatch(latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return vote;
    }

    public void shahrdarTurn(int shahrDarIndex) {
        String text;
        Message message;
        String vote;


        text = "SHAHRDAR is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "SHAHRDAR who do you want to cancel the voting(yes/no)?please enter your vote like above \n" + " vote <username> ";


        message = new Message("?vote", "gameManager", "SHAHRDAR", text);
        sharedData.clientHandlers.get(shahrDarIndex).sendMessage(message);

        sharedData.clientHandlers.get(shahrDarIndex).setCommand("?vote");

        latch = new CountDownLatch(1);
        sharedData.clientHandlers.get(shahrDarIndex).setLatch(latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        vote = sharedData.clientHandlers.get(shahrDarIndex).getClientVote();
        if (vote.equals("no"))
            gameState.voteCancelByShahrdar = false;
        else
            gameState.voteCancelByShahrdar = true;
    }

    public String ravanShenasTurn(int ravanShenasIndex) {
        String text;
        Message message;
        String vote;

        text = "RAVANSHENAS is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "RAVANSHENAS who do you want to silent?please enter your vote like above \n" + " vote <username> ";


        message = new Message("?vote", "gameManager", "RAVANSHENAS", text);
        sharedData.clientHandlers.get(ravanShenasIndex).sendMessage(message);

        sharedData.clientHandlers.get(ravanShenasIndex).setCommand("?vote");

        latch = new CountDownLatch(1);
        sharedData.clientHandlers.get(ravanShenasIndex).setLatch(latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        vote = sharedData.clientHandlers.get(ravanShenasIndex).getClientVote();
        return vote;
    }

    public void janshakhtTurn(int jansakhtIndex) {
        String text;
        Message message;
        String vote;

        text = "JANSAKHT is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "JANSAKHT do you want to know about who exit until now? please enter your vote like above \n" + " vote <username> ";

        message = new Message("?vote", "gameManager", "JANSAKHT", text);
        sharedData.clientHandlers.get(jansakhtIndex).sendMessage(message);

        sharedData.clientHandlers.get(jansakhtIndex).setCommand("?vote");

        latch = new CountDownLatch(1);
        sharedData.clientHandlers.get(jansakhtIndex).setLatch(latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vote = sharedData.clientHandlers.get(jansakhtIndex).getClientVote();
        if (!vote.equals("no one")) {
            gameState.jansakhtEstelamCount++;
            gameState.isJansakhtAsk = true;

        }

    }

    public String drLectorTurn(int drLectorIndex) {
        String text;
        Message message;
        String vote = "";


        do {
            text = "DRLECTOR is your turn";
            sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

            text = "DRLECTOR who do you want to heal?please enter your vote like above \n" + " vote <username> ";
//        if (drLectorIndex != -1) {

            message = new Message("?vote", "gameManager", "DRLECTOR", text);
            sharedData.clientHandlers.get(drLectorIndex).sendMessage(message);

            sharedData.clientHandlers.get(drLectorIndex).setCommand("?vote");

            latch = new CountDownLatch(1);
            sharedData.clientHandlers.get(drLectorIndex).setLatch(latch);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            vote = sharedData.clientHandlers.get(drLectorIndex).getClientVote();
//        }
            if (vote.equals(sharedData.clientHandlers.get(drLectorIndex).getNAME())) {
                if (gameState.drLectorSelfReviveCount > 0) {
                    message = new Message("-", "gameManager", "DRLECTOR", "you cant save yourself 2times enter again");
                    sharedData.clientHandlers.get(drLectorIndex).sendMessage(message);
                } else {
                    gameState.drLectorSelfReviveCount++;
                    break;
                }
            } else
                break;
        } while (true);
        return vote;
    }

    public String karagahTurn(int kargahIndex) {
        String text;
        Message message;
        String vote;

        text = "KARAGAH is your turn";
        sharedData.server.broadcast(new Message("-", "gameManager", "all", text));

        text = "KARAGAH who do you want to know about?please enter your vote like above \n" + " vote <username> ";
//        if (drLectorIndex != -1) {

        message = new Message("?vote", "gameManager", "KARAGAH", text);
        sharedData.clientHandlers.get(kargahIndex).sendMessage(message);

        sharedData.clientHandlers.get(kargahIndex).setCommand("?vote");

        latch = new CountDownLatch(1);
        sharedData.clientHandlers.get(kargahIndex).setLatch(latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vote = sharedData.clientHandlers.get(kargahIndex).getClientVote();
        return vote;
//        }
    }

    public void karagahResponse(String txt, int kargahIndex) {
        Message message = new Message("-", "gameManager", "KARAGAH", txt);
        sharedData.clientHandlers.get(kargahIndex).sendMessage(message);
    }

    public boolean isCitizen(String name) {
        for (Player p : sharedData.players) {
            if (name.equals(p.getNAME())) {
                if (p.isMafia())
                    return false;
            }
        }
        return true;
    }

}
