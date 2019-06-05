package sample.Controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import sample.Others.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class GameController implements Initializable {
    @FXML private ImageView back;
    @FXML private Label Vie;
    @FXML private BorderPane borderpaneG;
    public static Pistolero  pistolero;
    public static Life life;
    @FXML private Label niveauLabel;
    private Niveau niveau;
    //@FXML private Slider vitessePis;
    @FXML private Slider vitesseDem;
    double vitessesDemJeu;
    int enleverNiveau=0;
    @FXML private Pane pane;
    @FXML private Label score;
    @FXML private Label name;
    @FXML private Label tempsjeu;
    public static ImageView imageNiveau;// = new ImageView();
    private static final int G = 0, D = 1, H = 2, B = 3, SP = 4;
    private Hashtable<String, Integer> key_assoc = new Hashtable<>();
    public static List<Demon> listeDemon = new ArrayList<>();
    public static List<Obstacle> listObstacle = new ArrayList<>();
    public static Boss boss;
    public static Player player;
    public static Cerceau cerceau;

    public static String avoirImagePistolero(String url){
        if(url.contains("yellow")) return "sample/Icone/pist/yellow.png";
        if(url.contains("blue")) return "sample/Icone/pist/blue.png";
        if(url.contains("black")) return "sample/Icone/pist/black.png";
        else return "sample/Icone/pist/white.png";
    }

    public void removeAll(){
        for(Demon d : listeDemon){
            pane.getChildren().remove(d);
        }
        for(Obstacle o : listObstacle){
            pane.getChildren().remove(o);
        }
        for(Ball b : Pistolero.listBall){
            pane.getChildren().remove(b);
        }
        pane.getChildren().remove(pistolero);

        if(boss != null){
            for(Ball b : Boss.listBall){
                pane.getChildren().remove(b);
            }
            pane.getChildren().remove(boss);
        }
        if(cerceau!=null)
            if(cerceau.getImage()!=null){
                pane.getChildren().removeAll(cerceau);
            }
        if (life!=null)
            if(life.getImage()!=null){
                pane.getChildren().removeAll(life);
            }
    }

    public static void stopAll(){

        if(life != null){
            if(life.getImage() != null)  life.deplacement.stop();
            life=null;
        }
        if(cerceau != null) {
            if(cerceau.getImage() != null) cerceau.deplacement.stop();
            cerceau=null;
        }

        ListIterator<Demon> itr_demon = listeDemon.listIterator();
        while (itr_demon.hasNext()) {
            Demon demon = itr_demon.next();
            demon.animation.stop();
            itr_demon.remove();
        }

        ListIterator<Obstacle> itr_obstacle = listObstacle.listIterator();
        while (itr_obstacle.hasNext()) {
            Obstacle o = itr_obstacle.next();
            o.animation.stop();
            itr_obstacle.remove();
        }

        ListIterator<Ball> itr_ball = Pistolero.listBall.listIterator();
        while (itr_ball.hasNext()) {
            Ball b = itr_ball.next();
            b.shoot.stop();
            itr_ball.remove();
        }
        if(boss!=null){
            boss.stopBoss();
            ListIterator<Ball> itr_balls = Boss.listBall.listIterator();
            while (itr_balls.hasNext()) {
                itr_balls.next();
                itr_balls.remove();
            }
            boss=null;
        }
        mytimer.stop();
        pistolero.stopPistolero();
    }

    public static void enregistrementScore(){

        try {
            File file = new File("src/sample/Fichiers/Score");
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(player.getName()+":"+player.getScore()+":"+Chrono.timeToHMS(iterations.get())+"\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quitter(ActionEvent actionEvent) throws IOException {
        System.out.println(tempsjeu.getText());
        removeAll();
        stopAll();
        Pistolero.myY = 0;
        Pistolero.myX = 0;
        CoverController.audio.play();
        CoverController.son.play();
        FXMLLoader coverPaneLoader = new FXMLLoader(getClass().getResource("../Views/cover.fxml"));
        Parent coverPane = coverPaneLoader.load();
        Scene coverScene = new Scene(coverPane, 800, 600);
        Stage stage = (Stage) borderpaneG.getScene().getWindow();
        stage.setScene(coverScene);

    }


    @FXML
    public void pause(){
        //CoverController.son.pause();
        //CoverController.audio
        mytimer.pause();
        pistolero.pausePistolero();
        for(Demon demon : listeDemon){
            demon.animation.pause();
        }
        for(Obstacle o : listObstacle){
            o.animation.pause();
        }
        if(life !=null ) {
            if (life.getImage() != null) {
                life.deplacement.pause();
            }
        }
        if(cerceau!=null) {
            if (cerceau.getImage()!=null) {
                cerceau.deplacement.pause();
            }
        }
        if(boss!=null){
            boss.pauseBoss();
        }
        vitesseDem.setDisable(false);
        //vitessePis.setDisable(false);
    }

    @FXML
    public void play(){

        if(pistolero.animation.getStatus() == Animation.Status.PAUSED){
            pistolero.playPistolero();
            for(Demon demon : listeDemon){
                demon.depProperty.bind(new SimpleIntegerProperty(16).add(vitesseDem.valueProperty().multiply(-10)));
                demon.seDeplacer();;
            }
            for(Obstacle o : listObstacle){
                o.animation.play();
            }
            mytimer.play();
            if(life !=null ) {
                if (life.getImage() != null) {
                    life.deplacement.play();
                }
            }
            if(cerceau!=null) {
                if (cerceau.getImage()!=null) {
                    cerceau.deplacement.play();
                }
            }
            if(boss!=null){
                boss.playBoss();
            }
        }
        vitesseDem.setDisable(true);
        //vitessePis.setDisable(true);
    }

    public static Timeline mytimer;


    public void removeBall(){
        ListIterator<Ball> itr_ball = Pistolero.listBall.listIterator();
        while (itr_ball.hasNext()) {
            Ball b = itr_ball.next();
            b.shoot.stop();
            pane.getChildren().remove(b);
            itr_ball.remove();
        }
        ListIterator<Obstacle> itr_obstacle = listObstacle.listIterator();
        while (itr_obstacle.hasNext()) {
            Obstacle o = itr_obstacle.next();
            o.animation.stop();
            pane.getChildren().remove(o);
            itr_obstacle.remove();
        }
        pane.getChildren().removeAll(cerceau,life);
        if(cerceau!=null) cerceau.setImage(null);
        if (life!=null) life.setImage(null);
    }

    static SimpleIntegerProperty iterations = new SimpleIntegerProperty(0);

    private void timer(int i){
        mytimer = new Timeline();
        mytimer.setCycleCount(Animation.INDEFINITE);
        iterations = new SimpleIntegerProperty(i);
        KeyFrame kf = new KeyFrame(Duration.seconds(1),e -> {
            tempsjeu.textProperty().bind( new SimpleStringProperty(Chrono.timeToHMS(iterations.get())));
            score.textProperty().bind( new SimpleIntegerProperty(player.getScore()).asString());
            Vie.textProperty().bind(new SimpleIntegerProperty(player.getVie()).asString());
            niveauLabel.textProperty().bind(new SimpleIntegerProperty(niveau.getNiveau()).asString());

            if(iterations.get()%5 == 0 /*&& !pistolero.isProtection()*/){
                Obstacle o = new Obstacle(randomPositionX(),0,pane);
                o.seDeplacer();
                listObstacle.add(o);
            }
            if(iterations.get()%8 == 0) {
                cerceau= new Cerceau(randomPositionX(),0,pane);
                cerceau.cerceauPouvoir();
            }
            if(iterations.get()%25 == 0) {
                life= new Life(randomPositionX(),pane);
                life.lancerLife();
            }
            if(listeDemon.size()==0){
                enleverNiveau=iterations.get();
                if(niveau.getNiveau()!=7) changerNiveau(niveau.getNiveau());
            }
            if(enleverNiveau+2== iterations.get()){
                pane.getChildren().remove(imageNiveau);
            }
            iterations.set(iterations.get()+1);
        });
        mytimer.getKeyFrames().add(kf);
        mytimer.play();
    }

    private void changerNiveau(int niv){
        if(niv==1){
            removeBall();
            chargerBgNiveau();
            imageNiveau = new ImageView(new Image("sample/Icone/n2.png"));
            pane.getChildren().add(imageNiveau);
            niveau= new Niveau(2,pane,genererDemonListe(12,6));
            vitesseDem.valueProperty().bind(new SimpleDoubleProperty(0.2));
            niveau.demarrerNiveau();
            faireBouger(false);
        }else if (niv==2){
            removeBall();
            chargerBgNiveau();
            imageNiveau=new ImageView(new Image("sample/Icone/n3.png"));
            pane.getChildren().add(imageNiveau);
            niveau= new Niveau(3,pane,genererDemonListe(12,8));
            vitesseDem.valueProperty().bind(new SimpleDoubleProperty(0.4));
            niveau.demarrerNiveau();
            faireBouger(false);
        }else if(niv==3){
            removeBall();
            chargerBgNiveau();
            imageNiveau=new ImageView(new Image("sample/Icone/n4.png"));
            pane.getChildren().add(imageNiveau);
            niveau= new Niveau(4,pane,genererDemonListe(10,10));
            vitesseDem.valueProperty().bind(new SimpleDoubleProperty(0.6));
            niveau.demarrerNiveau();
            faireBouger(false);
        }else if(niv==4){
            removeBall();
            chargerBgNiveau();
            imageNiveau=new ImageView(new Image("sample/Icone/n5.png"));
            pane.getChildren().add(imageNiveau);
            niveau= new Niveau(5,pane,genererDemonListe(8,12));
            vitesseDem.valueProperty().bind(new SimpleDoubleProperty(0.8));
            niveau.demarrerNiveau();
            faireBouger(false);
        }else  if(niv==5){
            removeBall();
            chargerBgNiveau();
            imageNiveau=new ImageView(new Image("sample/Icone/n6.png"));
            pane.getChildren().add(imageNiveau);
            niveau= new Niveau(6,pane,genererDemonListe(6,14));
            vitesseDem.valueProperty().bind(new SimpleDoubleProperty(1.0));
            niveau.demarrerNiveau();
            faireBouger(false);
        }else {
            if(boss==null) {
                removeBall();
                niveau= new Niveau(7,pane,null);
                chargerBgNiveau();
                niveau.demarrerNiveau();
                boss = new Boss(new Image("sample/Icone/alien.png"), pane,400);
                faireBouger(false);
            }
        }
    }

    private void chargerBgNiveau(){
        if(niveau.getNiveau()==1){
            back.setImage(new Image("sample/Background/b3.jpg"));
        }else if(niveau.getNiveau()==2){
            back.setImage(new Image("sample/Background/g2.jpg"));
        }else if(niveau.getNiveau()==3){
            back.setImage(new Image("sample/Background/g3.jpg"));
        }else if(niveau.getNiveau()==4){
            back.setImage(new Image("sample/Background/g4.jpg"));
        }else if(niveau.getNiveau()==5){
            back.setImage(new Image("sample/Background/g5.jpg"));
        }else if(niveau.getNiveau()==6){
            back.setImage(new Image("sample/Background/g6.jpg"));
        }else if(niveau.getNiveau()==7){
            back.setImage(new Image("sample/Background/g6.jpg"));
        }else{
            back.setImage(new Image("sample/Background/b3.jpg"));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        back.setFitWidth(800);
        back.setFitHeight(494);
        vitesseDem.setDisable(true);
        pane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        File file1 = new File("src/sample/Fichiers/parametre.txt");
        try  {
            FileReader fileReader = new FileReader(file1);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line=bufferedReader.readLine())!=null){
                String[] ligne = line.split(":");
                int dir = -1;
                switch (ligne[0]){
                    case "gauche": dir = G; break;
                    case "droite": dir = D; break;
                    case "haut":   dir = H; break;
                    case "bas": dir = B; break;
                    case "space": dir = SP; break;
                    case "vitessePistolero":
                        //vitessePis.setValue(Double.parseDouble(ligne[1]));
                        //break;
                    case "vitesseDemons":
                        vitesseDem.setValue(Double.parseDouble(ligne[1]));
                        break;
                    default:
                        break;
                }
                if (dir >= 0)
                    key_assoc.put(ligne[1], dir);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        File file = new File("src/sample/config/config");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int cpt = 1;
            while((line = br.readLine()) != null){
                try {
                    if(cpt == 1) {
                        if(line.equals("SauvegardeJeu")){
                            br.close();
                            fr.close();
                            demarrerPartieSauvegarde();
                            break;
                        }else{
                            br.close();
                            fr.close();
                            demarrerPartieNormal();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cpt++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    private void faireBouger(boolean b){
        for(Demon demon : listeDemon){
            demon.seDeplacer();
        }
        for(Obstacle ob : listObstacle){
            ob.seDeplacer();
        }
        for(Ball ball : Pistolero.listBall){
            ball.shoot();
        }
        if(b) pistolero.bouger();
        if(life !=null ) {
            if (life.getImage() != null) {
                life.lancerLife();
            }
        }
        if(cerceau!=null) {
            if (cerceau.getImage()!=null) {
                cerceau.cerceauPouvoir();
            }
        }
        if(boss!=null){
            boss.seDeplacer();
            boss.shoot();
            for(Ball ball: Boss.listBall){
                ball.shoot();
            }
        }

    }

    public static boolean  up = false;
    public static boolean  down = false;
    public static boolean  left = false;
    public static boolean  right = false;
    public static boolean  shoot = false;


    @FXML
    public void keyPressed(KeyEvent keyEvent) {
        String k_name = keyEvent.getCode().getName();
        Integer dir = key_assoc.get(k_name);
        try {
            switch (dir) {
                case 0:
                    left = true;
                    break;
                case 1:
                    right = true;
                    break;
                case 2:
                    up = true;
                    break;
                case 3:
                    down = true;
                    break;
                case 4: shoot = true; break;//spacebar
            }
        } catch (Exception e){
            System.out.println("Not know button pressed");
        }
    }

    private void demarrerPartieSauvegarde(){
        int niveauSauv=0;
        ArrayList<Demon> listeSauv= new ArrayList<>();
        File file = new File("src/sample/config/config");
        try  {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader= new BufferedReader((fileReader));
            int i=0;
            String line;
            while ((line=bufferedReader.readLine())!=null){
                if(i!=0){
                    String[] msg= line.split(":");
                    if(msg[0].equals("S")){
                        score.setText(msg[1]);
                    } else if(msg[0].equals("T")) {
                        tempsjeu.setText(msg[1]);
                    }else if(msg[0].equals("V")) {
                        Vie.setText(msg[1]);
                    }else if(msg[0].equals("NJ")) {
                        name.setText(msg[1]);
                    }else if(msg[0].equals("N")) {
                        niveauSauv=Integer.parseInt(msg[1]);
                    }else if(msg[0].equals("p")) {
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        boolean shild=avoirbool(r[2]);
                        String imgP=avoirImagePistole(r[3],shild);
                        pistolero = new Pistolero(new Image(imgP),x,y,shild);
                        pane.getChildren().add(pistolero);
                    }else if(msg[0].contains("d")) {
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        boolean mal = avoirbool(r[2]);
                        if(mal){
                            Demon demon= new Demon(true,new Image("sample/Icone/male.png"),x,y,16);
                            listeSauv.add(demon);
                        }else{
                            Demon demon= new Demon(false,new Image("sample/Icone/femelle.png"),x,y,16);
                            listeSauv.add(demon);
                        }
                    }else if(msg[0].contains("o")){
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        Obstacle ob = new Obstacle(x,y,pane);
                        listObstacle.add(ob);
                    }else if(msg[0].contains("b")){
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        Ball b = new Ball(x,y,pane);
                        Pistolero.listBall.add(b);
                        //k liste ballBoss; q boss; c cerceau; l life
                    }else if(msg[0].contains("q")){
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        boss= new Boss(new Image("sample/Icone/alien.png"), pane,x);
                        boss.bossVie=Integer.parseInt(r[2]);
                    } else if(msg[0].contains("k")){
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        Ball b = new Ball( x, y, pane );
                        b.setImage(new Image("sample/Icone/smugF.gif"));
                        Boss.listBall.add(b);
                        //listeball
                    }else if(msg[0].contains("c")){
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        cerceau=new Cerceau(x,y,pane);
                    }else if(msg[0].contains("l")){
                        String[] r=msg[1].split(",");
                        double x = Double.parseDouble(r[0]);
                        double y = Double.parseDouble(r[1]);
                        life=new Life(x,pane);
                        life.setLayoutY(y);
                    }else if(msg[0].equals("VD")){
                        vitesseDem.setValue(Double.parseDouble(msg[1]));
                    }
                }
                i++;
            }
            bufferedReader.close();
            fileReader.close();
            String namePlayer = name.getText();
            int scorePlayer = Integer.parseInt(score.getText());
            int viePlayer = Integer.parseInt(Vie.getText());
            player=new Player(namePlayer, scorePlayer,viePlayer);
            niveau= new Niveau(niveauSauv,pane,listeSauv);
            chargerBgNiveau();
            niveau.demarrerNiveau();
            appelerTimer(tempsjeu.getText());
            faireBouger(true);
            pause();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sauvegarder(ActionEvent actionEvent) {
        pause();
        FileChooser fileChooser = new FileChooser();
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) borderpaneG.getScene().getWindow();
        //Show save file dialog
        File file1 = fileChooser.showSaveDialog(stage);
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file1);
            BufferedWriter bw= new BufferedWriter(fileWriter);
            bw.write("SauvegardeJeu\n");
            bw.write("S:"+score.getText()+"\n");//score
            bw.write("N:"+niveau.getNiveau()+"\n"); //niveau
            bw.write("T:"+tempsjeu.getText()+"\n"); //temps
            bw.write("V:"+Vie.getText()+"\n"); //vie
            bw.write("NJ:"+name.getText()+"\n"); //name
            bw.write("VD:"+vitesseDem.getValue()+"\n");
            bw.write("p:"+(pistolero.getLayoutX()+Pistolero.myX)+","+(pistolero.getLayoutY()+Pistolero.myY)+","+pistolero.isProtection()+","
                    +pistolero.getImagePistolero().impl_getUrl().replace(":","")+"\n"); //pistolero
            for(int i=0; i<listeDemon.size(); i++)
                bw.write("d"+i+":"+(listeDemon.get(i).getLayoutX()+listeDemon.get(i).getMyX())+","+(listeDemon.get(i).getLayoutY()+listeDemon.get(i).getMyY())
                        +","+listeDemon.get(i).isMale()+"\n"); // demons
            int j=0;
            for(Obstacle obstacle : listObstacle){
                bw.write("o"+j+":"+(obstacle.getLayoutX()+obstacle.xObstacle)+","+(obstacle.getLayoutY()+obstacle.yObstacle+"\n")); //obstacles
                j++;
            }
            int k=0;
            for(Ball b : Pistolero.listBall){
                bw.write("b"+k+":"+(b.xball)+","+(+b.yball)+"\n"); //liste ball pistolero
                k++;
            }
            if(life!=null)
                if(life.getImage()!=null){
                    bw.write("l:"+(life.getLayoutX())+","+(life.getY())+"\n"); //life
                }
            if (cerceau!=null)
                if(cerceau.getImage()!=null){
                    bw.write("c:"+(cerceau.getLayoutX())+","+(cerceau.getY())+"\n"); //cerceau
                }
            if(boss!=null){
                bw.write("q:"+(boss.getLayoutX()+boss.myX)+","+(boss.getLayoutY()+boss.myY)+","+boss.bossVie+"\n"); //boss
                int l=0;
                for(Ball ball : Boss.listBall){
                    bw.write("k"+l+":"+(ball.xball)+","+(+ball.yball)+"\n");
                    l++;
                }
            }

            bw.flush();
            bw.close();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        pause();

    }


    private String avoirImagePistole(String msg,boolean shield){
        if(!shield) {
            if (msg.contains("yellow")) return "sample/Icone/pist/yellow.png";//
            else if (msg.contains("white")) return "sample/Icone/pist/white.png";//
            else if (msg.contains("black")) return "sample/Icone/pist/black.png";//
            return "sample/Icone/pist/blue.png";
        }else{
            if (msg.contains("yellow")) return "sample/Icone/pist/shield/yellow.png";
            else if (msg.contains("white")) return "sample/Icone/pist/shield/white.png";
            else if (msg.contains("black")) return "sample/Icone/pist/shield/black.png";
            return "sample/Icone/pist/blue.png";
        }
    }

    private boolean avoirbool(String msg){
        return msg.equals("true");
    }

    private void appelerTimer(String time){
        String r = time.replace(" ","");
        if(r.contains("m")){
            String[] r1= r.split("m");
            int res= 60*Integer.parseInt(r1[0]);
            String r2=r1[1].replace("s","");
            timer(res+Integer.parseInt(r2));
        }else{
            String r1=r.replace("s","");
            timer(Integer.parseInt(r1));
        }
    }

    private void demarrerPartieNormal(){
        File file = new File("src/sample/config/config");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader((fileReader));
            int i = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (i == 0) {
                    pistolero= new Pistolero(new Image(avoirImagePistole(line,false)),380,420,false);
                    pane.getChildren().add(pistolero);
                }else if(i==1){
                    name.setText(line);
                }
                i++;
            }
            bufferedReader.close();
            fileReader.close();
            player=new Player(name.getText(), 0,5);
            //demon.depProperty.bind(new SimpleIntegerProperty(16).add(vitesseDem.valueProperty().multiply(-10)));
            vitessesDemJeu=16 + vitesseDem.getValue()*(-10);
            System.out.println("vitesse au depart  " + vitessesDemJeu);
            System.out.println("vitesse au depart du S " + vitesseDem.getValue());

            niveau= new Niveau(1,pane,genererDemonListe((int)vitessesDemJeu,6));
            chargerBgNiveau();
            timer(0);
            niveau.demarrerNiveau();
            faireBouger(true);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private ArrayList<Demon> genererDemonListe(int dep, int number){
        ArrayList<Demon> listeSauv= new ArrayList<>();
        for(int i=0; i<number;i++){
            Demon demon = genererDemon(randomPositionX(),randomPositionY(),dep);
            listeSauv.add(demon);
        }
        return listeSauv;
    }

    private int randomPositionX(){
        return (int) (Math.random() * 750);
    }

    private int randomPositionY(){
        return (int) (Math.random() * 350);
    }

    private Demon genererDemon(double x, double y,int dep){
        int rand = (int) (Math.random() * 2);
        if(rand==0) return new Demon(true,new Image("sample/Icone/male.png"),x,y,dep);
        else return new Demon(false,new Image("sample/Icone/femelle.png"),x,y,dep);
    }


    @FXML
    public void keyReleased(KeyEvent keyEvent){
        String k_name = keyEvent.getCode().getName();
        Integer dir = key_assoc.get(k_name);
        try {
            switch (dir) {
                case 0:
                    left = false;
                    break;
                case 1:
                    right = false;
                    break;
                case 2:
                    up = false;
                    break;
                case 3:
                    down = false;
                    break;
            }
        } catch (Exception e){
            System.out.println("not known button released");
        }
    }
}