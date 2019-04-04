package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.*;

import java.net.URL;
import java.util.*;


public class SettingAxisCorrectionController implements Initializable {

    // test
    private RtlFileWrap rtlWrap;
    private MeanMeasurementValue meanValue;
    private SimpleStringProperty content = new SimpleStringProperty();
    private Controller consumer;

    private String controlSystem;
    private String axisDefs;
    private String axisComp;
    private ArrayList<AxisDef> axisList;

    private double startCompValue;
    private double endCompValue;
    private double stepCompValue;


    public ObservableList<String> controlSystems;

    private Map<String, ArrayList<AxisDef>>  axisListConfigDatabase = Database.getAxisListdatabase();
    TextArea outFileTextArea;


    //<editor-fold desc="FXML properties">

    //<editor-fold desc="ComboBoxs">
    @FXML
    private ComboBox comboBoxControlSystem;

    @FXML
    private ComboBox comboBoxAxisConfig;

    @FXML
    private ComboBox comboBoxCompensatedAxis;

    //</editor-fold>

    //<editor-fold desc="Buttons">
    @FXML
    private Button buttonCreateOutputFile;
    //</editor-fold>

    //<editor-fold desc="FXML TextFields">
    @FXML
    private TextField textFieldStartCompValue;

    @FXML
    private TextField textFieldEndCompValue;

    @FXML
    private TextField textFieldStepCompValue;

    //</editor-fold>

    //</editor-fold>


    //<editor-fold desc="FXML Actions">
    @FXML
    void handleOnActionComboBoxControlSystem(ActionEvent event) {

    }

    @FXML
    void handleOnActionComboBoxAxisConfig(ActionEvent event) {

        ArrayList<AxisDef> axisDefs = Database.getAxisListdatabase().get( comboBoxAxisConfig.getSelectionModel().getSelectedItem() );
        ObservableList<String> axisDef = FXCollections.observableArrayList();

        for (AxisDef items : axisDefs) {
            axisDef.add(items.getName());
        }

        comboBoxCompensatedAxis.setItems(axisDef);



    }

    @FXML
    void handleOnActionComboBoxCompensatedAxis(ActionEvent event) {

    }

    @FXML
    void handleOnActionCreateOutputFile(ActionEvent event) {

        controlSystem = comboBoxControlSystem.getSelectionModel().getSelectedItem().toString();
        axisDefs = comboBoxAxisConfig.getSelectionModel().getSelectedItem().toString();
        axisComp =  comboBoxCompensatedAxis.getSelectionModel().getSelectedItem().toString();
        createOutputFile();

    }

    @FXML
    void handleOnActionTextFieldStartCompValue(ActionEvent event) {

        startCompValue =  Double.valueOf(textFieldStartCompValue.getText());
        endCompValue = startCompValue + Collections.max(rtlWrap.getRtlTargetData().getTargets());
        textFieldEndCompValue.setText(String.valueOf(endCompValue));
        double targetCount =  rtlWrap.getRtlTargetData().getTargetCount() ;
        stepCompValue = Math.abs(((endCompValue - startCompValue )/ ( targetCount - 1)));
        textFieldStepCompValue.setText(String.valueOf(stepCompValue));



    }



    //</editor-fold>


    public void setSetting(Map<String, ArrayList<AxisDef>> axisListdatabase) {
        this.axisListConfigDatabase = axisListdatabase;
    }

    public void setTextArea(TextArea outFileTextArea) {
        this.outFileTextArea = outFileTextArea;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        controlSystems = FXCollections.observableArrayList(new String[]{ (Constants.iTNC530), (Constants.TNC640),});

        // Řidicí systemy
        comboBoxControlSystem.setItems(controlSystems);
        //Konfigurace os
        comboBoxAxisConfig.setItems(FXCollections.observableList(FXCollections.observableArrayList(Database.getAxisListdatabase().keySet())));

        comboBoxAxisConfig.disableProperty().bind(comboBoxControlSystem.getSelectionModel().selectedItemProperty().isNull());
        comboBoxCompensatedAxis.disableProperty().bind(comboBoxAxisConfig.getSelectionModel().selectedItemProperty().isNull());
        buttonCreateOutputFile.disableProperty().bind(comboBoxCompensatedAxis.getSelectionModel().selectedItemProperty().isNull()
                .or (textFieldStartCompValue.textProperty ().isEmpty ()) .or (textFieldEndCompValue.textProperty ().isEmpty ()) . or (textFieldStepCompValue.textProperty ().isEmpty ()));




    }

    public void createOutputFile() {
        // todo variable position

        axisList = axisListConfigDatabase.get(axisDefs);
        int axisIndex = 0 ;

        StringBuilder sb = new StringBuilder();
       // outFileTextArea.appendText("BEGIN AXIS-X.COM  DATUM:-875.0000 DISTANCE:50.0000");

        if (controlSystem == Constants.iTNC530) {
            sb.append("BEGIN AXIS-"+ axisComp +".COM  DATUM:"+startCompValue+" DISTANCE:"+stepCompValue);
        }
        else if (controlSystem == Constants.TNC640){
            sb.append("BEGIN AXIS_"+ axisComp +".COM MM");
        }
//        outFileTextArea.appendText(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
//        outFileTextArea.appendText(String.format("%1$-5s%2$-12s%3$s", "NR", "", "1=F()"));

        if (controlSystem == Constants.iTNC530) {

            // TODO Zjistit mezeru mezi sloupci, jestli má být 10 nebo 11 znaků pro iTNC530

            sb.append(String.format("%1$-8s%2$-12s", "NR", ""));

            for (AxisDef axis:axisList) {
                sb.append(String.format("%1$-10s",axis.getLabel()));

                // zjisteni indexu osy, kterou chci kompenzovat
                if (axis.getName() ==  axisComp) {
                   axisIndex = Integer.valueOf(axis.getIndex()) - 1;
                }

            }

        }
        else if (controlSystem == Constants.TNC640){
            sb.append(String.format("%1$-8s%2$-12s%3$s-12s%3$s", "NR", "","AXISPOS", axisComp));
        }
//        outFileTextArea.appendText(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        for (int i = 0; i < meanValue.getBothMean().size(); i++) {
            Double t =  rtlWrap.getRtlTargetData().getTargets().get(i);
            String tPrefix = t >= 0 ? "+" : "";
            Double m = meanValue.getBothMean().get(i);
            String mPrefix = m >= 0 ? "+" : "";
//            outFileTextArea.appendText(String.format("%1$-5s%2$s%3$-12.4f%4$s%5$.4f", i, tPrefix, t, mPrefix, m / 1000));
            double columnWidth =  10;
            double columnPrefix =  11.4 + columnWidth*axisIndex;

            String row = String.format("%1$-6s%2$3s%3$-"+columnPrefix+"f%4$s%5$.4f", i, tPrefix, t, mPrefix, m / 1000);
            sb.append(row.replace(",", ".")); // String.valueOf(t).replace(",", ".")
//            outFileTextArea.appendText(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));
        }
//    content.set(sb.toString());
    consumer.setOutContent(sb.toString());

    }

    public void setArgs(RtlFileWrap rtlFileWrap, MeanMeasurementValue meanMeasurementValue, SimpleStringProperty outContent) {
        rtlWrap = rtlFileWrap;
        meanValue = meanMeasurementValue;
        content.bind(outContent);
    }

    public void setArgs2(RtlFileWrap rtlFileWrap, MeanMeasurementValue meanMeasurementValue, Controller ctr) {
        rtlWrap = rtlFileWrap;
        meanValue = meanMeasurementValue;
        consumer = ctr;
    }
}
