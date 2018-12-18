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
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller  implements Initializable {
  private List<List<String>> splitLines = new ArrayList<>();
  private XYChart.Series series;

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
      series.getData().clear();
      System.out.println("selectedFile = " + selectedFile.getName());
      System.out.println("close = " + close);

      String fileName = selectedFile.getAbsolutePath();
      System.out.println("Výpis souboru:");

      //read file into stream, try-with-resources
      try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

        stream.forEach(s -> {
          System.out.println(s);
          inFileTextArea.appendText(s);
          inFileTextArea.appendText(System.getProperty("line.separator"));
          splitLines.add(split(s));
        });

      } catch (IOException e) {
          e.printStackTrace();
      }
    }
    System.out.println(splitLines);
    AtomicInteger itr = new AtomicInteger(0);
    splitLines.forEach(i -> i.forEach(ii -> {
      String numberOnly= ii.replaceAll("[^0-9.0-9]", "");
      itr.set(itr.get() + 1);
      if (numberOnly != null && !numberOnly.isEmpty()) {
        outFileTextArea.appendText(numberOnly);
        outFileTextArea.appendText(System.getProperty("line.separator"));
        series.getData().add(new XYChart.Data(itr.get(),Double.valueOf(numberOnly)));
      }
    }));
  }

  private List<String> split(String str){
    return Stream.of(str.split(","))
      .map (elem -> new String(elem))
      .collect(Collectors.toList());
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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TEST GRAFU
    //Defining Axis
    //Defining Label for Axis
    xAxis.setLabel("osa x");
    yAxis.setLabel("osa y");

    //Creating the instance of linechart with the specified axis
    //chart = new LineChart<>(xAxis,yAxis);

    //creating the series
    series = new XYChart.Series();

    //setting name and the date to the series
    series.setName("Parsovaná čísla ze souboru");
    //adding series to the linechart
    chart.getData().add(series);

//    xAxis.setAutoRanging(false);
//    xAxis.setLowerBound(2008);
//    xAxis.setUpperBound(2018);
//    xAxis.setTickUnit(1);
//
//    yAxis.setAutoRanging(false);
//    yAxis.setLowerBound(0);
//    yAxis.setUpperBound(80);
//    yAxis.setTickUnit(10);
  }
}
