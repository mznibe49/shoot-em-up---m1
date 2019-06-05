package sample.Others;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import sample.Controllers.GameController;
import java.util.Timer;
import java.util.TimerTask;

public class Cerceau extends ImageView {
    public Timeline deplacement;
    Pane pane;

    public Cerceau(double x, double y, Pane pane){
        this.setImage(new Image("sample/Icone/pist/s1.png"));
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setFitWidth(50);
        this.setFitHeight(45);
        this.pane=pane;
    }

    public void cerceauPouvoir(){
        pane.getChildren().add(this);
        deplacement = new Timeline();
        deplacement.setCycleCount(Animation.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.millis(20), e ->{
            if(this.getBoundsInParent().intersects(GameController.pistolero.getBoundsInParent())){
                AudioClip audio = new AudioClip(getClass().getResource("../Sound/bonus.wav").toString());
                audio.play();
                String shieldPath = avoirShild(GameController.pistolero);
                GameController.pistolero.setImagePistolero( new Image(shieldPath));
                GameController.pistolero.setProtection(true);
                deplacement.stop();
                pane.getChildren().remove(this);
                this.setImage(null);
            }
            double max_y = 2 + this.getBoundsInParent().getMaxY();
            if ( max_y > pane.getHeight()) {
                pane.getChildren().remove(this);
                deplacement.stop();
                this.setImage(null);
            }else
                this.setY(this.getY()+2);
        });
        deplacement.getKeyFrames().add(kf);
        deplacement.play();
    }


    String avoirShild(Pistolero pisto){

        if(pisto.getImage().impl_getUrl().contains("yellow")) return "sample/Icone/pist/shield/yellow.png";
        else if (pisto.getImage().impl_getUrl().contains("white")) return "sample/Icone/pist/shield/white.png";
        else if(pisto.getImage().impl_getUrl().contains("black")) return "sample/Icone/pist/shield/black.png";
        return "sample/Icone/pist/shield/blue.png";

    }
}
