package sample.Others;

public class Player {

    private String name;
    private int score;
    int vie;


    public String getName() {
        return name;
    }

    public int getVie() {
        return vie;
    }

    public int getScore() {
        return score;
    }

    public void setVie(int vie) {
        this.vie = vie;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Player(String name,int score, int vie){
        this.name = name;
        this.score = score;
        this.vie = vie;
    }
}
