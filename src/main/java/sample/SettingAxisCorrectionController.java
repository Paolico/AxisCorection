package sample;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import model.AxisDef;
import model.Constants;
import model.Database;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;


public class SettingAxisCorrectionController implements Initializable {


    public ObservableList<String> controlSystems;

    private Map<String, ArrayList<AxisDef>>  axisListConfigDatabase;


    //<editor-fold desc="FXML properties">

    //<editor-fold desc="ComboBox">
    @FXML
    private ComboBox comboBoxControlSystem;

    @FXML
    private ComboBox comboBoxAxisConfig;

    @FXML
    private ComboBox comboBoxCompensatedAxis;

    //</editor-fold>

    //</editor-fold>


    //<editor-fold desc="FXML Actions">
    @FXML
    void handleOnActionComboBoxControlSystem(ActionEvent event) {
        //comboBoxAxisConfig.disableProperty().bind(comboBoxControlSystem.getSelectionModel().selectedItemProperty().isNull());
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
    //</editor-fold>


    public void setSetting(Map<String, ArrayList<AxisDef>> axisListdatabase) {
        this.axisListConfigDatabase = axisListdatabase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        controlSystems = FXCollections.observableArrayList(new String[]{ (Constants.iTNC530), (Constants.TNC640),});
        // Řidicí systemy
        comboBoxControlSystem.setItems(controlSystems);
        //Konfigurace os
        comboBoxAxisConfig.setItems(FXCollections.observableList(FXCollections.observableArrayList(Database.getAxisListdatabase().keySet())));

        ObservableBooleanValue a ;
       // SimpleBooleanProperty enable = new SimpleBooleanProperty(true);

        comboBoxAxisConfig.disableProperty().bind(comboBoxControlSystem.getSelectionModel().selectedItemProperty().isNull());
        comboBoxCompensatedAxis.disableProperty().bind(comboBoxAxisConfig.getSelectionModel().selectedItemProperty().isNull());
           //     buttonDeleteTableRow.disableProperty().bind
        System.out.println("");

    }


}
