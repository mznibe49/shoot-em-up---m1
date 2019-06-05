package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent coverRoot = FXMLLoader.load(getClass().getResource("/sample/Views/cover.fxml"));
        Scene coverScene = new Scene(coverRoot, 800, 600);
        primaryStage.setTitle("El Pistolero");
        primaryStage.setScene(coverScene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
