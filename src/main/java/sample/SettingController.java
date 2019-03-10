package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.MeanMeasurementValue;
import model.RtlParser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML
    private Button button;

    @FXML
    private Label label;

    @FXML
    private Button buttonLoadInputDataFolder;

    @FXML
    private TextField textFieldIOutputDataPath;

    @FXML
    private Button buttonLoadExternProgram;

    @FXML
    private Button buttonLoadOutputDataFolder;

    @FXML
    private TextField textFieldExtermalProgramPath;

    @FXML
    private TextField textFieldInputDataPath;
    private String InputDataFolderPath;


    @FXML
    void handleOnClickLoadExternProgram(ActionEvent event) {

    }

    @FXML
    void handleOnClickLoadInputDataFolder(ActionEvent event) {

        openDirectoryChooser (textFieldInputDataPath);

    }

    @FXML
    void handleOnClickLoadOutpuDataFolder(ActionEvent event) {

        openDirectoryChooser (textFieldIOutputDataPath);

    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void openDirectoryChooser (TextField textField){

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
        Window window = buttonLoadInputDataFolder.getScene().getWindow();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop")); //pka
        File  selectedDirectory =  directoryChooser.showDialog(window);
        // File selectedDirectory = (FileChooser)  directoryChooser.showOpenDialog(window);
        if (selectedDirectory != null) {
            InputDataFolderPath = selectedDirectory.getAbsolutePath();
            textField.setText(InputDataFolderPath);
        }

    }
}
