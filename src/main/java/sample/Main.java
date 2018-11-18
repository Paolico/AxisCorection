package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Axis Corection");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

//        primaryStage.setOnCloseRequest(e -> {
//            Platform.exit();
//            System.exit(0);
//        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
