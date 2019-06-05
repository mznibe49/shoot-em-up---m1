package sample.Others;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import sample.Controllers.GameController;

public class Life extends ImageView {
    public Pane pane;
    public Timeline deplacement;

    public Life( double x, Pane pane){
        this.setImage(new Image("sample/Icone/life.gif"));
        this.setFitWidth(50);
        this.setFitHeight(50);
        this.setLayoutX(x);
        this.setLayoutY(0);
        this.pane=pane;
        pane.getChildren().add(this);
    }

    public void lancerLife() {


        deplacement = new Timeline();
        deplacement.setCycleCount(Animation.INDEFINITE);

        KeyFrame kf = new KeyFrame(Duration.millis(16), e -> {
            bougerLife();
        });

        deplacement.getKeyFrames().add(kf);
        deplacement.play();
    }

    private void bougerLife() {
        if(this.getBoundsInParent().intersects(GameController.pistolero.getBoundsInParent())){
            AudioClip audio = new AudioClip(getClass().getResource("../Sound/bonus.wav").toString());
            audio.play();
            pane.getChildren().remove(this);
            GameController.player.setVie(GameController.player.getVie() + 1);
            deplacement.stop();
            this.setImage(null);
        }
        double max_y = 2 + this.getBoundsInParent().getMaxY();
        if ( max_y > pane.getHeight()) {
            pane.getChildren().remove(this);
            deplacement.stop();
            this.setImage(null);
        }else
            this.setY(this.getY()+2);
    }


}
