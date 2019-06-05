package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.Others.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ScoreController implements Initializable {

    @FXML private ImageView back;

    @FXML private BorderPane borderpane;
    @FXML private ListView listView;
    private Score[] score = new Score[tailletable()];
    private ObservableList<String> seasonList = FXCollections.observableArrayList();
    int tab[] = new int[tailletable()];

    private int tailletable(){
        int t=0;
        try {
            File file = new File("src/sample/Fichiers/Score");
            FileReader fileReader= new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line=bufferedReader.readLine())!=null){
                if(line.contains(":")){
                    t++;
                }
            }
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int t=0;
        back.setFitWidth(800);
        back.setFitHeight(630);
        back.setImage(new Image("sample/Icone/cover.gif"));
        try {
            File file = new File("src/sample/Fichiers/Score");
            FileReader fileReader= new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while((line=bufferedReader.readLine())!=null){
                if(line.contains(":")){
                    String[] res= line.split(":");
                    score[t]=new Score(res[0],Integer.parseInt(res[1].replace(" ","")),res[2]);
                    t++;
                }
            }
            bufferedReader.close();
            fileReader.close();

            Arrays.sort(score, Comparator.comparingInt(Score::getScore));

            Arrays.parallelSort(tab);
            for(int i=score.length -1 ; i>=0; i--){
                if(seasonList.size()<17) seasonList.add(score[i].getNom()+" : "+score[i].getScore()+" : "+score[i].getTemp());
            }
            listView.setItems(seasonList);

            listView.setOrientation(Orientation.VERTICAL);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void retourCover(ActionEvent actionEvent) throws IOException {
        FXMLLoader coverPaneLoader = new FXMLLoader(getClass().getResource("../Views/cover.fxml"));
        Parent coverPane = coverPaneLoader.load();
        Scene coverScene = new Scene(coverPane, 800, 600);
        Stage stage = (Stage) borderpane.getScene().getWindow();
        stage.setScene(coverScene);
    }

}
