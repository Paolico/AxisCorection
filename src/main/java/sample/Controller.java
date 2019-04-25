package sample;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import model.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller  implements Initializable {

  public Database database;

  private SettingController settingController;
  private SettingOutputFileController settingOutputFileController;
  private static Gson gson;

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
  private MenuItem axisCorrectionSetting;

  @FXML
  private MenuItem settingsComunication;

  @FXML
  private MenuItem settingsOutputFile;

  @FXML
  private MenuItem about;

  @FXML
  private MenuItem save;

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
    fileChooser.setInitialDirectory(new File (UserSettings.getInstance().getInputDataFolderPath()) );
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
      meanMeasurementValue = new MeanMeasurementValue(rtlFileWrap, /*todo user input*/rtlFileWrap.getRtlTargetData().getTargets().get(0));
      calculate(false);
      plotXY ();
      System.out.println("volani show");
    }
  }

  @FXML
  void handleOnActionShow(ActionEvent event) {
      System.out.println("volani show");
  }

  @FXML
  void handleOnActionSave(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();

    //Set extension filter
    FileChooser.ExtensionFilter extFilterTxt = new FileChooser.ExtensionFilter("TXT soubory (*.txt)", "*.txt");
    FileChooser.ExtensionFilter extFilterHH = new FileChooser.ExtensionFilter("HEIDENHAIN soubory (*.COM)", "*.COM");
    FileChooser.ExtensionFilter extFilterSIN = new FileChooser.ExtensionFilter("SIEMENS soubory (*.MPF)", "*.MPF");

    fileChooser.getExtensionFilters().add(extFilterTxt);
    fileChooser.getExtensionFilters().add(extFilterHH);
    fileChooser.getExtensionFilters().add(extFilterSIN);


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
  void handleOnActionAxisCorrectionSetting(ActionEvent event) {

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
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setMinWidth(460);
      stage.setMaxWidth(460);
      stage.setMinHeight(380);
      stage.setMaxHeight(380);
      stage.show();
    } catch (Exception e){
      System.out.println("Chyba");
    }
  }

  public void  setOutContent(String content) {
    outFileTextArea.setText(content);
  }


  @FXML
  void handleOnActionSettingsCommunication(ActionEvent event) throws IOException {
      //TODO
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
                UserSettings settings = settingController.getUserSettings();
                settings.setExternProgramPath(settingController.getExtermal());
                settings.setInputDataFolderPath(settingController.getInput());
                settings.setOutputDataFolderPath(settingController.getOutput());
                try {
                  settings.save();
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
          });
          stage.setMaxHeight(180);
          stage.show();
      } catch (Exception e){
          System.out.println("Chyba");
      }
  }

  @FXML
  void handleOnActionSettingsOutputFile(ActionEvent event) throws IOException {

    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("settingOutputFile.fxml"));
      Parent root1 =  fxmlLoader.load();
      // vytáhnutí controlleru
      settingOutputFileController = fxmlLoader.getController();
     // if ( database.getAxisListdatabase() !=null) {
        settingOutputFileController.setAxisListConfigDatabase(database.getAxisListdatabase());
     // }
      Stage stage = new Stage();
      stage.setScene(new Scene(root1));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setMinWidth(800);

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
  void handleOnActionAbout(ActionEvent event) {
    System.out.println("Pavel Kadlecik");
  }
  //</editor-fold>

  //<editor-fold desc="Buttons">
  @FXML
  void onClick_buttonConnection(ActionEvent event) {

    Path aPath = Paths.get(System.getProperty("user.home") );

    Client.Connect("192.168.56.100", 19000 ,5);
    Client.SendFile("a.txt",aPath.toString());
  }
  //</editor-fold>


  @Override
  public void initialize(URL location, ResourceBundle resources) {

    database = new Database();

    // Disable data symbols chart
    chartInputData.setCreateSymbols(false);
    chartCorrectionData.setCreateSymbols(false);

    //Defining Label for Axis
    xAxisInput.setLabel("Pozice [mm]");
    yAxisInput.setLabel("Měřené hodnoty [um]");

    xAxisCorrection.setLabel("Pozice [mm]");
    yAxisCorrection.setLabel("Korekční hodnoty [um]");

    //creating the meanSeries
    tamSeries_1 = new XYChart.Series();
    zpetSeries_2 = new XYChart.Series();

    meanSeries = new XYChart.Series();
    meanForwardSeries = new XYChart.Series();
    meanBackSeries = new XYChart.Series();

    //setting name and the date to the meanSeries
    tamSeries_1.setName("Běh 1 tam");
    zpetSeries_2.setName("Běh 1 zpět");

    meanSeries.setName("Korekce pozic");
    meanForwardSeries.setName("Průměrná korekce tam");
    meanBackSeries.setName("Průměrná korekce zpět");

    //adding meanSeries to the linechart
    chartInputData.getData().add(tamSeries_1);
    chartInputData.getData().add(zpetSeries_2);

    chartCorrectionData.getData().add(meanSeries);
    chartCorrectionData.getData().add(meanForwardSeries);
    chartCorrectionData.getData().add(meanBackSeries);

    inFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
    outFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));

    inFileTextArea.setEditable(false);
    outFileTextArea.setEditable(false);
    // test
    //outFileTextArea.promptTextProperty().bind(outContent);
  }

  //<editor-fold desc="private support methods">
  public void calculate(boolean reload) {

    meanSeries.getData().clear();
    meanForwardSeries.getData().clear();
    meanBackSeries.getData().clear();

      int runCount = rtlFileWrap.getRtlRuns().getRunCount();
      int positionCount = rtlFileWrap.getRtlDeviations().getRun().size() / runCount;
      List<Double> data = rtlFileWrap.getRtlDeviations().getData();

    if (!reload) {

      for (int i = 1, ii = 0; i <= runCount; i++, ii++) {
        if (i % 2 != 0) { // tam
          for (int j = 0; j < positionCount; j++) {
            meanMeasurementValue.add(j, data.get(ii * positionCount + j), true);
          }
        } else { // zpět
          for (int j = positionCount - 1, jj = 0; j >= 0; j--, jj++) {
            meanMeasurementValue.add(jj, data.get(ii * positionCount + j), false);
          }
        }
      }

    }

    List<Double> bothMean = meanMeasurementValue.getBothMean();
    List<Double> meanForward = meanMeasurementValue.getBackMean();
    List<Double> meanBackward = meanMeasurementValue.getForwardMean();
    List<Double>  X = FXCollections.observableArrayList(rtlFileWrap.getRtlTargetData().getTargets());

    for (int i = 0, j = 1; i < positionCount; i++, j++) {
      meanSeries.getData().add(new XYChart.Data(/*todo by user input*/X.get(i), bothMean.get(i)));
      meanForwardSeries.getData().add(new XYChart.Data(/*todo by user input*/X.get(i),meanForward.get (i)));
      meanBackSeries.getData().add(new XYChart.Data(/*todo by user input*/X.get(i),meanBackward.get (i)));

    }

    database.setMeanMeasurementValue(meanMeasurementValue);
    database.setRtlFileWrap(rtlFileWrap);
  }

  private void plotXY (){

    int runCount = rtlFileWrap.getRtlRuns().getRunCount();
    int positionCount = rtlFileWrap.getRtlDeviations().getRun().size() / runCount;

    List<Double> Tam = new ArrayList<Double>() ; //meanMeasurementValue.getForward();
    List<Double> Zpet = new ArrayList<Double>(); //meanMeasurementValue.getForward();


    List<Double> Vsechny = rtlFileWrap.getRtlDeviations().getData();
    List<Integer> runs =rtlFileWrap.getRtlDeviations().getRun();

    for (int i = 0; i < 126; i++) {
      if (runs.get(i) == 1) {
        Tam.add(Vsechny.get(i));
      } else if (runs.get(i) == 2) {
        Zpet.add(Vsechny.get(i));
      }
    }

    Zpet = Lists.reverse(Zpet);

    for (int i = 0, j = 1; i < positionCount; i++, j++) {
//      XYChart.Series series = new XYChart.Series();
//      series.setName(String.format("Beh %d %s", 5, "tam")); // i % 2 = 0 ? "tam" : "zpet"
//      chartCorrectionData.getData().add(meanSeries);
      tamSeries_1.getData().add(new XYChart.Data(/*todo by user input*/rtlFileWrap.getRtlTargetData().getTargets().get(i), Tam.get(i)));
    }


    for (int i = 0, j = 2; i < positionCount; i++, j++) {
      zpetSeries_2.getData().add(new XYChart.Data(/*todo by user input*/rtlFileWrap.getRtlTargetData().getTargets().get(i), Zpet.get(i)));
    }

  }

  //</editor-fold>

}
