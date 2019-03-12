package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.RtlUserSettings;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

//        Path aPath = Paths.get(System.getProperty("user.home") + "/a.txt");
//        Path bPath = Paths.get(System.getProperty("user.home") + "/b.txt");
//
//        FileUtils.copyFile(aPath.toFile(), bPath.toFile());

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Axis Corection");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

        RtlUserSettings.initConfiguration();

//        primaryStage.setOnCloseRequest(e -> {
//            Platform.exit();
//            System.exit(0);
//        });
    }



    public static void main(String[] args) {
        launch(args);
    }
}
