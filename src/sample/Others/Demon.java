package sample.Others;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
 import javafx.util.Duration;
import sample.Controllers.GameController;

import java.util.*;

public class Demon extends ImageView {

    boolean male;
    public Timeline animation;
    public double myX = 0, myY = 0; // les varible pour truver sa position par rapport a la pos du depart

    public int rand; // pour avoire une valeur entre 30 et -30 // monter-descendre
    public int dir; // direction; 1 doirte -1 gauche

    public static int id = 0;
    public int myId;
    public int dep;

    //public boolean prete_a_accoucher;
    public boolean ready;
    public int waiting = 0;
    // ???
    public SimpleIntegerProperty depProperty = new SimpleIntegerProperty();
    public void initRand() {
        //Min + (Math.random() * (Max - Min))
        rand = -100 + (int)(Math.random()*(100 - (-100)));
        dir = (int) (Math.random() * 2);
    }

    public Demon(boolean male, Image image, double lX, double lY, int dep) {
        super();
        this.setImage(image);
        this.setFitWidth(60);
        this.setFitHeight(60);
        this.setLayoutX(lX);
        this.setLayoutY(lY);
        this.male = male;
        id++;
        this.myId = id;
        initRand();
        this.ready = true;
        this.dep=dep;
        this.depProperty.set(dep);
    }


    public int getMyId() {
        return myId;
    }

    public boolean isMale() {
        return this.male;
    }

    public void setRand() {
        this.rand = this.rand * (-1);
    }

    public void setDir() {
        this.dir = this.dir * (-1);
    }


    public void seDeplacer() {

        animation = new Timeline();
        animation.setCycleCount(Animation.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.millis(depProperty.get()), e -> {
            bougerDemon();
        });
        animation.getKeyFrames().add(kf);
        animation.play();
    }

    private void bougerDemon() {

        double xMin = this.getBoundsInParent().getMinX();
        double yMin = this.getBoundsInParent().getMinY();
        double xMax = this.getBoundsInParent().getMaxX();
        double yMax = this.getBoundsInParent().getMaxY();

        if (xMin < 0 || xMax > ((Pane) this.getParent()).getWidth()) {
            dir = dir * -1;
        }
        if (yMin < 0 || yMax > ((Pane) this.getParent()).getHeight()) {
            rand = rand * -1;
        }


        if (this.ready){
            DetecterCollision();
        }

        else {
            waiting += 20;
            if (waiting == 5000) {
                waiting = 0;
                ready = true;
            }
        }

        if (rand < 0) {
            rand += 1;
            if (rand == 0 ) {
                initRand();
                return;
            }
            myY -= 1;
            myX -= 1 * dir;
        } else {
            rand -= 1;
            if (rand == 0) {
                initRand();
                return;
            }
            myY += 1;
            myX += 1 * dir;
        }

        this.setX(myX);
        this.setY(myY);
    }


