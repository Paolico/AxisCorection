package sample;

import com.google.common.collect.Lists;
import javafx.application.Platform;
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
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ResourceBundle;

public class Controller  implements Initializable {

  SettingController settingController;

  //<editor-fold desc="Properties">
  // Pavlovo
  private XYChart.Series tamSeries_1;
  private XYChart.Series zpetSeries_2;

  private XYChart.Series meanSeries;
  private XYChart.Series meanForwardSeries;
  private XYChart.Series meanBackSeries;
  private RtlParser rtlParser;
  private RtlFileWrap rtlFileWrap;
  private MeanMeasurementValue meanMeasurementValue;
  private RtlUserSettings settings;
  private Client client;

  //</editor-fold>

  public Controller() {
    settings = RtlUserSettings.getInstance();
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
    fileChooser.setInitialDirectory(new File (RtlUserSettings.getInstance().getInputDataFolderPath()) );
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
      calculate();
      createOutputFile();

      plotXY ();
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
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
    fileChooser.getExtensionFilters().add(extFilter);

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
  void handleOnActionSettingsComunication(ActionEvent event) throws IOException {
      //TODO
      try {
          FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("setting.fxml"));
          Parent root1 = (Parent)fxmlLoader.load();
          // vytáhnutí controlleru
          settingController = fxmlLoader.<SettingController>getController();
          // předání objektu s nastavením
          settingController.setRtlUserSetting(settings);
          Stage stage = new Stage();
          stage.setScene(new Scene(root1));
          stage.initModality(Modality.APPLICATION_MODAL);
          stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
              public void handle(WindowEvent we) {
                RtlUserSettings settings = settingController.getUserSettings();
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
      System.out.println("OKNO NASTAVENI");
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("settings2.fxml"));
      Parent root2 = (Parent) fxmlLoader.load();
      // vytáhnutí controlleru
      settingController = fxmlLoader.<SettingController>getController();
      // předání objektu s nastavením
      settingController.setRtlUserSetting(settings);
      Stage stage = new Stage();
      stage.setScene(new Scene(root2));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        public void handle(WindowEvent we) {
         // RtlUserSettings settings = settingController.getUserSettings();
         // settings.setExternProgramPath(settingController.getExtermal());
         // settings.setInputDataFolderPath(settingController.getInput());
         // settings.setOutputDataFolderPath(settingController.getOutput());
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
      System.out.println(e.toString());
    }

  }


  @FXML
  void handleOnActionAbout(ActionEvent event) {
    System.out.println("Pavel Kadlecik.sro");
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

//    xAxisInput.setAutoRanging(false);
//    xAxisInput.setLowerBound(2008);
//    xAxisInput.setUpperBound(2018);
//    xAxisInput.setTickUnit(1);
//
//    yAxisInput.setAutoRanging(false);
//    yAxisInput.setLowerBound(0);
//    yAxisInput.setUpperBound(80);
//    yAxisInput.setTickUnit(10);

    inFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
    outFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
  }

  //<editor-fold desc="private support methods">
  private void calculate() {
    int runCount = rtlFileWrap.getRtlRuns().getRunCount();
    int positionCount = rtlFileWrap.getRtlDeviations().getRun().size() / runCount;
    List<Double> data = rtlFileWrap.getRtlDeviations().getData();
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
    List<Double> bothMean = meanMeasurementValue.getBothMean();
    List<Double> meanForward = meanMeasurementValue.getBackMean();
    List<Double> meanBackward = meanMeasurementValue.getForwardMean();
    for (int i = 0, j = 1; i < positionCount; i++, j++) {
      meanSeries.getData().add(new XYChart.Data(/*todo by user input*/rtlFileWrap.getRtlTargetData().getTargets().get(i), bothMean.get(i)));
      meanForwardSeries.getData().add(new XYChart.Data(/*todo by user input*/rtlFileWrap.getRtlTargetData().getTargets().get(i),meanForward.get (i)));
      meanBackSeries.getData().add(new XYChart.Data(/*todo by user input*/rtlFileWrap.getRtlTargetData().getTargets().get(i),meanBackward.get (i)));

    }
  }

  private void createOutputFile() {
    // todo variable position
    outFileTextArea.appendText("BEGIN AXIS-X.COM  DATUM:-875.0000 DISTANCE:50.0000");
    outFileTextArea.appendText(System.getProperty("line.separator"));
    outFileTextArea.appendText(String.format("%1$-5s%2$-12s%3$s", "NR", "", "1=F()"));
    outFileTextArea.appendText(System.getProperty("line.separator"));

    for (int i = 0; i < meanMeasurementValue.getBothMean().size(); i++) {
      Double t = rtlFileWrap.getRtlTargetData().getTargets().get(i);
      String tPrefix = t >= 0 ? "+" : "";
      Double m = meanMeasurementValue.getBothMean().get(i);
      String mPrefix = m >= 0 ? "+" : "";
      outFileTextArea.appendText(String.format("%1$-5s%2$s%3$-12.4f%4$s%5$.4f", i, tPrefix, t, mPrefix, m / 1000));
      outFileTextArea.appendText(System.getProperty("line.separator"));
    }
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
