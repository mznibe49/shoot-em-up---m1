package sample.Others;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import sample.Controllers.GameController;
import java.util.ArrayList;


public class Pistolero extends ImageView {

    public String dir="u";

    public static double myX = 0, myY = 0; // les varible pour truver sa position par rapport a la pos du depart
    public boolean pause;
    Image imagePistolero;
    public boolean protection;
    public Timeline animation;
    public static double posPist;
    public static ArrayList<Ball> listBall = new ArrayList<>();
    public boolean invincible;// = false;
    public int waiting = 0;



    public Pistolero(Image image, double x, double y,boolean protection){
        this.setImage(image);
        this.setFitWidth(120);
        this.setFitHeight(72);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.pause = false;
        this.imagePistolero=image;
        this.protection=protection;
        this.invincible = false;
    }

    public void pausePistolero(){
        this.animation.pause();
        this.pause = true;
        for(Ball ball : listBall){
            ball.shoot.pause();
        }
    }

    public void stopPistolero(){
        this.animation.stop();
    }


    public void setImagePistolero(Image imagePistolero) {
        this.setImage(imagePistolero);
        this.imagePistolero=imagePistolero;
    }

    public Image getImagePistolero() {
        return this.imagePistolero;
    }

    public void playPistolero(){
        this.animation.play();
        this.pause = false;
        for(Ball ball : listBall){
            ball.shoot.play();
        }
    }

    public boolean canMove(double dx, double dy){
        double min_x = dx + this.getBoundsInParent().getMinX();
        double max_x = dx + this.getBoundsInParent().getMaxX();
        double min_y = dy + this.getBoundsInParent().getMinY();
        double max_y = dy + this.getBoundsInParent().getMaxY();
        if (min_x < 0 || max_x > ((Pane)this.getParent()).getWidth() || min_y < 0 || max_y > ((Pane) this.getParent()).getHeight())
            return false;
        return true;
    }
    

    public void shoot() {

        if(!pause) {
            double x = this.getLayoutX() + myX + (this.getFitWidth()/4) ;
            double y = this.getLayoutY() + myY - (this.getFitHeight()/2) ;

            Pane parent = ((Pane) this.getParent());
            Ball b = new Ball( x, y, parent );
            b.shoot();
            AudioClip audio = new AudioClip(getClass().getResource("../Sound/tir.wav").toString());
            audio.play();
            listBall.add(b);
        }
    }


     public void bouger(){

        animation = new Timeline();
        animation.setCycleCount(Animation.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.millis(12),e -> {

            posPist = this.getBoundsInParent().getMinY();

            if(!pause) {

                if(this.invincible){
                    waiting += 12;
                    if (waiting > 2000) {
                        waiting = 0;
                        invincible = false;
                    }
                }

                int dx = 0, dy = 0;

                if(GameController.up){
                    dy -= 3; dir = "u";
                }
                if(GameController.right){
                    dx += 3; dir = "r";
                }
                if(GameController.down){
                    dy += 3; dir = "d";
                }
                if(GameController.left){
                    dx -=3; dir = "l";
                }
                if(GameController.shoot){
                    shoot();
                    GameController.shoot = false;
                }
                if (canMove(dx, dy))
                    movePist(dx, dy);
            }

        });

        animation.getKeyFrames().add(kf);
        animation.play();
    }

    void movePist(double dx, double dy){

        if(dx == 0 && dy == 0) return ;

        double currX = this.getX();
        double currY = this.getY();

        double x = currX + dx;
        double y = currY + dy;


        this.setX(x);
        this.setY(y);

        myX = x;
        myY = y;

    }

    public boolean isProtection() {
        return protection;
    }

    public void setProtection(boolean protection) {
        this.protection = protection;
    }

    private static final int COLUMNS  =   9;
    private static final int COUNT    =  81;
    private static final int OFFSET_X =  0;
    private static final int OFFSET_Y =  0;
    private static final int WIDTH    = 100;
    private static final int HEIGHT   = 100;

    public void dead(){

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
        GameController.stopAll();
        anim.play();
    }

}
