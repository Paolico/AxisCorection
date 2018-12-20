package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Controller  implements Initializable {
  private List<String> splitLines = new ArrayList<>();

  private XYChart.Series meanSeries;
  private XYChart.Series meanForwardSeries;
  private XYChart.Series meanBackSeries;

  private AtomicReference<RtlSections> section = new AtomicReference<>(RtlSections.NONE);

  private RtlHeader rtlHeader;
  private RtlTargetData rtlTargetData;
  private RtlUserText rtlUserText;
  private RtlRuns rtlRuns;
  private RtlDeviations rtlDeviations;
  private RtlEnvironment rtlEnvironment;

  List<Double> mean;
  List<Double> meanForward;
  List<Double> meanBack;

  //<editor-fold desc="FXML Properties">
  @FXML
  private MenuItem close;

  @FXML
  private MenuItem open;

  @FXML
  private MenuItem show;

  @FXML
  private MenuItem save;

  @FXML
  private TextArea inFileTextArea;

  @FXML
  private TextArea outFileTextArea;

  @FXML
  private NumberAxis xAxis;// = new NumberAxis(2008,2018,1);

  @FXML
  private NumberAxis yAxis;// = new NumberAxis(10,80,5);

  @FXML
  private LineChart<NumberAxis, NumberAxis> chart;// = new LineChart(xAxis,yAxis);
  //</editor-fold>

  //<editor-fold desc="FXML Actions">
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
    File selectedFile = fileChooser.showOpenDialog(window);
    if (selectedFile != null) {
      inFileTextArea.clear();
      outFileTextArea.clear();
      splitLines.clear();
      meanSeries.getData().clear();
      meanForwardSeries.getData().clear();
      meanForwardSeries.getData().clear();
      System.out.println("selectedFile = " + selectedFile.getName());
      System.out.println("close = " + close);

      String fileName = selectedFile.getAbsolutePath();
      System.out.println("Výpis souboru:");

      //read file into stream, try-with-resources
      try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

        stream.forEach(s -> {
          //System.out.println(s);
          inFileTextArea.appendText(s);
          inFileTextArea.appendText(System.getProperty("line.separator"));
          if (!s.isEmpty()) {
            splitLines.add(s);
          }
        });

      } catch (IOException e) {
          e.printStackTrace();
      }
    }
    System.out.println(splitLines);
    parse();
    calculate();
    createOutputFile();
    System.out.println();
  }

  @FXML
  void handleOnActionShow(ActionEvent event) {
      System.out.println("volani show");
  }

  @FXML
  void handleOnActionSave(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();

    //Set extension filter
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
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
  //</editor-fold>

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TEST GRAFU
    //Defining Axis
    //Defining Label for Axis
    xAxis.setLabel("osa x");
    yAxis.setLabel("osa y");

    //Creating the instance of linechart with the specified axis
    //chart = new LineChart<>(xAxis,yAxis);

    //creating the meanSeries
    meanSeries = new XYChart.Series();
    meanForwardSeries = new XYChart.Series();
    meanBackSeries = new XYChart.Series();

    //setting name and the date to the meanSeries
    meanSeries.setName("Korekce pozic");
    meanForwardSeries.setName("Korekce dopředu");
    meanBackSeries.setName("Korekce zpět");
    //adding meanSeries to the linechart
    //chart.getData().add(meanSeries);
    chart.getData().add(meanForwardSeries);
    chart.getData().add(meanBackSeries);

//    xAxis.setAutoRanging(false);
//    xAxis.setLowerBound(2008);
//    xAxis.setUpperBound(2018);
//    xAxis.setTickUnit(1);
//
//    yAxis.setAutoRanging(false);
//    yAxis.setLowerBound(0);
//    yAxis.setUpperBound(80);
//    yAxis.setTickUnit(10);

    rtlHeader = new RtlHeader();
    rtlTargetData = new RtlTargetData();
    rtlUserText = new RtlUserText();
    rtlRuns = new RtlRuns();
    rtlDeviations = new RtlDeviations();
    rtlEnvironment = new RtlEnvironment();

    inFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
    outFileTextArea.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
  }

  //<editor-fold desc="private support methods">
  private void parse() {

    splitLines.forEach(i -> {
      switch (section.get()) {
        case HEADER:
          if (tryChangeSection(i)) {
            break;
          }
          if (i.startsWith("File type")) {
            String[] split = i.split(":", 2);
            rtlHeader.setFileType(split[1].trim());
          } else if (i.startsWith("Owner")) {
            String[] split = i.split(":", 2);
            rtlHeader.setOwner(split[1].trim());
          } else if (i.startsWith("Version no")) {
            String[] split = i.split(":", 2);
            rtlHeader.setVersionNo(split[1].trim());
          }
          break;
        case TARGET_DATA:
          if (tryChangeSection(i)) {
            break;
          }
          if (i.startsWith("Filetype")) {
            String[] split = i.split(":", 2);
            rtlTargetData.setFileType(split[1].trim());
          } else if (i.startsWith("Target-count")) {
            String[] split = i.split(":", 2);
            rtlTargetData.setTargetCount(split[1].trim());
          } else if (i.startsWith("Flags")) {
            String[] split = i.split(":", 2);
            rtlTargetData.setFlags(split[1].trim());
          } else if (i.startsWith("Targets")) {
            break; // todo ověřit, že data nikdy nezačínají již na tomto řádku
          } else if (Character.isDigit(i.charAt(0))){ // Targets položky začínají číslicí
            String[] split = i.split("\\s+");
            Arrays.stream(split).forEach(s -> rtlTargetData.getTargets().add(Double.valueOf(s)));
          }
          break;
        case USER_TEXT:
          if (tryChangeSection(i)) {
            break;
          }
          if (i.startsWith("Machine")) {
            String[] split = i.split(":", 2);
            rtlUserText.setMachine(split[1].trim());
          } else if (i.startsWith("Serial No")) {
            String[] split = i.split(":", 2);
            rtlUserText.setSerialNo(split[1].trim());
          } else if (i.startsWith("Date")) {
            String[] split = i.split(":", 2);
            rtlUserText.setDate(split[1].trim());
          } else if (i.startsWith("By")) {
            String[] split = i.split(":", 2);
            rtlUserText.setBy(split[1].trim());
          } else if (i.startsWith("Axis")) {
            String[] split = i.split(":", 2);
            rtlUserText.setAxis(split[1].trim());
          } else if (i.startsWith("Location")) {
            String[] split = i.split(":", 2);
            rtlUserText.setLocation(split[1].trim());
          } else if (i.startsWith("TITLE")) {
            String[] split = i.split(":", 2);
            rtlUserText.setTitle(split[1].trim());
          }
          break;
        case RUNS:
          if (tryChangeSection(i)) {
            break;
          }
          if (i.startsWith("Run-count")) {
            String[] split = i.split(":", 2);
            rtlRuns.setRunCount(Integer.valueOf(split[1].trim()));
          }
          break;
        case DEVIATIONS:
          if (tryChangeSection(i)) {
            break;
          }
          if (i.startsWith("Run Target Data")) {
            break;
          } else if (Character.isDigit(i.charAt(0))){ // Targets položky začínají číslicí
            String[] split = i.split("\\s+", 3);
            rtlDeviations.getRun().add(Integer.valueOf(split[0]));
            rtlDeviations.getTarget().add(Integer.valueOf(split[1]));
            rtlDeviations.getData().add(Double.valueOf(split[2]));
          }
          break;
        case ENVIRONMENT:
          if (tryChangeSection(i)) {
            break;
          }
          break;
        case NONE:
          try {
            String value = i.replace("::", "");
            section.set(RtlSections.valueOf(value));
            System.out.println("*********************** SEKCE HEADER ***********************");
          } catch(IllegalArgumentException ex) {
            System.err.println("Soubor nezačíná sekcí HEADER!");
          }
          break;
      }
    });
  }

  private boolean tryChangeSection(String value) {
    value = value.replace("::", "");
    value = value.replace(" ", "_");
    value = value.replace("-", "_");
    try {
      this.section.set(RtlSections.valueOf(value));
      System.out.println("*********************** ZMĚNA SEKCE ***********************");
      return true;
    } catch(IllegalArgumentException ex) {
      System.out.println("Řádek sekce " + this.section.get());
      return false;
    }
  }

  private void calculate() {
    int positionCount = rtlDeviations.getRun().size() / rtlRuns.getRunCount();
    mean = new ArrayList<>(positionCount);
    meanForward = new ArrayList<>(positionCount);
    meanBack = new ArrayList<>(positionCount);
    for (int i = 0; i < positionCount; i++) {
      mean.add(i, 0.0);
      meanForward.add(i, 0.0);
      meanBack.add(i, 0.0);
    }
    for (int i = 1, ii = 0; i <= rtlRuns.getRunCount(); i++, ii++) {
      if (i % 2 != 0) { // tam
        for (int j = 0; j < positionCount; j++) {
          mean.set(j, mean.get(j) + rtlDeviations.getData().get(ii * positionCount + j));
          meanForward.set(j, meanForward.get(j) + rtlDeviations.getData().get(ii * positionCount + j));
        }
      } else { // zpět
        for (int j = positionCount - 1, jj = 0; j >= 0; j--, jj++) {
          mean.set(j, mean.get(j) + rtlDeviations.getData().get(ii * positionCount + j));
          meanBack.set(j, meanBack.get(j) + rtlDeviations.getData().get(ii * positionCount + jj));
        }
      }
    }
    for (int i = 0, j = 1; i < positionCount; i++, j++) {
      // spojeno tam i zpet - smazat
      mean.set(i, mean.get(i) / rtlRuns.getRunCount());
      meanSeries.getData().add(new XYChart.Data(rtlTargetData.getTargets().get(i),mean.get(i)));
      // tam
      meanForward.set(i, meanForward.get(i) / rtlRuns.getRunCount() / 2);
      meanForwardSeries.getData().add(new XYChart.Data(rtlTargetData.getTargets().get(i),meanForward.get(i)));
      // zpet
      meanBack.set(i, meanBack.get(i) / rtlRuns.getRunCount() / 2);
      meanBackSeries.getData().add(new XYChart.Data(rtlTargetData.getTargets().get(i), meanBack.get(i)));
    }
  }

  private void createOutputFile() {
    outFileTextArea.appendText("BEGIN AXIS-X.COM  DATUM:-875.0000 DISTANCE:50.0000");
    outFileTextArea.appendText(System.getProperty("line.separator"));
    outFileTextArea.appendText(String.format("%1$-5s%2$-12s%3$s", "NR", "", "1=F()"));
    outFileTextArea.appendText(System.getProperty("line.separator"));

    for (int i = 0; i < meanForward.size(); i++) {
      Double t = rtlTargetData.getTargets().get(i);
      String tPrefix = t >= 0 ? "+" : "";
      Double m = meanForward.get(i);
      String mPrefix = m >= 0 ? "+" : "";
      outFileTextArea.appendText(String.format("%1$-5s%2$s%3$-12.4f%4$s%5$.4f", i, tPrefix, t, mPrefix, m));
      outFileTextArea.appendText(System.getProperty("line.separator"));
    }
    int offset = meanBack.size();
    for (int i = 0; i < offset; i++) {
      Double t = rtlTargetData.getTargets().get(offset - i - 1);
      String tPrefix = t >= 0 ? "+" : "";
      Double m = meanBack.get(offset - i - 1);
      String mPrefix = m >= 0 ? "+" : "";
      outFileTextArea.appendText(String.format("%1$-5s%2$s%3$-12.4f%4$s%5$.4f", offset + i, tPrefix, t, mPrefix, m));
      outFileTextArea.appendText(System.getProperty("line.separator"));
    }
  }
  //</editor-fold>

}
