package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.UserSettings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLAppSettingsController implements Initializable {

    UserSettings userSettings;

    //<editor-fold desc="FXML properties">
    @FXML
    private Button button;

    @FXML
    private Label label;

    @FXML
    private Button buttonLoadInputDataFolder;

    @FXML
    private Button buttonSaveUserSettings;

    @FXML
    private Button buttonLoadOutputDataFolder;

    @FXML
    private Button btnLoadHeideinhainPrg;

    @FXML
    private Button btnLoadSiemensPrg;

    @FXML
    private TextField textFieldExtermalProgramPath;

    @FXML
    private TextField textFieldInputDataPath;

    @FXML
    private TextField textFieldIOutputDataPath;

    @FXML
    private TextField tfExtPrgHeidenhain;

    @FXML
    private TextField tfExtPrgSiemens;

    //</editor-fold>

    //<editor-fold desc="FXML actions">
    @FXML
    void handleOnClickLoadHeidenhainPrg(ActionEvent event) {
        String folder = openFileChooser();
        tfExtPrgHeidenhain.setText(folder);
    }

    @FXML
    void handleOnClickLoadSiemensPrg(ActionEvent event) {
        String folder = openFileChooser();
        tfExtPrgSiemens.setText(folder);
    }

    @FXML
    void handleOnClickLoadInputDataFolder(ActionEvent event) {
        String folder = openDirectoryChooser();
        textFieldInputDataPath.setText(folder);
    }

    @FXML
    void  handleOnClickLoadOutputDataFolder(ActionEvent event) {
      String folder = openDirectoryChooser();
      textFieldIOutputDataPath.setText(folder);
    }

    @FXML
    void onDragExited(ActionEvent event) {
        System.out.println();
    }
    //</editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //<editor-fold desc="Choosers">
    private String openDirectoryChooser (){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource Directory");
        Window window = buttonLoadInputDataFolder.getScene().getWindow();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop")); //pka
        File  selectedDirectory =  directoryChooser.showDialog(window);
        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        }
        return  null;
    }

    private String openFileChooser (){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Window window = btnLoadHeideinhainPrg.getScene().getWindow();
        fileChooser.setInitialDirectory(new File (System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop")); //pka
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
    //</editor-fold>

    public void setRtlUserSetting(UserSettings settings) {
        userSettings = settings;
        tfExtPrgHeidenhain.setText(String.valueOf(userSettings.getExternPrgPathHeideinhain()));
        tfExtPrgSiemens.setText(String.valueOf(userSettings.getExternPrgPathSiemens()));
        textFieldInputDataPath.setText(String.valueOf(userSettings.getInputDataFolderPath()));
        textFieldIOutputDataPath.setText(String.valueOf(userSettings.getOutputDataFolderPath()));
    }

    //<editor-fold desc="Getters">
    public UserSettings getUserSettings() {
        return userSettings;
    }

    public String getExternalHeidenhain() {
        return tfExtPrgHeidenhain.getText();
    }

    public String getExternalSiemens() {
        return tfExtPrgSiemens.getText();
    }

    public String getInput() {
        return textFieldInputDataPath.getText();
    }

    public String getOutput() {
        return textFieldIOutputDataPath.getText();
    }
    //</editor-fold>
}