    public void DetecterCollision() {

        ListIterator<Demon> itr = GameController.listeDemon.listIterator();

        while (itr.hasNext()) {
            Demon demon = itr.next();
            if (this.getMyId() != demon.getMyId()) {
                if (this.getBoundsInParent().intersects(demon.getBoundsInParent())) {
                    // s'ils sont de sex opposé
                    if ((this.isMale() && !demon.isMale()) || (!this.isMale() && demon.isMale())) {
                        //System.out.println("MF");
                        if (this.ready && demon.ready) {

                            if (!this.isMale()) {
                                if(GameController.listeDemon.size()<15){
                                    double xMinCurr = this.getBoundsInParent().getMinX();
                                    double yMinCurr = this.getBoundsInParent().getMinY();
                                    this.makeBabyDemon(itr, xMinCurr, yMinCurr);
                                    this.notReadyForSex();
                                }
                            } else {
                                if(GameController.listeDemon.size()<15) {
                                    double xMinDem = demon.getBoundsInParent().getMinX();
                                    double yMinDem = demon.getBoundsInParent().getMinY();
                                    this.makeBabyDemon(itr, xMinDem, yMinDem);
                                    demon.notReadyForSex();
                                }
                            }
                        }

                    } else if(this.isMale() && demon.isMale()){
                        Pane pane = ((Pane) this.getParent());
                        // supprimé un des deux (celui avec le plus ptit id)
                        if (this.getMyId() > demon.getMyId()) {
                            demon.blood_splatter();
                            Timer t = new Timer();
                            TimerTask tt = new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> pane.getChildren().remove(demon));
                                }
                            };
                            t.schedule(tt, 360);
                            itr.remove();
                        }
                    }
                }
            }
        }
        if(this.getBoundsInParent().intersects(GameController.pistolero.getBoundsInParent())){
            if (GameController.pistolero.isProtection()) {
                //AudioClip audio = new AudioClip(getClass().getResource("../Sound/exlposion.wav").toString());
                //audio.play();
                Pane pane = ((Pane) this.getParent());
                this.blood_splatter();
                Timer t = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> pane.getChildren().remove(this));
                    }
                };
                t.schedule(tt, 360);
                GameController.listeDemon.remove(this);
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
                        Ball.stopEtEnregister(false);
                    } else {
                        // remmetre position depart
                        GameController.pistolero.relocate(380,420);
                        GameController.pistolero.invincible = true;
                    }
                }
            }
        }
    }

    private void makeBabyDemon(ListIterator<Demon> itr, double x, double y/*, Demon mere */) {
        int rand = (int) (Math.random() * 2);
        Demon dem = null;// = new Demon(true,new Image("sample/Icone/alien.png"),250,200);
        if (rand == 0) { // c est un mal
            // definir la position
            dem = new Demon(true, new Image("sample/Icone/male.png"), x, y,depProperty.get());
        } else { // femmelle // a changé le nom
            dem = new Demon(false, new Image("sample/Icone/femelle.png"), x, y,depProperty.get());
            dem.notReadyForSex();
        }
        itr.add(dem);
        // tjrs la mere qui donne naissance ( le pere peut etre supp en colision entre temp ce qui engendre des excp )
        //System.out.println("val rand after bearth "+rand+" id new demon "+dem.getMyId());
        ((Pane) this.getParent()).getChildren().add(dem);
        dem.seDeplacer();
    }

    public void notReadyForSex() {
        this.ready = false;
        this.waiting = 0;
    }


    // animation avant de mourir
    public void blood_splatter() {

        Image i1 = new Image("sample/exp/ex1.png");
        Image i2 = new Image("sample/exp/ex2.png");
        Image i3 = new Image("sample/exp/ex3.png");
        Image i4 = new Image("sample/exp/ex4.png");
        Image i5 = new Image("sample/exp/ex5.png");
        Image i6 = new Image("sample/exp/ex6.png");
        Image i7 = new Image("sample/exp/ex7.png");
        Image i8 = new Image("sample/exp/ex8.png");
        Image i9 = new Image("sample/exp/ex9.png");
        Image i10 = new Image("sample/exp/ex10.png");
        Image i11 = new Image("sample/exp/ex11.png");
        Image i12 = new Image("sample/exp/ex12.png");
        Image i13 = new Image("sample/exp/ex13.png");
        Image i14 = new Image("sample/exp/ex14.png");
        Image i15 = new Image("sample/exp/ex15.png");
        Image i16 = new Image("sample/exp/ex16.png");
        Image i17 = new Image("sample/exp/ex17.png");
        Image i18 = new Image("sample/exp/ex18.png");
        Image i19 = new Image("sample/exp/ex19.png");
        Image i20 = new Image("sample/exp/ex20.png");
        Image i21 = new Image("sample/exp/ex21.png");


        this.animation.stop();


        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(this.imageProperty(), i1)),
                new KeyFrame(Duration.millis(20), new KeyValue(this.imageProperty(), i2)),
                new KeyFrame(Duration.millis(40), new KeyValue(this.imageProperty(), i3)),
                new KeyFrame(Duration.millis(60), new KeyValue(this.imageProperty(), i4)),
                new KeyFrame(Duration.millis(80), new KeyValue(this.imageProperty(), i5)),
                new KeyFrame(Duration.millis(100), new KeyValue(this.imageProperty(), i6)),
                new KeyFrame(Duration.millis(120), new KeyValue(this.imageProperty(), i7)),
                new KeyFrame(Duration.millis(140), new KeyValue(this.imageProperty(), i8)),
                new KeyFrame(Duration.millis(160), new KeyValue(this.imageProperty(), i9)),
                new KeyFrame(Duration.millis(180), new KeyValue(this.imageProperty(), i10)),
                new KeyFrame(Duration.millis(200), new KeyValue(this.imageProperty(), i11)),
                new KeyFrame(Duration.millis(220), new KeyValue(this.imageProperty(), i12)),
                new KeyFrame(Duration.millis(240), new KeyValue(this.imageProperty(), i13)),
                new KeyFrame(Duration.millis(260), new KeyValue(this.imageProperty(), i14)),
                new KeyFrame(Duration.millis(280), new KeyValue(this.imageProperty(), i15)),
                new KeyFrame(Duration.millis(300), new KeyValue(this.imageProperty(), i16)),
                new KeyFrame(Duration.millis(320), new KeyValue(this.imageProperty(), i17)),
                new KeyFrame(Duration.millis(340), new KeyValue(this.imageProperty(), i18)),
                new KeyFrame(Duration.millis(360), new KeyValue(this.imageProperty(), i19)),
                new KeyFrame(Duration.millis(370), new KeyValue(this.imageProperty(), i20)),
                new KeyFrame(Duration.millis(380), new KeyValue(this.imageProperty(), i21))
        );
        tl.play();
    }

    public double getMyX() {
        return myX;
    }

    public double getMyY() {
        return myY;
    }
}
