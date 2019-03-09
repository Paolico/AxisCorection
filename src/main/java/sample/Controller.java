package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ResourceBundle;

public class Controller  implements Initializable {

  //<editor-fold desc="Properties">
  private XYChart.Series meanSeries;
  private XYChart.Series meanForwardSeries;
  private XYChart.Series meanBackSeries;
  private RtlParser rtlParser;
  private RtlFileWrap rtlFileWrap;
  private MeanMeasurementValue meanMeasurementValue;
  //</editor-fold>

  //<editor-fold desc="FXML properties">
  @FXML
  private Button nefakcenko;

  @FXML
  private MenuItem close;

  @FXML
  private MenuItem open;

  @FXML
  private MenuItem show;

  @FXML
  private MenuItem about;

  @FXML
  private MenuItem save;

  @FXML
  private TextArea inFileTextArea;

  @FXML
  private TextArea outFileTextArea;

  @FXML
  private NumberAxis xAxis;// = new NumberAxis(2008,2018,1);

  @FXML
  private LineChart<NumberAxis, NumberAxis> chart;// = new LineChart(xAxis,yAxis);

  @FXML
  private NumberAxis yAxis;// = new NumberAxis(10,80,5);
  //</editor-fold>

  //<editor-fold desc="FXML Actions">

  //<editor-fold desc="Menu">

  @FXML
  void handleOnActionClose(ActionEvent event) {
    Platform.exit();
    System.exit(0);
  }

  @FXML
  void handleOnActionOpen(ActionEvent event) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    Window window = open.getParentPopup().getScene().getWindow();
    fileChooser.setInitialDirectory(new File (System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop")); //pka
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
  void handleOnActionAbout(ActionEvent event) {
    System.out.println("Pavel Kadlecik.sro");
  }
  //</editor-fold>

  @FXML
  void onClick_nefakcenko(ActionEvent event) {
    System.out.println(String.format("Uz mam %s praci.", "dobrou"));
  }
  //</editor-fold>

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //Defining Label for Axis
    xAxis.setLabel("osa x");
    yAxis.setLabel("osa y");

    //creating the meanSeries
    meanSeries = new XYChart.Series();
    meanForwardSeries = new XYChart.Series();
    meanBackSeries = new XYChart.Series();

    //setting name and the date to the meanSeries
    meanSeries.setName("Korekce pozic");
    meanForwardSeries.setName("Korekce dopředu");
    meanBackSeries.setName("Korekce zpět");
    //adding meanSeries to the linechart
    chart.getData().add(meanSeries);
    //chart.getData().add(meanForwardSeries);
    //chart.getData().add(meanBackSeries);

//    xAxis.setAutoRanging(false);
//    xAxis.setLowerBound(2008);
//    xAxis.setUpperBound(2018);
//    xAxis.setTickUnit(1);
//
//    yAxis.setAutoRanging(false);
//    yAxis.setLowerBound(0);
//    yAxis.setUpperBound(80);
//    yAxis.setTickUnit(10);

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
    for (int i = 0, j = 1; i < positionCount; i++, j++) {
      meanSeries.getData().add(new XYChart.Data(/*todo by user input*/rtlFileWrap.getRtlTargetData().getTargets().get(i), bothMean.get(i)));
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
  //</editor-fold>

}
