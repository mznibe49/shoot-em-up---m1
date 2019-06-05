package sample.Controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.Others.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ParamController implements Initializable {
    @FXML private BorderPane borderpane;
    @FXML private TextField gaucheTextField;
    @FXML private TextField droiteTextField;
    @FXML private TextField hautTextField;
    @FXML private TextField basTextField;
    @FXML private TextField barTextField;
    @FXML private Slider vitesseDem;
    @FXML private ImageView back;

    @FXML private Slider vitessePis;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setFitWidth(800);
        back.setFitHeight(630);
        back.setImage(new Image("sample/Icone/cover.gif"));
        lectureFichier("src/sample/Fichiers/parametre.txt");
    }

    public String mettreMaj(String line){
        String line1= line.replace(" ","");
        if(line1.length()==1){
            return line1.toUpperCase();
        }
        return line1;
    }



    public void validerParam(ActionEvent actionEvent) throws IOException {
        String gaucheString = gaucheTextField.getText();
        String droiteString = droiteTextField.getText();
        String hautString = hautTextField.getText();
        String basString = basTextField.getText();
        String barString = barTextField.getText();
        try{
            File file = new File("src/sample/Fichiers/parametre.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bw= new BufferedWriter(fileWriter);
            bw.write("gauche:"+mettreMaj(gaucheString)+"\n");
            bw.write("droite:"+mettreMaj(droiteString)+"\n");
            bw.write("haut:"+mettreMaj(hautString)+"\n");
            bw.write("bas:"+mettreMaj(basString)+"\n");
            bw.write("space:"+mettreMaj(barString)+"\n");
            //bw.write("vitessePistolero:"+vitessePis.getValue()+"\n");
            bw.write("vitesseDemons:"+vitesseDem.getValue()+"\n");
            bw.flush();
            bw.close();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


        FXMLLoader coverPaneLoader = new FXMLLoader(getClass().getResource("../Views/cover.fxml"));
        Parent coverPane = coverPaneLoader.load();
        Scene coverScene = new Scene(coverPane, 800, 600);
        Stage stage = (Stage) borderpane.getScene().getWindow();
        stage.setScene(coverScene);
    }

    public void lectureFichier(String fileName){
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bf = new BufferedReader(fileReader);
            String line;
            while((line=bf.readLine())!=null){
                String ligne[] = line.split(":");
                switch (ligne[0]){
                    case "gauche":
                        gaucheTextField.setText(ligne[1]);
                        break;
                    case "droite":
                        droiteTextField.setText(ligne[1]);
                        break;
                    case "haut":
                        hautTextField.setText(ligne[1]);
                        break;
                    case "bas":
                        basTextField.setText(ligne[1]);
                        break;
                    case "space":
                        barTextField.setText(ligne[1]);
                        break;
                    case "vitessePistolero":
                        vitessePis.setValue(Double.parseDouble(ligne[1]));
                        break;
                    case "vitesseDemons":
                        vitesseDem.setValue(Double.parseDouble(ligne[1]));
                        break;
                    default:
                        break;
                }
            }
            bf.close();
            fileReader.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public void prDF(ActionEvent actionEvent) {
        lectureFichier("src/sample/Fichiers/ParamParDefault");
    }


    public void parametresSaisie(TextField textField, KeyEvent keyEvent){

        if(!keyEvent.getCode().isLetterKey() && !keyEvent.getCode().isKeypadKey())
            textField.setText(keyEvent.getCode().getName());
        else{
            textField.setText("");
        }
    }

    public void gaucheOnKey(KeyEvent keyEvent) {
        parametresSaisie(gaucheTextField,keyEvent);
    }

    public void droiteOnKey(KeyEvent keyEvent) {
        parametresSaisie(droiteTextField,keyEvent);
    }

    public void basOnKey(KeyEvent keyEvent) {
        parametresSaisie(basTextField,keyEvent);
    }

    public void hautOnKey(KeyEvent keyEvent) {
        parametresSaisie(hautTextField,keyEvent);
    }

    public void spaceOnKey(KeyEvent keyEvent) {
        parametresSaisie(barTextField,keyEvent);
    }

}
