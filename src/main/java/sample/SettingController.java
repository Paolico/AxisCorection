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
import model.RtlUserSettings;

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

    @FXML
    private String InputDataFolderPath;


    @FXML
    void handleOnClickLoadExternProgram(ActionEvent event) {
        // TODO osetrit proti nezadani cesty
        settings.setExternProgramPath(openFileChooser());
        textFieldExtermalProgramPath.setText(settings.getExternProgramPath());
    }

    @FXML
    void handleOnClickLoadInputDataFolder(ActionEvent event) {
        // TODO osetrit proti nezadani cesty
        settings.setInputDataFolderPath(openDirectoryChooser());
        textFieldInputDataPath.setText(settings.getInputDataFolderPath());
    }

    @FXML
    void  handleOnClickLoadOutputDataFolder(ActionEvent event) {
        // TODO osetrit proti nezadani cesty
        settings.setOutputDataFolderPath(openDirectoryChooser());
        textFieldIOutputDataPath.setText(settings.getOutputDataFolderPath());
    }

    private  RtlUserSettings settings = new RtlUserSettings();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDefaultSettings();
    }

    private void initDefaultSettings() {
        RtlUserSettings userSettings = RtlUserSettings.loadUserSettings();
        textFieldExtermalProgramPath.setText(String.valueOf(userSettings.getExternProgramPath()));
        textFieldInputDataPath.setText(String.valueOf(userSettings.getInputDataFolderPath()));
        textFieldIOutputDataPath.setText(String.valueOf(userSettings.getOutputDataFolderPath()));
    }

    private String openDirectoryChooser (){

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource Directory");
        Window window = buttonLoadInputDataFolder.getScene().getWindow();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop")); //pka
        File  selectedDirectory =  directoryChooser.showDialog(window);
        if (selectedDirectory != null) {

            return selectedDirectory.getAbsolutePath();

        }
        else {
            return  null;
        }

    }

    private String openFileChooser (){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Window window = buttonLoadExternProgram.getScene().getWindow();
        fileChooser.setInitialDirectory(new File (System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop")); //pka
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {

            return selectedFile.getAbsolutePath();
        }
        else {
            return null;
        }

    }




}
