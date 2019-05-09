package sample;


import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AxisConfig;
import model.AxisDef;
import model.Constants;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


public class SettingOutputFileController implements Initializable {

    public ObservableList<String> controlSystems;

    //<editor-fold desc="FXML properties">

    //<editor-fold desc="FXML ComboBox">
    @FXML
    private ComboBox cbControlSystem;
    //</editor-fold>

    //<editor-fold desc="FXML Table">

    @FXML
    private TableView<AxisConfig> tableAxisConfig;

    @FXML
    private TableColumn<AxisConfig,String> columnControlSystem;

    @FXML
    private TableColumn<AxisConfig,String> columnConfigName;


    @FXML
    private TableColumn<AxisDef,String> columnAxisIndex;

    @FXML
    private TableColumn<AxisDef, String> columnAxisName;

    @FXML
    private TableColumn<AxisDef, String> columnAxisLabel;

    @FXML
    private TableView<AxisDef> tableOutputFileSetting;
    //</editor-fold>

    //<editor-fold desc="FXML buttons">

    @FXML
    private Button buttonAddConfigAxisList;

    @FXML
    private Button buttonDeleteConfigAxisList;

    @FXML
    private Button buttonAddTableRow;

    @FXML
    private Button buttonDeleteTableRow;

    //</editor-fold>

    //<editor-fold desc="FXML TextFields">

    @FXML
    private TextField textFieldConfigName;

    @FXML
    private TextField textFieldAxisName;

    //</editor-fold>

    //<editor-fold desc="FXML ToolBars">
    @FXML
    private ToolBar ToolBarTemp;
    //</editor-fold>

    private ObservableList<AxisDef> axisLists;

    private ObservableList<AxisConfig> axisConfig;

    private ObservableList<String> axisListConfigView;

    private  Map <String,ObservableList<AxisDef>> axisListConfigDatabase;

    //</editor-fold>

    //<editor-fold desc="FXML Actions">

    @FXML
    void handleOnActionCbControlSystem(ActionEvent event) {

    }

    @FXML
    void handleOnActionButtonAddConfigAxisList(ActionEvent event) {


        String configName = cbControlSystem.getValue().toString() +"."+ textFieldConfigName.getText();
        axisLists.clear();
        ObservableList<AxisDef> newSetting = FXCollections.observableArrayList(axisLists);
        axisListConfigDatabase.put(configName, newSetting);

        tableOutputFileSetting.setItems(axisListConfigDatabase.get(configName));
        tableAxisConfig.getItems().add (new AxisConfig (cbControlSystem.getValue().toString(),textFieldConfigName.getText()));
        textFieldConfigName.setText("");

    }

    @FXML
    void handleOnActionButtonDeleteConfigAxisList(ActionEvent event) {
        axisLists.clear();

        tableOutputFileSetting.getItems().clear();
        AxisConfig selectItem = tableAxisConfig.getSelectionModel().getSelectedItem();
        String configName = selectItem.getCfgName();
        axisListConfigDatabase.remove(configName);

        tableAxisConfig.getItems().remove(selectItem);

      // listViewAxisListConfig.getItems().remove(configName);






    }

