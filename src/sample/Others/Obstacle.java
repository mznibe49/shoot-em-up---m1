package sample.Others;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import sample.Controllers.GameController;

import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class Obstacle extends ImageView {

    public Timeline animation;
    int cpt = 1; // compteur sur img
    public double xObstacle=0, yObstacle=0;
    public Pane p;


    public Obstacle(double x, double y, Pane pane) {
        this.setImage(new Image("sample/obstacle/rf1.png"));
        this.setFitWidth(90);
        this.setFitHeight(90);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.p = pane;
        p.getChildren().add(this);
    }

    public void seDeplacer() {
        animation = new Timeline();
        animation.setCycleCount(Animation.INDEFINITE);

        KeyFrame kf = new KeyFrame(Duration.millis(25), e -> {

            bougerObstacle();

        });

        animation.getKeyFrames().add(kf);
        animation.play();
    }

    public void bougerObstacle() {
        cpt++;
        //System.out.println(cpt%32);
        int ind = (cpt % 32) + 1;
        String img_path = "sample/obstacle/rf" + ind + ".png";
        this.setImage(new Image(img_path));

        // traité limite du cadre


        // supprimer toute balle collisionné avec
        ListIterator<Ball> itr = Pistolero.listBall.listIterator();
        while (itr.hasNext()) {
            Ball b = itr.next();
            if (this.getBoundsInParent().intersects(b.getBoundsInParent())) {
                itr.remove();
                b.shoot.stop();
                ((Pane) this.getParent()).getChildren().remove(b);
            }
        }
        // collision avec demon
        ListIterator<Demon> itr_demon = GameController.listeDemon.listIterator();
        while (itr_demon.hasNext()) {
            //Ball b = itr.next();
            Demon dem = itr_demon.next();
            if (this.getBoundsInParent().intersects(dem.getBoundsInParent())) {
                dem.dir = dem.dir * (-1);
                //dem.rand = dem.rand;
                //itr.remove();
                //b.shoot.stop();
                //((Pane) this.getParent()).getChildren().remove(b);
            }
        }

        // collision avec pistolero
        if (this.getBoundsInParent().intersects(GameController.pistolero.getBoundsInParent())) {
            if (GameController.pistolero.isProtection()) {
                this.dead();
                String urlImage = GameController.pistolero.getImagePistolero().impl_getUrl();
                GameController.pistolero.setImagePistolero(new Image(GameController.avoirImagePistolero(urlImage)));
                GameController.pistolero.setProtection(false);
            } else {
                // attendre les 5s
                if(!GameController.pistolero.invincible){
                    GameController.player.setVie(GameController.player.getVie() - 1);//vieDepart--;
                    if (GameController.player.getVie() == 0) {
                        GameController.pistolero.dead();
                        this.dead();
                        GameController.stopAll();
                        Ball.stopEtEnregister(false);

                    } else {

                        // remettre position depart
                        GameController.pistolero.relocate(380,420);
                        GameController.pistolero.invincible = true;

                    }
                }

            }

        }

        double max_y = 1 + this.getBoundsInParent().getMaxY();
        double min_x = 1 + this.getBoundsInParent().getMinX();
        double max_x = 1 + this.getBoundsInParent().getMaxX();
        if (min_x < 0 || max_x > p.getWidth()  || max_y > p.getHeight()  ) {
           // p.getChildren().remove(this);
            this.dead();
            Timeline t = new Timeline(new KeyFrame(Duration.seconds(2)));
            t.setCycleCount(1);
            t.setOnFinished(e -> {
                GameController.listObstacle.remove(this);
                p.getChildren().remove(this);
            });
            t.play();
        }else{
            if(this.getLayoutX()>350){
                this.xObstacle -=1;
                this.yObstacle +=1;
                this.setY(this.getY() + 1);
                this.setX(this.getX() - 1);


            }else{
                this.setY(this.getY() + 1);
                this.setX(this.getX() + 1);
                this.xObstacle +=1;
                this.yObstacle +=1;
            }
        }
        // traité collision avec demon et changé leur direction


    }

    private static final int COLUMNS = 9;
    private static final int COUNT = 81;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    private void dead() {
        this.animation.stop();
        Image img = new Image("sample/expPisto/exp.png");
        this.setImage(img);
        this.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));
        final Animation anim = new SpriteAnimation(
                this,
                Duration.millis(2000),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );
        anim.setOnFinished(e->{
            GameController.listObstacle.remove(this);
            p.getChildren().remove(this);
        });
        anim.play();
        //AudioClip audio = new AudioClip(getClass().getResource("../Sound/exlposion.wav").toString());
        //audio.play();
    }

}


