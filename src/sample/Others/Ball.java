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
import sample.Controllers.CoverController;
import sample.Controllers.GameController;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class Ball extends ImageView {

    public Timeline shoot;
    final int MaxY=345;
    public double xball, yball;
    public static  Pane p;

    public Ball(double x, double y, Pane pane) {

        this.xball = x;
        this.yball = y;
        this.p = pane;
        this.setImage(new Image( "sample/Icone/ball.gif"));
        this.setFitWidth(50);
        this.setFitHeight(50);

        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public void shoot() {

        p.getChildren().add(this);
        shoot = new Timeline();
        shoot.setCycleCount(Animation.INDEFINITE);

        KeyFrame kf = new KeyFrame(Duration.millis(10), e -> {
            if(this.getImage().impl_getUrl().contains("ball.gif")) {
               ballPlayer();
            }else{
                ballBoss();
            }
        });

        shoot.getKeyFrames().add(kf);
        shoot.play();
    }

    public void ballPlayer(){
        Iterator<Demon> itr = GameController.listeDemon.iterator();
        while (itr.hasNext()) {
            Demon demon = itr.next();
            if (this.getBoundsInParent().intersects(demon.getBoundsInParent())) {

                //animation.stop de demon est faite ici
                demon.blood_splatter();

                Timer t = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            demon.animation.stop();
                            p.getChildren().remove(demon);
                        });
                    }
                };
                t.schedule(tt, 380);
                p.getChildren().remove(this);
                shoot.stop();
                Pistolero.listBall.remove(this);
                itr.remove();
                AudioClip audio = new AudioClip(getClass().getResource("../Sound/explosion.wav").toString());
                audio.play();
                GameController.player.setScore(GameController.player.getScore() + 1);
            }
        }
        if (GameController.boss!=null) {
            Boss b = GameController.boss;
            if (this.getBoundsInParent().intersects(b.getBoundsInParent())) {
                b.bossVie--;
                p.getChildren().remove(this);
                shoot.stop();
                Pistolero.listBall.remove(this);
                if (b.bossVie == 0) {
                    b.dead();
                    GameController.stopAll();
                    stopEtEnregister(true);
                }
            }
        }

        double dist_move = 2;

        if ((yball < 0)) {
            p.getChildren().remove(this);
            Pistolero.listBall.remove(this);
            shoot.stop();
        } else {
            yball -= dist_move;
            this.setY(this.getY() - dist_move);
        }
    }

    public void ballBoss(){
        if(this.getBoundsInParent().intersects(GameController.pistolero.getBoundsInParent())){
            if (GameController.pistolero.isProtection()) {
                String urlImage = GameController.pistolero.getImagePistolero().impl_getUrl();
                GameController.pistolero.setImagePistolero(new Image(GameController.avoirImagePistolero(urlImage)));
                GameController.pistolero.setProtection(false);
            } else {
                // attendre les 5s
                if(!GameController.pistolero.invincible){
                    GameController.player.setVie(GameController.player.getVie() - 1);//vieDepart--;
                    if (GameController.player.getVie() == 0) {
                        GameController.pistolero.dead();
                        GameController.stopAll();
                        stopEtEnregister(false);
                    } else {
                        // remmetre position depart
                        GameController.pistolero.relocate(380,420);
                        GameController.pistolero.invincible = true;
                    }
                }
            }
        }
        double max_y = 2 + this.getBoundsInParent().getMaxY();
        if ( max_y > p.getHeight()) {
            p.getChildren().remove(this);
            Boss.listBall.remove(this);
            shoot.stop();
        }else{
            yball += 2;
            this.setY(this.getY()+2);
        }
    }



    public static void stopEtEnregister(boolean gm){


        if(!gm) {
            // afficher defaite
            CoverController.son.stop();
            CoverController.audio.stop();
            AudioClip audio = new AudioClip(Ball.class.getResource("../Sound/DefeatedGame.wav").toString());
            audio.play();
            GameController.imageNiveau = new ImageView("sample/Icone/gOver.png");

        } else {
            // afficher victoire
            CoverController.son.stop();
            CoverController.audio.stop();
            AudioClip audio = new AudioClip(Ball.class.getResource("../Sound/VictoryTune.wav").toString());
            audio.play();
            GameController.imageNiveau = new ImageView("sample/Icone/vic.png");
        }
        p.getChildren().add(GameController.imageNiveau);
        GameController.enregistrementScore();
    }

}
