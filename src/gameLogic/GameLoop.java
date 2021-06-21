package gameLogic;

import org.w3c.dom.UserDataHandler;
import utils.SharedData;

public class GameLoop {
    private SharedData sharedData;
    private GameManager gameManager;

    public GameLoop() {
        sharedData = SharedData.getInstance();
        gameManager = new GameManager();
    }

    public void init() {
        gameManager.firstNightRun();
        gameManager.firstDayRun();
        loop();
    }

    public void loop() {

        while (sharedData.mafiaClients.size() != 0 || sharedData.citizenPlayers.size() != 0) {
            gameManager.nightRun();
            gameManager.dayRun();
        }
        if (sharedData.mafiaPlayers.size() == 0)
            System.out.println("mafia lose");
        else
            System.out.println("citizen lose");
    }
}
