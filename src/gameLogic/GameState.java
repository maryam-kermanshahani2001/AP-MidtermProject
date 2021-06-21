package gameLogic;

import Elements.Player;
import utils.SharedData;

import java.util.ArrayList;

public class GameState {
    private static GameState instance;
    public int jansakhtEstelamCount;
    public boolean isJansakhtAsk = false;
    public int jansakhtLives = 2;
    public int drLectorSelfReviveCount;
    public int cityDoctorSelfReviveCount;
    public String ravanShenasVote;
    public ArrayList<Player> removedPlayers;
    public boolean ravanshenasSilenced = false;

    public boolean voteCancelByShahrdar = false;

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

}
