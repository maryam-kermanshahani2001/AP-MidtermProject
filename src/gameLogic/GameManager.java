package gameLogic;

import Elements.Character;
import Elements.Message;
import Elements.Player;
import network.server.ClientHandler;
import utils.SharedData;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

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
        int index = sharedData.removedPlayers.size();
        String text;
        Message message;
        if (index >= 1) {
            text = sharedData.removedPlayers.get(index - 1).getNAME() + " removed last night";
            message = new Message("-", "gameManager", "all", text);
            sharedData.server.broadcast(message);

        }

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

        removePlayer(playerName);

    }

    public void nightRun() {
        String txtOfMessage;

        int godFatherIndex = -1;
        int drShahrIndex = -1;
        int herfeiIndex = -1;
        int ravanShenasIndex = -1;
        int mafiaIndex = -1;
        int drLectorIndex = -1;

        String drShahrVote;
        String mafiaVote;
        String herfiVote = herfeiTurn(herfeiIndex);

        sharedData.server.broadcast(new Message("-", "gameManager", "all", "close your eyes"));

        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setCommand("mafiaChat");
        }
        // while with thread sleep
        latch = new CountDownLatch(sharedData.playerCount);
        for (int i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).setLatch(latch);
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


        if (godFatherIndex != -1) {
            mafiaVote = godFatherTurn(godFatherIndex);
        } else if (mafiaIndex != -1) {
            mafiaVote = mafiaGroupTurn(mafiaIndex);
        } else {
            mafiaVote = mafiaGroupTurn(drLectorIndex);
        }

        if (isExist(Character.DOCTORSHAHR)) {
            drShahrIndex = findCharacter(Character.DOCTORSHAHR);
            drShahrVote = drShahrTurn(drShahrIndex);
            if (drShahrVote.equals("me")) {

                if (!mafiaVote.equals(sharedData.players.get(drShahrIndex).getNAME())) {
                    removePlayer(mafiaVote);
                }

            } else {
                if (!mafiaVote.equals(drShahrVote)) {
                    removePlayer(mafiaVote);
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
                        if (!drlectorVote.equals(herfiVote)) {
                            removePlayer(herfeiVote);
                        }
                    }
                }
            } else {
                if (isCitizen(herfeiVote)) {
                    txtOfMessage = "you removed a citizen. so you lose";
                    sharedData.clientHandlers.get(herfeiIndex).sendMessage(new Message("-", "gameManager", "HERFEI", txtOfMessage));
                    removePlayer(sharedData.players.get(herfeiIndex).getNAME());
                }
                else {
                    removePlayer(herfeiVote);
                }
            }
        }



//        ravanShenasIndex =
//
//                findCharacter(Character.RAVANSHENAS);
//
//        ravanShenasTurn(ravanShenasIndex);


    }



private String mafiaGroupTurn(int mafiaGpIndex){
        String text;
        Message message;
        String vote="";

        text="mafia is your turn";
        sharedData.server.broadcast(new Message("-","gameManager","all",text));

        text="MAFIA GP please enter your vote like above \n"+" vote <username> ";


        message=new Message("?vote","gameManager","MAFIA",text);
        sharedData.clientHandlers.get(mafiaGpIndex).sendMessage(message);

        sharedData.clientHandlers.get(mafiaGpIndex).setCommand("?vote");

//            CountDownLatch latch = new CountDownLatch(1);
        latch=new CountDownLatch(1);
        sharedData.clientHandlers.get(mafiaGpIndex).setLatch(latch);
        try{
        latch.await();
        }catch(InterruptedException e){
        e.printStackTrace();
        }
        vote=sharedData.clientHandlers.get(mafiaGpIndex).getClientVote();

        return vote;
        // handle who has to be remove
        }

public HashMap<String, Integer> calculateVote(){
        HashMap<String, Integer> playerVoteMap=new HashMap<>();

        for(Player p:sharedData.players){
        playerVoteMap.put(p.getNAME(),0);
        }
        int temp;
        for(ClientHandler cl:sharedData.clientHandlers){
        if(playerVoteMap.containsKey(cl.getClientVote())){
        temp=playerVoteMap.get(cl.getClientVote());
        temp++;
        playerVoteMap.replace(cl.getClientVote(),temp);
        }
        }
        return playerVoteMap;

        }

public void removePlayer(String name){
        Iterator<Player> it=sharedData.players.iterator();
        while(it.hasNext()){
        if(it.next().getNAME().equals(name)){
        it.remove();
        removeClient(name);
        break;
        }
        }

        }

public void removeClient(String name){
        Iterator<ClientHandler> it=sharedData.clientHandlers.iterator();
        while(it.hasNext()){
        if(it.next().getNAME().equals(name)){
        it.remove();
        sharedData.playerCount--;
        break;
        }
        }


        }


public int findCharacter(Character character){
        int characterIndex=-1;
        for(int i=0;i<sharedData.players.size();i++){
        if(sharedData.players.get(i).getCHARACTER()==character){
        characterIndex=i;
        break;
        }
        }
        return characterIndex;
        }

public boolean isExist(Character character){
        if(findCharacter(character)==-1)
        return false;
        else
        return true;
        }

public String godFatherTurn(int godFatherIndex){
        String text;
        Message message;
        String vote="";

        text="GODFATHER is your turn";
        sharedData.server.broadcast(new Message("-","gameManager","all",text));

        text="GODFATHER please enter your vote like above \n"+" vote <username> ";


        message=new Message("?vote","gameManager","GODFATHER",text);
        sharedData.clientHandlers.get(godFatherIndex).sendMessage(message);

        sharedData.clientHandlers.get(godFatherIndex).setCommand("?vote");

//            CountDownLatch latch = new CountDownLatch(1);
        latch=new CountDownLatch(1);
        sharedData.clientHandlers.get(godFatherIndex).setLatch(latch);
        try{
        latch.await();
        }catch(InterruptedException e){
        e.printStackTrace();
        }
        vote=sharedData.clientHandlers.get(godFatherIndex).getClientVote();

        return vote;
        // handle who has to be remove
        }


