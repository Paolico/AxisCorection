package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.*;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;


public class SettingAxisCorrectionController implements Initializable {

    private static String tmpControlSystem;
    private static String tmpAxisConfig;
    private static String tmpAxisComp;

    private RtlFileWrap rtlWrap;
    private MeanMeasurementValue meanValue;
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

    //<editor-fold desc="FXML CheckBoxs">
    @FXML
    private CheckBox checkBoxZeroShift;

    @FXML
    private CheckBox chbEncoderType;
    //</editor-fold>

    //</editor-fold>


    //<editor-fold desc="FXML Actions">
    @FXML
    void handleOnActionComboBoxControlSystem(ActionEvent event) {

        comboBoxCompensatedAxis.getSelectionModel().clearSelection();
        comboBoxCompensatedAxis.getItems().removeAll();
        comboBoxAxisConfig.getItems().removeAll();
        tmpAxisConfig = null;
        tmpAxisComp = null;
    }

    @FXML
    void handleOnActionComboBoxAxisConfig(ActionEvent event) {


        Object a = comboBoxControlSystem.getSelectionModel().getSelectedItem();
        Object b = comboBoxAxisConfig.getSelectionModel().getSelectedItem();

        if (a != null && b!= null) {

        String cfg =  a.toString()+"."+b.toString();

        ArrayList<AxisDef> axisDefs = Database.getAxisListdatabase().get(cfg);

        ObservableList<String> axisDef = FXCollections.observableArrayList();

        for (AxisDef items : axisDefs) {
            axisDef.add(items.getName());
        }

        comboBoxCompensatedAxis.setItems(axisDef);

       }

    }

    @FXML
    void handleOnActionComboBoxCompensatedAxis(ActionEvent event) {

    }

    @FXML
    void handleOnActionCreateOutputFile(ActionEvent event) {

        controlSystem = comboBoxControlSystem.getSelectionModel().getSelectedItem().toString();
        axisDefs = comboBoxControlSystem.getSelectionModel().getSelectedItem().toString()+"."+comboBoxAxisConfig.getSelectionModel().getSelectedItem().toString();
        axisComp =  comboBoxCompensatedAxis.getSelectionModel().getSelectedItem().toString();
        createOutputFile();
        consumer.setIsSiemensFile(controlSystem.equals(Constants.SIN840D) ? true : false);
        consumer.miSave.setDisable(false);
    }

    @FXML
    void handleOnActionTextFieldStartCompValue(ActionEvent event) {


            if(!textFieldStartCompValue.getText().matches("[+-]?([0-9]{1,6}|[0-9]{1,6}[\\.][0-9]{0,4})")  ) {
                    textFieldEndCompValue.setText("");
                    textFieldStepCompValue.setText("");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Chyba");
                    alert.setHeaderText("Chybné zadání.");
                    alert.setContentText("Zadejte hodnotu v rozahu ±100 000.0000 !");
                    alert.showAndWait();

                    return;
            }

            startCompValue =  Double.valueOf(textFieldStartCompValue.getText());
            endCompValue = startCompValue + Collections.max(rtlWrap.getRtlTargetData().getTargets());

            if (startCompValue > 100000 || startCompValue<-100000 || endCompValue > 100000){
                textFieldEndCompValue.setText("");
                textFieldStepCompValue.setText("");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Chyba");
                alert.setHeaderText("Chybné zadání.");
                alert.setContentText("Zadejte hodnotu v rozahu ±100 000.0000 !");
                alert.showAndWait();


                return;
            }

        textFieldEndCompValue.setText(String.valueOf(endCompValue));
        double targetCount =  rtlWrap.getRtlTargetData().getTargetCount() ;
        stepCompValue = Math.abs(((endCompValue - startCompValue )/ ( targetCount - 1)));
        textFieldStepCompValue.setText(String.valueOf(stepCompValue));

    }

    @FXML
    void handleOnChangeEncoderType(ActionEvent event) {

    }

    //</editor-fold>


    public void setSetting(Map<String, ArrayList<AxisDef>> axisListdatabase) {
        this.axisListConfigDatabase = axisListdatabase;
    }

    public void setTextArea(TextArea outFileTextArea) {
        this.outFileTextArea = outFileTextArea;
    }

