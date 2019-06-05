package sample.Others;

public class Score {

    private String nom;
    private int score;
    private String temp;

    public Score(String nom, int score, String temp) {
        this.nom = nom;
        this.score = score;
        this.temp = temp;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
