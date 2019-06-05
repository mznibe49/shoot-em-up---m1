package sample.Controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CoverController implements Initializable {

    @FXML private ImageView back;
    @FXML private Ellipse pistolero;
    private ArrayList<Image> list_image;
    private int currIndex = 0;
    @FXML private TextField texteF;
    @FXML private Label notavailable;

    public static boolean audioP = true;

    public static Timeline son;
    public static AudioClip audio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(audioP){
            audio = new AudioClip(getClass().getResource("../Sound/SkyFire.wav").toString());
            audio.play();
            son = new Timeline();
            son.setCycleCount(Animation.INDEFINITE);
            KeyFrame kf = new KeyFrame(Duration.seconds(67), e -> {
                audio.play();
            });

            son.getKeyFrames().add(kf);
            son.play();
            audioP = false;
        }

        back.setFitWidth(800);
        back.setFitHeight(630);
        back.setImage(new Image("sample/Icone/cover.gif"));
        list_image = new ArrayList<Image>();
        list_image.add(new Image("sample/Icone/pist/yellow.png"));
        list_image.add(new Image("sample/Icone/pist/black.png"));
        list_image.add(new Image("sample/Icone/pist/blue.png"));
        list_image.add(new Image("sample/Icone/pist/white.png"));
        pistolero.setFill(new ImagePattern(list_image.get(currIndex)));
    }



    private String getPathFromUrl(String url){
        String tab [] = url.split("/");
        StringBuilder res = new StringBuilder();
        for(int i = tab.length-3; i<tab.length; i++){
            res.append("/").append(tab[i]);
        }
        return res.toString();
    }

    public void demarrer(ActionEvent actionEvent)  throws IOException {
            if(texteF.getText().length()<3){
                notavailable.setVisible(true);
            } else {
                String url_file = list_image.get(currIndex).impl_getUrl();
                String path = getPathFromUrl(url_file);
                //  ecrire dans un fichier conf
                File file = new File("src/sample/config/config");
                FileWriter fw = new FileWriter(file);
                fw.write(path+"\n");
                fw.write(texteF.getText().replace(":","")+"\n");
                fw.flush();
                fw.close();
                Parent gameRoot = FXMLLoader.load(getClass().getResource("../Views/game.fxml"));
                Scene gameScene = new Scene(gameRoot, 800, 600);
                Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                stage.setScene(gameScene);

            }
    }

    public void charger() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un nouveau Jeu");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("fichiers textes","*.txt")
        );
        File fichierSelectionne =fileChooser.showOpenDialog((Stage)(pistolero.getScene().getWindow()));
        if(fichierSelectionne!=null){
            try {
                File fileConfig = new File("src/sample/config/config");
                FileWriter fw = new FileWriter(fileConfig);

                FileReader file = new FileReader(fichierSelectionne);
                BufferedReader br = new BufferedReader(file);
                String line;
                int i=0;
                while((line = br.readLine())!=null){
                    if(i==0 && !line.equals("SauvegardeJeu")){
                        error("Erreur Fichier Sauvegarde","Le Fichier de sauvegarde n'est pas le bon \n" +
                                "Veuillez Réessayer svp!!");
                        break;
                    }
                    fw.write(line+"\n");
                    i++;
                }
                fw.flush();
                fw.close();
                br.close();
                file.close();
                Parent gameRoot = FXMLLoader.load(getClass().getResource("../Views/game.fxml"));
                Scene gameScene = new Scene(gameRoot, 800, 600);
                Stage stage = (Stage)(pistolero.getScene().getWindow());
                stage.setScene(gameScene);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }else{
            error("Erreur Fichier Sauvegarde","Le Fichier de sauvegarde n'est pas le bon \n" +
                    "Veuillez Réessayer svp!!");
        }
    }

    private void error(String msg1, String msg2){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(msg1);
        errorAlert.setContentText(msg2);
        errorAlert.showAndWait();
    }


    public void param(ActionEvent actionEvent) {
        try {

            Parent paramRoot = FXMLLoader.load(getClass().getResource("../Views/parametre.fxml"));
            Scene paramScene = new Scene(paramRoot, 800, 600);
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(paramScene);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void score(ActionEvent actionEvent) {
        try {

            Parent scoreRoot = FXMLLoader.load(getClass().getResource("../Views/score.fxml"));
            Scene scoreScene = new Scene(scoreRoot, 800, 600);
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scoreScene);
            stage.show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void leftChoice() {
        currIndex++;
        if(currIndex > 3) currIndex = 0;
        pistolero.setFill(new ImagePattern(list_image.get(currIndex)));
    }

    public void rightChoice() {
        currIndex--;
        if(currIndex < 0) currIndex = 3;
        pistolero.setFill(new ImagePattern(list_image.get(currIndex)));
    }

}
