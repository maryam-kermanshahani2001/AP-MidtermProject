package gameLogic;

import Elements.Character;
import Elements.Message;
import Elements.Player;
import utils.SharedData;

import java.util.*;

public class GameStarter {
    private GameLoop gameLoop;
    private SharedData sharedData;

    public GameStarter() {
        gameLoop = new GameLoop();
        this.sharedData = SharedData.getInstance();
        gameInit();

    }

    public void gameInit() {
     /*   cardInit();
        boardInit();
        murderInit();*/
        setRoles();
        gameLoop.init();
    }

//
//    /**
//     * Initialize each Player of the Game
//     */
//    public void playersInit() {
//        Player[] players = new Player[sharedData.playerCount];
//
//        for (int i = 0; i < sharedData.playerCount; i++) {
//            Character character = Character.values()[i];
//
//            players[i] = new Player(character.toString(), character, color, playerLocations[i], notebook);
//            sharedData.gameBoard.getBlocks()
//                    [playerLocations[i].getY()][playerLocations[i].getX()].setFull(true);
//        }
//        for (int i = 0; i < sharedData.PLAYER_COUNT; i++) {
//            players[i].setHand(sharedData.cardDeck.getRandomCards
//                    (sharedData.PLAYER_HAND_SIZE));
//        }
//        sharedData.players = players;
//        logger.log("Players are initialized ", LogLevels.INFO);
//    }

    /*
    set roles of the players of the sharedData
     */
    public void setRoles() {
        Player[] players = new Player[sharedData.playerCount];
        //ArrayList<Player> playerArrayList = new ArrayList<>();
        // Java Program to generate random numbers with no duplicates
/*
        HashSet<Integer> set = new HashSet<Integer>();
        Random randNum = new Random();

        for (int i = 0; set.size() < sharedData.playerCount; i++) {
            set.add(randNum.nextInt(sharedData.playerCount));
        }
        List<Integer> list = new ArrayList<Integer>(set);
*/
        ArrayList<Integer> list = new ArrayList<Integer>();
        Random randomGenerator = new Random();
        while (list.size() < sharedData.playerCount) {

            int random = randomGenerator.nextInt(sharedData.playerCount);
            if (!list.contains(random)) {
                list.add(random);
            }
        }
        int i = 0;
       /* for (i = 0; i < (int) (sharedData.playerCount / (3 * 3)); i++) {
            players[list.get(i)] = new Player(sharedData.clientHandlers.get(i).getNAME(), Character.MAFIA);
            players[list.get(i)].setMafia();
        }*/
        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.MAFIA);
        players[list.get(i)].setMafia();
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.DRLECTOR);
        players[list.get(i)].setMafia();
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.GODFATHER);
        players[list.get(i)].setMafia();
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.DOCTORSHAHR);
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.KARAGAH);
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.HERFEI);
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.SHAHRDAR);
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.JANSAKHT);
//        // Handle shahrvand for tedad dg az bazikon ha
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.SHAHRVAND);
        i++;

        players[list.get(i)] = new Player(sharedData.clientHandlers.get(list.get(i)).getNAME(), Character.RAVANSHENAS);


        sharedData.players = new ArrayList<Player>(Arrays.asList(players));

        for (i = 0; i < sharedData.playerCount; i++) {
            sharedData.clientHandlers.get(i).sendMessage(new Message("-", "server", "all", "Your character is " + players[i].getCHARACTER()));
        }

        sharedData.removedPlayers = new ArrayList<>();


    }


}
