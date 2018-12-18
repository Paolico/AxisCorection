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
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller  implements Initializable {
  private List<List<String>> splitLines = new ArrayList<>();

  @FXML
  private MenuItem close;

  @FXML
  private MenuItem open;

  @FXML
  private MenuItem show;

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
    splitLines.forEach(i -> i.forEach(ii -> {
      String numberOnly= ii.replaceAll("[^0-9.0-9]", "");
      outFileTextArea.appendText(numberOnly);
      outFileTextArea.appendText(System.getProperty("line.separator"));
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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    System.out.println("byl jsem tu");
    // TEST GRAFU
    //Defining Axis


    //Defining Label for Axis
    xAxis.setLabel("Year");
    yAxis.setLabel("Price");

    //Creating the instance of linechart with the specified axis
    //chart = new LineChart<>(xAxis,yAxis);

    //creating the series
    XYChart.Series series = new XYChart.Series();

    //setting name and the date to the series
    series.setName("Stock Analysis");
    series.getData().add(new XYChart.Data(2009,25));
    series.getData().add(new XYChart.Data(2010,15));
    series.getData().add(new XYChart.Data(2011,68));
    series.getData().add(new XYChart.Data(2012,60));
    series.getData().add(new XYChart.Data(2013,35));
    series.getData().add(new XYChart.Data(2014,55));
    series.getData().add(new XYChart.Data(2015,45));
    series.getData().add(new XYChart.Data(2016,67));
    series.getData().add(new XYChart.Data(2017,78));

    //adding series to the linechart
    chart.getData().add(series);

    xAxis.setAutoRanging(false);
    xAxis.setLowerBound(2008);
    xAxis.setUpperBound(2018);
    xAxis.setTickUnit(1);

    yAxis.setAutoRanging(false);
    yAxis.setLowerBound(0);
    yAxis.setUpperBound(80);
    yAxis.setTickUnit(10);
  }
}
