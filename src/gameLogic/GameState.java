package gameLogic;

import utils.SharedData;

public class GameState {
    private static GameState instance;
    public int jansakhtEstelam;
    public int drLectorSelfReviveCount;
    public int cityDoctorSelfReviveCount;
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

}