    @FXML
    void handleOnActionCheckBoxZeroShift(ActionEvent event){

     if (checkBoxZeroShift.isSelected()) {
         meanValue.zeroShift(true);
         consumer.fillData(true);
     }
     else {
            meanValue.zeroShift(false);
            consumer.fillData(true);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        chbEncoderType.setDisable(true);

        controlSystems = FXCollections.observableArrayList(new String[]{ (Constants.iTNC530), (Constants.TNC640),(Constants.SIN840D) });

        comboBoxControlSystem.setItems(controlSystems);
        if (tmpControlSystem != null) {

            comboBoxControlSystem.getSelectionModel().select(tmpControlSystem);

            if (tmpControlSystem.equals(Constants.SIN840D)) {
                chbEncoderType.setDisable(false);
            } else {
                chbEncoderType.setDisable(true);
            }

            ArrayList<String> axisDef = new ArrayList<>();

            for (Map.Entry<String, ArrayList<AxisDef>> items : Database.getAxisListdatabase().entrySet() ) {

                String [] cs = items.getKey().split(Pattern.quote("."), 2);
                if (tmpControlSystem.contains(cs [0])) {
                    axisDef.add(cs [1]);

                }
            }

            comboBoxAxisConfig.setItems(FXCollections.observableList(FXCollections.observableArrayList(axisDef)));
            comboBoxAxisConfig.getSelectionModel().select(tmpAxisConfig);


            Object a = comboBoxControlSystem.getSelectionModel().getSelectedItem();
            Object b = comboBoxAxisConfig.getSelectionModel().getSelectedItem();

            if (a != null && b!= null) {

                String cfg =  a.toString()+"."+b.toString();

                ArrayList<AxisDef> axisDefs = Database.getAxisListdatabase().get(cfg);

                ObservableList<String> defs = FXCollections.observableArrayList();

                for (AxisDef items : axisDefs) {
                    defs.add(items.getName());
                }

                comboBoxCompensatedAxis.setItems(defs);

            }
        }

        comboBoxControlSystem.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            tmpControlSystem = (String)newValue;
            if (newValue != null && tmpControlSystem.equals(Constants.SIN840D)) {
                chbEncoderType.setDisable(false);
            } else {
                chbEncoderType.setDisable(true);
            }


            ArrayList<String> axisDef = new ArrayList<>();

            for (Map.Entry<String, ArrayList<AxisDef>> items : Database.getAxisListdatabase().entrySet() ) {

                String [] cs = items.getKey().split(Pattern.quote("."), 2);
                if (tmpControlSystem.contains(cs [0])) {
                    axisDef.add(cs [1]);
                }
            }

            comboBoxAxisConfig.setItems(FXCollections.observableList(FXCollections.observableArrayList(axisDef)));
            comboBoxAxisConfig.getSelectionModel().select(tmpAxisConfig);


        });

