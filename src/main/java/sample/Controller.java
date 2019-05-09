package sample;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.stage.Window;
import model.*;
import sun.text.normalizer.UTF16;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller  implements Initializable {

  public Database database;

  private Boolean isSiemensFile = null;
  private Boolean afterInit = false;

  private SettingController settingController;
  private SettingOutputFileController settingOutputFileController;
  private static Gson gson;
  private UserSettings settings;

  // test
  SimpleStringProperty outContent = new SimpleStringProperty(null);

  //<editor-fold desc="Properties">
  private XYChart.Series tamSeries_1;
  private XYChart.Series zpetSeries_2;

  private XYChart.Series meanSeries;
  private XYChart.Series meanForwardSeries;
  private XYChart.Series meanBackSeries;
  private RtlParser rtlParser;
  private RtlFileWrap rtlFileWrap;
  private MeanMeasurementValue meanMeasurementValue;

  //</editor-fold>

  public Controller() {
    gson = new Gson();
  }

  //<editor-fold desc="FXML properties">
  @FXML
  private Button buttonConnection;

  @FXML
  private MenuItem close;

  @FXML
  private MenuItem open;

  @FXML
  private MenuItem show;

  @FXML
  private MenuItem miOutput;

  @FXML
  private MenuItem settingsComunication;

  @FXML
  private MenuItem settingsOutputFile;

  @FXML
  public MenuItem miSave;

  @FXML
  private MenuItem openSiemens;

  @FXML
  private MenuItem openHeideinhain;

  @FXML
  private TextArea inFileTextArea;

  @FXML
  private TextArea outFileTextArea;

  //<editor-fold desc="Chart Input Data">
  @FXML
  private LineChart<NumberAxis, NumberAxis> chartInputData;// = new LineChart(xAxisInput,yAxisInput);

  @FXML
  private NumberAxis xAxisInput;// = new NumberAxis(2008,2018,1);

  @FXML
  private NumberAxis yAxisInput;// = new NumberAxis(10,80,5);
  //</editor-fold>

  //<editor-fold desc="Chart Correction Data">
  @FXML
  private LineChart<NumberAxis, NumberAxis> chartCorrectionData;// = new LineChart(xAxisInput,yAxisInput);

  @FXML
  private NumberAxis xAxisCorrection;// = new NumberAxis(2008,2018,1);

  @FXML
  private NumberAxis yAxisCorrection;// = new NumberAxis(10,80,5);
  //</editor-fold>

  //</editor-fold>

  //<editor-fold desc="FXML Actions">

  //<editor-fold desc="Menu">


  @FXML
  void handleOnActionOpen(ActionEvent event) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    Window window = open.getParentPopup().getScene().getWindow();
    File file = null;
    if (UserSettings.getInstance().getInputDataFolderPath().isEmpty()) {
      file = new File(Constants.DEFAULT_FOLDER);
    } else {
      file = new File (UserSettings.getInstance().getInputDataFolderPath());
    }
    fileChooser.setInitialDirectory(file);
    FileChooser.ExtensionFilter extFilterTxt = new FileChooser.ExtensionFilter("RTL soubory (*.rtl)", "*.rtl");
    fileChooser.getExtensionFilters().add(extFilterTxt);
    File selectedFile = fileChooser.showOpenDialog(window);
    if (selectedFile != null) {
      inFileTextArea.clear();
      outFileTextArea.clear();
      meanSeries.getData().clear();
      meanForwardSeries.getData().clear();
      meanForwardSeries.getData().clear();
      String fileName = selectedFile.getAbsolutePath();
      Path path = Paths.get(fileName);
      rtlParser = new RtlParser(path);
      rtlFileWrap = rtlParser.parse();
      inFileTextArea.appendText(rtlParser.getText());
      if (rtlFileWrap.getRtlTargetData().getTargetCount() < 1) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText("Chybný formát souboru.");
//        alert.setContentText("Ooops, there was an error!");
        alert.showAndWait();
        return;
      }
      meanMeasurementValue = new MeanMeasurementValue(rtlFileWrap, rtlFileWrap.getRtlTargetData().getTargets().get(0));
      fillData(false);
      plotXY ();
      miOutput.disableProperty().setValue(false);
    }
  }

  @FXML
  void handleOnActionShow(ActionEvent event) {
      System.out.println("volani show");
  }

  @FXML
  void handleOnActionSave(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    File dir = null;
    if (UserSettings.getInstance().getOutputDataFolderPath().isEmpty()) {
      dir = new File(Constants.DEFAULT_FOLDER);
    } else {
      dir = new File (UserSettings.getInstance().getOutputDataFolderPath());
    }
    fileChooser.setInitialDirectory(dir);

    //Set extension filter
    if (isSiemensFile) {
      FileChooser.ExtensionFilter extFilterSIN = new FileChooser.ExtensionFilter("SIEMENS soubory (*.MPF)", "*.MPF");
      fileChooser.getExtensionFilters().add(extFilterSIN);
    } else {
      FileChooser.ExtensionFilter extFilterHH = new FileChooser.ExtensionFilter("HEIDENHAIN soubory (*.COM)", "*.COM");
      fileChooser.getExtensionFilters().add(extFilterHH);
    }
    FileChooser.ExtensionFilter extFilterTxt = new FileChooser.ExtensionFilter("TXT soubory (*.txt)", "*.txt");
    fileChooser.getExtensionFilters().add(extFilterTxt);

    //Show save file dialog
    Window window = open.getParentPopup().getScene().getWindow();
    File file = fileChooser.showSaveDialog(window);

    if(file != null){
      try {
        FileWriter fileWriter;
        fileWriter = new FileWriter(file);
        fileWriter.write(outFileTextArea.getText());
        fileWriter.close();
      } catch (IOException ex) {
        Logger.getLogger(Controller.class
            .getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  void handleOnActionClose(ActionEvent event) {
    Platform.exit();
    System.exit(0);
  }

  @FXML
  void handleOnActionOutput(ActionEvent event) {

    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("settingAxisCorrection.fxml"));
      System.out.println(fxmlLoader.getLocation());
      Parent root1 = fxmlLoader.load();

      // vytáhnutí controlleru
      SettingAxisCorrectionController settingAxisCorrectionController = fxmlLoader.<SettingAxisCorrectionController>getController();
      // předání objektu s nastavením
      settingAxisCorrectionController.setArgs2(rtlFileWrap, meanMeasurementValue, this);
      Stage stage = new Stage();
      stage.setScene(new Scene(root1));
      stage.setTitle("Výstupní soubor");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setMinWidth(455);
     // stage.setMaxWidth(460);
      stage.setMinHeight(440);
      stage.setMaxHeight(440);
      stage.show();
    } catch (Exception e){
      System.out.println("Chyba");
      e.printStackTrace();
    }
  }

  public void setOutContent(String content) {
    outFileTextArea.setText(content);
  }


  @FXML
  void handleOnActionSettingsCommunication(ActionEvent event) throws IOException {
      try {
          FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("setting.fxml"));
          System.out.println(fxmlLoader.getLocation());
          Parent root1 = fxmlLoader.load();

          // vytáhnutí controlleru
          settingController = fxmlLoader.<SettingController>getController();
          // předání objektu s nastavením
          settingController.setRtlUserSetting(database.getUserSettings());
          Stage stage = new Stage();
          stage.setScene(new Scene(root1));
          stage.initModality(Modality.APPLICATION_MODAL);
          stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
              public void handle(WindowEvent we) {
                settings = settingController.getUserSettings();
                settings.setExternPrgPathHeideinhain(settingController.getExternalHeidenhain());
                settings.setExternPrgPathSiemens(settingController.getExternalSiemens());
                settings.setInputDataFolderPath(settingController.getInput());
                settings.setOutputDataFolderPath(settingController.getOutput());
                try {
                  settings.save();
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
          });
          stage.setMaxHeight(220);
          stage.show();
      } catch (Exception e){
          System.out.println("Chyba");
          e.printStackTrace();
      }
  }

  @FXML
  void handleOnActionSettingsOutputFile(ActionEvent event) throws IOException {

    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("settingOutputFile.fxml"));
      Parent root1 =  fxmlLoader.load();
      // vytáhnutí controlleru
      settingOutputFileController = fxmlLoader.getController();
      settingOutputFileController.setAxisListConfigDatabase(database.getAxisListdatabase());
      Stage stage = new Stage();
      stage.setScene(new Scene(root1));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setMinWidth(800);
      stage.setMinHeight(400);

      stage.setOnCloseRequest(we -> {
        Map<String, ObservableList<AxisDef>> axisListConfigDatabase = settingOutputFileController.getAxisListConfigDatabase();
        try (FileWriter fw = new FileWriter(Constants.OUTPUT_FILE_SETTINGS)) {
          OutputFileSettings obj = new OutputFileSettings(axisListConfigDatabase);
          fw.write(gson.toJson(obj.getDatabase(), new TypeToken<Map<String, ArrayList<AxisDef>>>(){}.getType()));
          database.setAxisListdatabase(obj.getDatabase());
        } catch (IOException e) {
          e.printStackTrace();
        }
        System.out.println("TODO ulozeni nastaveni");

      });
      stage.show();
    } catch (Exception e){
       System.out.println(e.toString());
    }

  }

  @FXML
  void handleOnActionOpenHeideinhain(ActionEvent event) {
    String path = database.getUserSettings().getExternPrgPathHeideinhain();
    openExternalProgram(path);
  }

  @FXML
  void handleOnActionOpenSiemens(ActionEvent event) {
    String path = database.getUserSettings().getExternPrgPathSiemens();
    openExternalProgram(path);
  }


  //</editor-fold>

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    database = new Database();

    // Disable data symbols chart
    chartInputData.setCreateSymbols(false);
    chartCorrectionData.setCreateSymbols(false);

    chartCorrectionData.setAnimated(false);

    chartInputData.setTitle("Naměřené hodnoty");
    xAxisInput.setLabel("Pozice [mm]");
    yAxisInput.setLabel("Polohová úchylka [µm]");

    xAxisCorrection.setLabel("Pozice [mm]");
    yAxisCorrection.setLabel("Korekční hodnoty [µm]");

    meanSeries = new XYChart.Series();
    meanForwardSeries = new XYChart.Series();
    meanBackSeries = new XYChart.Series();

    chartCorrectionData.setTitle("Průměr z naměřených hodnot");

    inFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
    outFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));

    inFileTextArea.setEditable(false);
    outFileTextArea.setEditable(false);

    miOutput.disableProperty().setValue(true);
    miSave.disableProperty().setValue(true);

  }

  //<editor-fold desc="private support methods">
  public void fillData(boolean reload) {

    meanSeries.getData().clear();
    meanForwardSeries.getData().clear();
    meanBackSeries.getData().clear();

    if (!afterInit) {
      chartCorrectionData.getData().add(meanSeries);
      chartCorrectionData.getData().add(meanForwardSeries);
      chartCorrectionData.getData().add(meanBackSeries);
    }

    meanSeries.setName("Běh(+|-)");
    meanForwardSeries.setName("Běh(+)");
    meanBackSeries.setName("Běh(-)");

      int runCount = rtlFileWrap.getRtlRuns().getRunCount();
      int positionCount = rtlFileWrap.getRtlDeviations().getRun().size() / runCount;
      List<Double> data = rtlFileWrap.getRtlDeviations().getData();

    if (!reload) {

      List<Double> forward = new ArrayList<>();
      List<Double> backward = new ArrayList<>();
      int f = 1;
      int b = 1;

      for (int i = 1, ii = 0; i <= runCount; i++, ii++) {
        if (i % 2 != 0) { // tam
          for (int j = 0; j < positionCount; j++) {
            meanMeasurementValue.add(j, data.get(ii * positionCount + j), true, i);
            forward.add(j, data.get(ii * positionCount + j));
          }
          meanMeasurementValue.addToMap(new ArrayList<>(forward), f++, true);
          forward.clear();

        } else { // zpět
          for (int j = positionCount - 1, jj = 0; j >= 0; j--, jj++) {
            meanMeasurementValue.add(jj, data.get(ii * positionCount + j), false, i);
            backward.add(jj, data.get(ii * positionCount + j));
          }
          meanMeasurementValue.addToMap(new ArrayList<>(backward), b++, false);
          backward.clear();
        }
      }

    }

    List<Double> bothMean = meanMeasurementValue.getBothMean();
    List<Double> meanForward = meanMeasurementValue.getBackMean();
    List<Double> meanBackward = meanMeasurementValue.getForwardMean();
    List<Double>  X = FXCollections.observableArrayList(rtlFileWrap.getRtlTargetData().getTargets());

    for (int i = 0, j = 1; i < positionCount; i++, j++) {
      meanSeries.getData().add(new XYChart.Data(X.get(i), bothMean.get(i)));
      meanForwardSeries.getData().add(new XYChart.Data(X.get(i),meanForward.get (i)));
      meanBackSeries.getData().add(new XYChart.Data(X.get(i),meanBackward.get (i)));
    }

    database.setMeanMeasurementValue(meanMeasurementValue);
    database.setRtlFileWrap(rtlFileWrap);
    afterInit = true;
  }

  private void plotXY (){

    chartInputData.getData().clear();

    int runCount = rtlFileWrap.getRtlRuns().getRunCount();
    int positionCount = rtlFileWrap.getRtlDeviations().getRun().size() / runCount;


    HashMap<Integer, List<Double>> forwardMap = meanMeasurementValue.getForwardMap();
    HashMap<Integer, List<Double>> backMap = meanMeasurementValue.getBackMap();

    int it = 0;
    for(Map.Entry<Integer, List<Double>> entry: forwardMap.entrySet()) {
      XYChart.Series series = new XYChart.Series();
      series.setName(String.format("Běh %d %s", entry.getKey(), "(+)"));
      chartInputData.getData().add(series);
      for (Double value: entry.getValue()) {
        series.getData().add(new XYChart.Data(rtlFileWrap.getRtlTargetData().getTargets().get(it++), value));
      }
      it = 0;
    }

    for(Map.Entry<Integer, List<Double>> entry: backMap.entrySet()) {
      XYChart.Series series = new XYChart.Series();
      series.setName(String.format("Běh %d %s", entry.getKey(), "(-)"));
      chartInputData.getData().add(series);
      for (Double value: entry.getValue()) {
        series.getData().add(new XYChart.Data(rtlFileWrap.getRtlTargetData().getTargets().get(it++), value));
      }
      it = 0;
    }

  }

  private void openExternalProgram (String path) {
    if (path.isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Varování");
      alert.setHeaderText("Program nelze otevřít.");
      alert.setContentText("Není nastavena cesta k externímu programu.");
      alert.showAndWait();
      return;
    }
    File file = new File(path);
    URI uri = file.toURI();
    final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      try {
        desktop.browse(uri);
      } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Varování");
        alert.setHeaderText("Program se nepodařilo otevřít.");
        alert.setContentText("Zkontrolujte cestu k externímu programu.");
        alert.showAndWait();
      }
    } else {
      throw new UnsupportedOperationException("Browse action not supported");
    }
  }

  //</editor-fold>

  public void setIsSiemensFile(boolean isSiemens) {
    isSiemensFile = isSiemens;
  }

}