public String drShahrTurn(int drShahrIndex){
        String text;
        String vote="";
        Message message;

        text="DRSHAHR is your turn";
        sharedData.server.broadcast(new Message("-","gameManager","all",text));

        text="DRSHAHR who do you want to heal?please enter your vote like above \n"+" vote <username> ";

        do{
        message=new Message("?vote","gameManager","DRSHAHR",text);
        sharedData.clientHandlers.get(drShahrIndex).sendMessage(message);

        sharedData.clientHandlers.get(drShahrIndex).setCommand("?vote");

        latch=new CountDownLatch(1);
        sharedData.clientHandlers.get(drShahrIndex).setLatch(latch);

        try{
        latch.await();
        }catch(InterruptedException e){
        e.printStackTrace();
        }
        vote=sharedData.clientHandlers.get(drShahrIndex).getClientVote();
        if(vote.equals("me")){
        if(gameState.cityDoctorSelfReviveCount>0){
        String txt="you have already save yourself ; enter some other value";
        message=new Message("-","gameManager",sharedData.players.get(drShahrIndex).getNAME(),txt);
        }else{
        gameState.cityDoctorSelfReviveCount++;
        break;
        }
        }
        }while(true);
        return vote;
        }

public String herfeiTurn(int herfeiIndex){
        String text;
        Message message;
        String vote="";

        text="HERFEI is your turn";
        sharedData.server.broadcast(new Message("-","gameManager","all",text));

        text="Herfei who do you want to kill?please enter your vote like above \n"+" vote <username> ";

        message=new Message("?vote","gameManager","HERFEI",text);
        sharedData.clientHandlers.get(herfeiIndex).sendMessage(message);

        sharedData.clientHandlers.get(herfeiIndex).setCommand("?vote");

        latch=new CountDownLatch(1);
        sharedData.clientHandlers.get(herfeiIndex).setLatch(latch);

        try{
        latch.await();
        }catch(InterruptedException e){
        e.printStackTrace();
        }

        return vote;
        }

public void shahrdarTurn(){

        }

public void ravanShenasTurn(int ravanShenasIndex){
        String text;
        Message message;

        text="RAVANSHENAS is your turn";
        sharedData.server.broadcast(new Message("-","gameManager","all",text));

        text="RAVANSHENAS who do you want to silent?please enter your vote like above \n"+" vote <username> ";
        if(ravanShenasIndex!=-1){

        message=new Message("?vote","gameManager","RAVANSHENAS",text);
        sharedData.clientHandlers.get(ravanShenasIndex).sendMessage(message);

        sharedData.clientHandlers.get(ravanShenasIndex).setCommand("?vote");

        latch=new CountDownLatch(1);
        sharedData.clientHandlers.get(ravanShenasIndex).setLatch(latch);

        try{
        latch.await();
        }catch(InterruptedException e){
        e.printStackTrace();
        }
        }
        }

public void janshakhtTurn(){
        String text;
        Message message;
        int jansakhtIndex=-1;
        jansakhtIndex=findCharacter(Character.JANSAKHT);

        text="JANSAKHT is your turn";
        sharedData.server.broadcast(new Message("-","gameManager","all",text));

        text="JANSAKHT who do you want to know about?please enter your vote like above \n"+" vote <username> ";
        if(jansakhtIndex!=-1){

        message=new Message("?vote","gameManager","JANSAKHT",text);
        sharedData.clientHandlers.get(jansakhtIndex).sendMessage(message);

        sharedData.clientHandlers.get(jansakhtIndex).setCommand("?vote");

        latch=new CountDownLatch(1);
        sharedData.clientHandlers.get(jansakhtIndex).setLatch(latch);

        try{
        latch.await();
        }catch(InterruptedException e){
        e.printStackTrace();
        }
        }
        }

public String drLectorTurn(int drLectorIndex){
        String text;
        Message message;
        String vote="";


        do{
        text="DRLECTOR is your turn";
        sharedData.server.broadcast(new Message("-","gameManager","all",text));

        text="DRLECTOR who do you want to heal?please enter your vote like above \n"+" vote <username> ";
//        if (drLectorIndex != -1) {

        message=new Message("?vote","gameManager","DRLECTOR",text);
        sharedData.clientHandlers.get(drLectorIndex).sendMessage(message);

        sharedData.clientHandlers.get(drLectorIndex).setCommand("?vote");

        latch=new CountDownLatch(1);
        sharedData.clientHandlers.get(drLectorIndex).setLatch(latch);

        try{
        latch.await();
        }catch(InterruptedException e){
        e.printStackTrace();
        }
        vote=sharedData.clientHandlers.get(drLectorIndex).getClientVote();
//        }
        if(vote.equals(sharedData.clientHandlers.get(drLectorIndex).getNAME())){
        if(gameState.drLectorSelfReviveCount>0){
        message=new Message("-","gameManager","DRLECTOR","you cant save yourself 2times enter again");
        sharedData.clientHandlers.get(drLectorIndex).sendMessage(message);
        }else{
        gameState.drLectorSelfReviveCount++;
        break;
        }
        }
        }while(true);
        return vote;
        }

public boolean isCitizen(String name){
        for(Player p:sharedData.players){
        if(name.equals(p.getNAME())){
        if(p.isMafia())
        return false;
        }
        }
        return true;
        }

        }