        comboBoxAxisConfig.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            tmpAxisConfig = (String)newValue;
        });


        comboBoxAxisConfig.disableProperty().bind(comboBoxControlSystem.getSelectionModel().selectedItemProperty().isNull());

        comboBoxCompensatedAxis.disableProperty().bind(comboBoxAxisConfig.getSelectionModel().selectedItemProperty().isNull());
        comboBoxCompensatedAxis.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            tmpAxisComp = (String)newValue;
        });
        comboBoxCompensatedAxis.getSelectionModel().select(tmpAxisComp);

        buttonCreateOutputFile.disableProperty().bind(comboBoxCompensatedAxis.getSelectionModel().selectedItemProperty().isNull() .or (comboBoxAxisConfig.getSelectionModel().selectedItemProperty().isNull())
                .or (comboBoxControlSystem.getSelectionModel().selectedItemProperty().isNull()).or (textFieldStartCompValue.textProperty ().isEmpty ()) .or (textFieldEndCompValue.textProperty ().isEmpty ()) . or (textFieldStepCompValue.textProperty ().isEmpty ()));

    }

    public void createOutputFile() {
        axisList = axisListConfigDatabase.get(axisDefs);
        int axisIndex = 0;
        int columnWidth = 0;
        double columnStart = 0;
        double columnPrefix = 0;

        int column12Width=0;

        StringBuilder sb = new StringBuilder();

        String encoderType = chbEncoderType.isSelected() ? "0" : "1";

        if (controlSystem == Constants.iTNC530) {
            String prefixStartCompValue = startCompValue >= 0 ? "+" : "";
            String prefixStepCompValue = stepCompValue >= 0 ? "+" : "";
            String row = String.format("BEGIN AXIS-%1$s.COM  DATUM:%2$s%3$.4f DISTANCE:%4$s%5$.4f", axisComp,prefixStartCompValue, startCompValue,prefixStepCompValue, stepCompValue);
            sb.append(row.replace(",", "."));

            sb.append(System.getProperty("line.separator"));

            column12Width= 8;  // mezera sloupcem NR a POZICEMI;
            columnStart = 11.4;// žačátek prvního sloupce s daty XYZ
            columnWidth = 11; //  mezera mezi sloupcemi XYZ


            sb.append(String.format("%1$-"+column12Width+"s%2$-"+(columnWidth+1)+"s", "NR", ""));

            for (AxisDef axis : axisList) {
                sb.append(String.format("%1$-"+columnWidth+"s", axis.getLabel()));

                // zjisteni indexu osy, kterou chci kompenzovat
                if (axis.getName() == axisComp) {
                    axisIndex = Integer.valueOf(axis.getIndex()) - 1;
                }

            }
            // pozice sloupce v závisloti na kompezované ose
            columnPrefix = columnStart + columnWidth * axisIndex;

        } else if (controlSystem == Constants.TNC640) {

            sb.append("BEGIN AXIS_" + axisComp + ".COM MM");
            sb.append(System.getProperty("line.separator"));

            column12Width = 5; // mezera sloupcem NR a AXISPOS
            columnStart = 27.4;// žačátek prvního sloupce s daty XYZ
            columnWidth = 14;  // mezera sloupcem AXISPOS,BACKLASH a XYZ

            sb.append(String.format("%1$-"+column12Width+"s%2$-"+columnWidth+"s%3$-"+columnWidth+"s", "NR", "AXISPOS", "BACKLASH"));
            for (AxisDef axis : axisList) {
                sb.append(String.format("%1$-"+columnWidth+"s", axis.getLabel()));

                // zjisteni indexu osy, kterou chci kompenzovat
                if (axis.getName() == axisComp) {
                    axisIndex = Integer.valueOf(axis.getIndex()) - 1;
                }

            }
            // pozice sloupce v závisloti na kompezované ose
             columnPrefix = columnStart + columnWidth * axisIndex; // pozice sloupce v závisloti na komp. ose

        }
        else if (controlSystem == Constants.SIN840D){

            // Hlavička

            for (AxisDef axis : axisList) {
                // zjisteni indexu osy, kterou chci kompenzovat
                if (axis.getName() == axisComp) {
                    axisIndex = Integer.valueOf(axis.getIndex());
                }

            }

            String rowPrefix = "$AA_ENC_COMP";

            sb.append("CHANDATA(1)");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_STEP[" + encoderType + ",AX"+axisIndex+"]="+stepCompValue+"");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_MIN[" + encoderType + ",AX"+axisIndex+"]="+startCompValue+"");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_MAX[" + encoderType + ",AX"+axisIndex+"]="+endCompValue+"");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_IS_MODULO[" + encoderType + ",AX"+axisIndex+"]=0");

        }

            sb.append(System.getProperty("line.separator"));
            String row;
            for (int i = 0; i < meanValue.getBothMean().size(); i++) {
                Double t = startCompValue + rtlWrap.getRtlTargetData().getTargets().get(i);
                String tPrefix = t >= 0 ? "+" : "-";
                t = Math.abs(t);
                Double m = meanValue.getBothMean().get(i);
                String mPrefix = m >= 0 ? "+" : "-";
                m = Math.abs(m);


                if (controlSystem == Constants.iTNC530 || controlSystem == Constants.TNC640 ) {
                    row = String.format("%1$-" + column12Width + "s%2$s%3$-" + columnPrefix + "f%4$s%5$.4f", i, tPrefix, t, mPrefix, m / 1000);
                    sb.append(row.replace(",", "."));
                }
                else if (controlSystem == Constants.SIN840D){

                    String rowPrefix = "$AA_ENC_COMP";
                    String rowPosix = String.format("[" + encoderType + ",%1$s,AX"+axisIndex+"]=",i);
                    String correction = String.format("%1$.4f", (m / 1000));
                    row = String.format("%1$s%2$s%3$1s%4$s" ,rowPrefix,rowPosix, mPrefix, correction.replace(",", "."));
                    sb.append(row);
                }
                sb.append(System.getProperty("line.separator"));
            }

        if(controlSystem == Constants.TNC640 ){
            sb.append("[END]");
        }

        if (controlSystem == Constants.SIN840D){
            sb.append("M17");
            sb.append(System.getProperty("line.separator"));
        }
            consumer.setOutContent(sb.toString());
    }


    public void setArgs2(RtlFileWrap rtlFileWrap, MeanMeasurementValue meanMeasurementValue, Controller ctr) {
        rtlWrap = rtlFileWrap;
        meanValue = meanMeasurementValue;
        consumer = ctr;
        checkBoxZeroShift.selectedProperty().setValue(meanValue.isShiftedData());
    }
}
