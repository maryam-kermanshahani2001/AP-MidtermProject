package Elements;

public class Player {
    private boolean isPlaying;
    private final String NAME;
    private final Character CHARACTER;
    private boolean isMafia = false;
    private String vote;
//    private int lives;

    public Player(String name, Character character) {
        NAME = name;
        CHARACTER = character;
        isPlaying = true;
    }

    public Character getCHARACTER() {
        return CHARACTER;
    }

    public String getNAME() {
        return NAME;
    }

    public void setMafia() {
        isMafia = true;
    }

    public boolean isMafia() {
        return isMafia;
    }

   /* public void setLives(int lives) {
        this.lives = lives;
    }*/
}