    @FXML
    void handleOnActionButtonAddTableRow(ActionEvent event) {

        String axisName = textFieldAxisName.getText();
        String axisLabel = "" ;
        AxisConfig selectConfig = tableAxisConfig.getSelectionModel().getSelectedItem();
        String config =selectConfig.getCfgName();
        String sw = selectConfig.getControlName();
        String axisListsCount = String.valueOf( (axisListConfigDatabase.get(config).size() +1) );

        if (tableOutputFileSetting.getItems() != null) {
            buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1));
        }
        if ( !isNullOrEmpty(axisName)) {

            switch (sw) {
                case Constants.iTNC530:
                    axisLabel = axisListsCount+"=F()";
                    break;
                case Constants.TNC640:
                    axisLabel = axisName;
                    break;
                case Constants.SIN840D:
                    axisLabel = axisListsCount;
            }
           axisListConfigDatabase.get(config).add (new AxisDef(axisListsCount, axisName, axisLabel));
        }

        tableOutputFileSetting.getSelectionModel().selectLast();
        textFieldAxisName.setText("");


    }

    @FXML
    void handleOnActionButtonDeleteTableRow(ActionEvent event) {

        AxisDef selectItem= null;
        AxisConfig axisConfig= tableAxisConfig.getSelectionModel().getSelectedItem();
        ObservableList<AxisDef> axisListsTemp = axisListConfigDatabase.get(axisConfig.getCfgName());

        if (!axisListsTemp.isEmpty() ) {
            selectItem = tableOutputFileSetting.getSelectionModel().getSelectedItem();
            axisListsTemp.remove(selectItem);

        // aktualizace čísla osy v tabulce po smazaní řádku
            for (int i = 0; i < axisListsTemp.size(); i++) {

                axisListsTemp.get(i).setAxisIndex(String.valueOf(i+1));
            }
        }
    }
    //</editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        controlSystems = FXCollections.observableArrayList((Constants.iTNC530), (Constants.TNC640), (Constants.SIN840D));
        cbControlSystem.setItems(controlSystems);
        cbControlSystem.getSelectionModel().selectFirst();

        axisLists = FXCollections.observableArrayList();
        axisConfig = FXCollections.observableArrayList();

        columnAxisIndex.setCellValueFactory(( new PropertyValueFactory<AxisDef,String> ("axisIndex")));
        columnAxisName.setCellValueFactory(( new PropertyValueFactory<AxisDef,String> ("axisName")));
        columnAxisLabel.setCellValueFactory(( new PropertyValueFactory<AxisDef,String> ("axisLabel")));
        tableOutputFileSetting.setItems(axisLists);

        columnConfigName.setCellValueFactory(( new PropertyValueFactory<AxisConfig,String> ("configName")));
        columnControlSystem.setCellValueFactory(( new PropertyValueFactory<AxisConfig,String> ("controlName")));
        tableAxisConfig.setItems(axisConfig);



    }

    public Map<String, ObservableList<AxisDef>> getAxisListConfigDatabase() {
        return axisListConfigDatabase;
    }

    public void setAxisListConfigDatabase(Map<String, ArrayList<AxisDef>> configDatabase) {

        axisListConfigDatabase = new HashMap<>();


        for (Map.Entry<String, ArrayList<AxisDef>> items : configDatabase.entrySet()) {
            System.out.println();
            for (AxisDef def: items.getValue()) {
                def.setAxisIndex(def.getIndex());
                def.setAxisName(def.getName());
                def.setAxisLabel(def.getLabel());
            }
            String itemsKey=items.getKey();
            axisListConfigDatabase.put(itemsKey, FXCollections.observableList(items.getValue()));
            String[] split = itemsKey.split(Pattern.quote("."), 2);
            AxisConfig axisConfig = new AxisConfig(split [0],split [1]);
            tableAxisConfig.getItems().addAll( axisConfig );
        }

    tableAxisConfig.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        System.out.println("Vymenit model tabulky pro: " + newValue);
        if (newValue != null) {
            String config = newValue.getCfgName();
            tableOutputFileSetting.setItems(axisListConfigDatabase.get(config));
            if (tableOutputFileSetting.getItems() != null) {
                buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1));
            }
        }
    });


        // blokovani tlacitek
        //buttonDeleteConfigAxisList.setDisable(true);
        buttonDeleteConfigAxisList.disableProperty().bind(( tableAxisConfig.getSelectionModel().selectedItemProperty().isNull()));
        buttonAddConfigAxisList.disableProperty().bind(textFieldConfigName.textProperty().isEmpty());
        buttonAddTableRow.disableProperty().bind(((textFieldAxisName.textProperty().isEmpty()) .or ( tableAxisConfig.getSelectionModel().selectedItemProperty().isNull())));
        buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1) );
    }


    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }


}
