package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

    //<editor-fold desc="FXML Labels">
    @FXML
    private Label labelErrorMsgInvalidNumber;
    //</editor-fold>

    //<editor-fold desc="FXML CheckBoxs">
    @FXML
    private CheckBox checkBoxZeroShift;
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

        labelErrorMsgInvalidNumber.setVisible(false);

            if(!textFieldStartCompValue.getText().matches("[+-]?([0-9]{1,6}|[0-9]{1,6}[\\.][0-9]{0,4})")  ) {
                    textFieldEndCompValue.setText("");
                    textFieldStepCompValue.setText("");
                    labelErrorMsgInvalidNumber.setVisible(true);
                    return;
            }

            startCompValue =  Double.valueOf(textFieldStartCompValue.getText());
            endCompValue = startCompValue + Collections.max(rtlWrap.getRtlTargetData().getTargets());

            if (startCompValue > 100000 || startCompValue<-100000 || endCompValue > 100000){
                textFieldEndCompValue.setText("");
                textFieldStepCompValue.setText("");
                labelErrorMsgInvalidNumber.setVisible(true);
                return;
            }

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

    @FXML
    void handleOnActionCheckBoxZeroShift(ActionEvent event){

     if (checkBoxZeroShift.isSelected()) {
         meanValue.zeroShift(true);
         consumer.calculate(true);
     }
     else {
            meanValue.zeroShift(false);
            consumer.calculate(true);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        labelErrorMsgInvalidNumber.setVisible(false);

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
        int axisIndex = 0;
        int columnWidth = 0;
        double columnStart = 0;
        double columnPrefix = 0;

        int column12Width=0;

        StringBuilder sb = new StringBuilder();

       // controlSystem = Constants.SIN840D;

        if (controlSystem == Constants.iTNC530) {

            // TODO Zjistit mezeru mezi sloupci, jestli má být 10 nebo 11 znaků pro iTNC530

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
                //  sb.append(String.format("%1$-"+columnWidth+"s", axis.getLabel()));

                // zjisteni indexu osy, kterou chci kompenzovat
                if (axis.getName() == axisComp) {
                    axisIndex = Integer.valueOf(axis.getIndex()) - 1;
                }

            }

            String rowPrefix = "$AA_ENC_COMP";

            sb.append("%_N_AX"+axisIndex+"_EEC_INI");
            sb.append(System.getProperty("line.separator"));
            sb.append("CHANDATA(1)");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_STEP[0,AX"+axisIndex+"] = "+stepCompValue+"");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_MIN[0,AX"+axisIndex+"]  = "+startCompValue+"");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_MAX[0,AX"+axisIndex+"]  = "+endCompValue+"");
            sb.append(System.getProperty("line.separator"));
            sb.append(rowPrefix+"_IS_MODULO[0,AX"+axisIndex+"]  = 0");

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
                    String rowPosix = String.format("[0,%1$s,AX"+axisIndex+"] = ",i);

                    row = String.format("%1$s%2$s%3$1s%4$.4f" ,rowPrefix,rowPosix, mPrefix, m / 1000);
                    sb.append(row);
                }
                sb.append(System.getProperty("line.separator"));
            }

        if (controlSystem == Constants.SIN840D){
            sb.append("M17");
            sb.append(System.getProperty("line.separator"));
        }

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
        checkBoxZeroShift.selectedProperty().setValue(meanValue.isShiftedData());
    }
}
