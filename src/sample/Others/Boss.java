package sample.Others;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import sample.Controllers.GameController;

import java.util.ArrayList;

public class Boss  extends ImageView {
    public Timeline deplacement,shootTimeLine;
    public static double myX = 0, myY = 0;
    public Pane pane;
    public boolean gauche=true;
    public int bossVie;
    public ProgressBar progressBar;
    public static ArrayList<Ball> listBall = new ArrayList<>();

    public Boss(Image image, Pane pane,double x){
        this.setImage(image);
        this.setRotate(180);
        this.setFitWidth(150);
        this.setFitHeight(150);
        this.setLayoutX(x); //x=400
        this.setLayoutY(0);
        this.pane=pane;
        this.bossVie=35;
        this.progressBar = new ProgressBar();
        this.progressBar.setLayoutX(x+25);
        progressBar.setPrefHeight(10);
        pane.getChildren().addAll(this,this.progressBar);
    }


    public void seDeplacer() {

        deplacement = new Timeline();
        deplacement.setCycleCount(Animation.INDEFINITE);

        KeyFrame kf = new KeyFrame(Duration.millis(16), e -> {
            bougerBoss();
            this.progressBar.progressProperty().bind(new SimpleDoubleProperty(bossVie).divide(25));
        });

        deplacement.getKeyFrames().add(kf);
        deplacement.play();
    }

    public void shoot(){
        shootTimeLine = new Timeline();
        shootTimeLine.setCycleCount(Animation.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), e -> {
            double x = this.getLayoutX() + myX + (this.getFitWidth()/4) ;
            double y = this.getLayoutY() + myY + (this.getFitHeight()/2) ;
            Ball b = new Ball( x, y, pane );
            b.setImage(new Image("sample/Icone/smugF.gif"));
            b.shoot();
            listBall.add(b);
        });

        shootTimeLine.getKeyFrames().add(kf);
        shootTimeLine.play();
    }

    private void bougerBoss() {
        int dx=0;
        if (gauche) {
            dx -=3;
            if(canMove(dx)) {
                moveBoss(dx);
            }else{
                gauche=false;
            }
        }
        if(!gauche){
            dx+=3;
            if(canMove(dx)) {
                moveBoss(dx);
            }else{
                gauche=true;
            }
        }
    }

    public boolean canMove(double dx){
        double min_x = dx + this.getBoundsInParent().getMinX();
        double max_x = dx + this.getBoundsInParent().getMaxX();
        if (min_x < 0 || max_x > pane.getWidth())
            return false;
        return true;
    }

    void moveBoss(double dx){

        if(dx == 0 ) return ;

        double currX = this.getX();

        double x = currX + dx;
        this.progressBar.setLayoutX(progressBar.getLayoutX()+dx);

        this.setX(x);
        myX = x;

    }

    private static final int COLUMNS = 9;
    private static final int COUNT = 81;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    public void dead() {
        this.deplacement.stop();
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
            pane.getChildren().removeAll(this,this.progressBar);
        });
        anim.play();
    }

    public void pauseBoss(){
        this.deplacement.pause();
        this.shootTimeLine.pause();
        for(Ball b : listBall){
            b.shoot.pause();
        }
    }

    public void playBoss(){
        this.deplacement.play();
        this.shootTimeLine.play();
        for(Ball b : listBall){
            b.shoot.play();
        }
    }

    public void stopBoss(){
        this.deplacement.stop();
        this.shootTimeLine.stop();
        for(Ball b : listBall){
            b.shoot.stop();
        }
    }

    public void setBossVie(int bossVie) {
        this.bossVie = bossVie;
    }
}