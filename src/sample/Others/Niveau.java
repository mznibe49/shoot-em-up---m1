package sample.Others;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sample.Controllers.GameController;
import java.util.ArrayList;

public class Niveau {

    private int niveau;
    private Pane pane;
    private ArrayList<Demon> liste;

    public Niveau(int niveau, Pane pane, ArrayList<Demon> listeDemon) {
        this.niveau=niveau;
        this.pane=pane;
        this.liste=listeDemon;
    }

    public int getNiveau() {
        return niveau;
    }

    public void demarrerNiveau(){
        if(liste!=null)
            for(Demon demon : liste){
                pane.getChildren().add(demon);
                GameController.listeDemon.add(demon);
            }
    }

}
