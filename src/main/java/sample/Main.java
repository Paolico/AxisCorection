package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

//        Path aPath = Paths.get(System.getProperty("user.home") + "/a.txt");
//        Path bPath = Paths.get(System.getProperty("user.home") + "/b.txt");
//
//        FileUtils.copyFile(aPath.toFile(), bPath.toFile());

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Axis Corection");
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("stylesheet.css")));
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
