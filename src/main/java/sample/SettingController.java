package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.RtlUserSettings_old;
import model.RtlUserSettings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    RtlUserSettings userSettings;

    //<editor-fold desc="FXML properties">
    @FXML
    private Button button;

    @FXML
    private Label label;

    @FXML
    private Button buttonLoadInputDataFolder;

    @FXML
    private Button buttonLoadExternProgram;

    @FXML
    private Button buttonSaveUserSettings;

    @FXML
    private Button buttonLoadOutputDataFolder;

    @FXML
    private TextField textFieldExtermalProgramPath;

    @FXML
    private TextField textFieldInputDataPath;

    @FXML
    private TextField textFieldIOutputDataPath;

    //</editor-fold>

    //<editor-fold desc="FXML actions">
    @FXML
    void handleOnClickLoadExternProgram(ActionEvent event) {
        // TODO osetrit proti nezadani cesty
        String file = openFileChooser();
        textFieldExtermalProgramPath.setText(file);
    }

    @FXML
    void handleOnClickLoadInputDataFolder(ActionEvent event) {
        // TODO osetrit proti nezadani cesty
        String folder = openDirectoryChooser();
        textFieldInputDataPath.setText(folder);
    }

    @FXML
    void  handleOnClickLoadOutputDataFolder(ActionEvent event) {
        // TODO osetrit proti nezadani cesty
      String folder = openDirectoryChooser();
      textFieldIOutputDataPath.setText(folder);
    }

    @FXML
    void handleOnClickSaveUserSettings(ActionEvent event) {
        userSettings.setExternProgramPath(textFieldExtermalProgramPath.getText());
        userSettings.setInputDataFolderPath(textFieldInputDataPath.getText());
        userSettings.setOutputDataFolderPath(textFieldIOutputDataPath.getText());
        try {
            userSettings.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDragExited(ActionEvent event) {
        System.out.println();
    }
    //</editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // prikaz za sipkou -> provede se kdyz je zavolany Listener viz lambda
        textFieldIOutputDataPath.focusedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println(newVal ? "Focused" : "Unfocused");
        });
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
        Window window = buttonLoadExternProgram.getScene().getWindow();
        fileChooser.setInitialDirectory(new File (System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop")); //pka
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
    //</editor-fold>

    public void setRtlUserSetting(RtlUserSettings settings) {
        userSettings = settings;
        textFieldExtermalProgramPath.setText(String.valueOf(userSettings.getExternProgramPath()));
        textFieldInputDataPath.setText(String.valueOf(userSettings.getInputDataFolderPath()));
        textFieldIOutputDataPath.setText(String.valueOf(userSettings.getOutputDataFolderPath()));
    }

    //<editor-fold desc="Getters">
    public RtlUserSettings getUserSettings() {
        return userSettings;
    }

    public String getExtermal() {
        return textFieldExtermalProgramPath.getText();
    }

    public String getInput() {
        return textFieldInputDataPath.getText();
    }

    public String getOutput() {
        return textFieldIOutputDataPath.getText();
    }
    //</editor-fold>
}
