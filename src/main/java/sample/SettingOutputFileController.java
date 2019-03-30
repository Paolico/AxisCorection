package sample;


import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AxisDef;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class SettingOutputFileController implements Initializable {

    //<editor-fold desc="FXML properties">

    //<editor-fold desc="FXML Table">
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
    private TextField textFieldAxisLabel;

    @FXML
    private TextField textFieldAxisName;

    //</editor-fold>

    //<editor-fold desc="FXML ListViews">

    @FXML
    private ListView<String> listViewAxisListConfig;

    //</editor-fold>

    //<editor-fold desc="FXML ToolBars">
    @FXML
    private ToolBar ToolBarTemp;
    //</editor-fold>

    private ObservableList<AxisDef> axisLists;

    private ObservableList<String> axisListConfigView;

    private  Map <String,ObservableList<AxisDef>> axisListConfigDatabase;

    //</editor-fold>

    //<editor-fold desc="FXML Actions">

    @FXML
    void handleOnActionButtonAddConfigAxisList(ActionEvent event) {

        String configName =textFieldConfigName.getText();

        axisLists.clear();
        ObservableList<AxisDef> newSetting = FXCollections.observableArrayList(axisLists);
        axisListConfigDatabase.put(configName, newSetting);

        tableOutputFileSetting.setItems(axisListConfigDatabase.get(configName));
        listViewAxisListConfig.getItems().add(configName);

        listViewAxisListConfig.getSelectionModel().selectLast();
        buttonDeleteConfigAxisList.disableProperty().bind(Bindings.size(axisListConfigDatabase.get(configName)).isEqualTo(0));
        textFieldConfigName.setText("");

    }

    @FXML
    void handleOnActionButtonDeleteConfigAxisList(ActionEvent event) {

        String configName =listViewAxisListConfig.getSelectionModel().getSelectedItem();
        axisLists.clear();

        tableOutputFileSetting.getItems().clear();
        axisListConfigDatabase.remove(configName);

       listViewAxisListConfig.getItems().remove(configName);


    }

    @FXML
    void handleOnActionButtonAddTableRow(ActionEvent event) {

        String axisName = textFieldAxisName.getText();
        String axisLabel = textFieldAxisLabel.getText();
        String selectConfig =listViewAxisListConfig.getSelectionModel().getSelectedItem();

        int axisListsCount = axisListConfigDatabase.get(selectConfig).size() +1 ;

        if (tableOutputFileSetting.getItems() != null) {
            buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1));
        }
        if ( !isNullOrEmpty(axisName) &&  !isNullOrEmpty(axisLabel)) {
            axisListConfigDatabase.get(listViewAxisListConfig.getSelectionModel().getSelectedItem()).add(new AxisDef(String.valueOf(axisListsCount), axisName, axisLabel));
        }

        tableOutputFileSetting.getSelectionModel().selectLast();
        textFieldAxisName.setText("");
        textFieldAxisLabel.setText("");

    }

    @FXML
    void handleOnActionButtonDeleteTableRow(ActionEvent event) {
        AxisDef selectItem= null;
        String selectConfig =listViewAxisListConfig.getSelectionModel().getSelectedItem();
        ObservableList<AxisDef> axisListsTemp = axisListConfigDatabase.get(selectConfig);

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

        axisLists = FXCollections.observableArrayList();
//        axisListConfigView = FXCollections.observableArrayList();
//        axisListConfigDatabase = new HashMap<>();

        columnAxisIndex.setCellValueFactory(( new PropertyValueFactory<AxisDef,String> ("axisIndex")));
        columnAxisName.setCellValueFactory(( new PropertyValueFactory<AxisDef,String> ("axisName")));
        columnAxisLabel.setCellValueFactory(( new PropertyValueFactory<AxisDef,String> ("axisLabel")));
        tableOutputFileSetting.setItems(axisLists);
//        tableOutputFileSetting.getSortOrder().add(columnAxisIndex);

//        listViewAxisListConfig.getItems().addAll(axisListConfigDatabase.keySet());
//
//        listViewAxisListConfig.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("Vymenit model tabulky pro: " + newValue);
//            tableOutputFileSetting.setItems(axisListConfigDatabase.get(newValue));
//            if (tableOutputFileSetting.getItems() != null) {
//                buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1));
//            }
//        });
//
//        // blokovani tlacitek
//        buttonDeleteConfigAxisList.setDisable(true);
//        buttonAddConfigAxisList.disableProperty().bind(textFieldConfigName.textProperty().isEmpty());
//        buttonAddTableRow.disableProperty().bind((textFieldAxisLabel.textProperty().isEmpty()) .or (textFieldAxisName.textProperty().isEmpty()) .or ( listViewAxisListConfig.getSelectionModel().selectedItemProperty().isNull()));
//        buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1) );

    }

    public Map<String, ObservableList<AxisDef>> getAxisListConfigDatabase() {
        return axisListConfigDatabase;
    }

    public void setAxisListConfigDatabase(Map<String, ArrayList<AxisDef>> configDatabase) {

//        this.axisListConfigDatabase = axisListConfigDatabase;
//        this.axisListConfigView = FXCollections.observableList(new ArrayList<>(axisListConfigDatabase.keySet()));

        axisListConfigDatabase = new HashMap<>();

        for (Map.Entry<String, ArrayList<AxisDef>> items : configDatabase.entrySet()) {
            System.out.println();
            for (AxisDef def: items.getValue()) {
                def.setAxisIndex(def.getIndex());
                def.setAxisName(def.getName());
                def.setAxisLabel(def.getLabel());
            }
            axisListConfigDatabase.put(items.getKey(), FXCollections.observableList(items.getValue()));
        }

        listViewAxisListConfig.getItems().addAll(axisListConfigDatabase.keySet());

        listViewAxisListConfig.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Vymenit model tabulky pro: " + newValue);
            tableOutputFileSetting.setItems(axisListConfigDatabase.get(newValue));
            if (tableOutputFileSetting.getItems() != null) {
                buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1));
            }
        });

        listViewAxisListConfig.getSelectionModel().selectFirst();

        // blokovani tlacitek
        buttonDeleteConfigAxisList.setDisable(true);
        buttonAddConfigAxisList.disableProperty().bind(textFieldConfigName.textProperty().isEmpty());
        buttonAddTableRow.disableProperty().bind((textFieldAxisLabel.textProperty().isEmpty()) .or (textFieldAxisName.textProperty().isEmpty()) .or ( listViewAxisListConfig.getSelectionModel().selectedItemProperty().isNull()));
        buttonDeleteTableRow.disableProperty().bind(Bindings.size(tableOutputFileSetting.getItems()).lessThan(1) );
    }

    //    public ObservableList<AxisDef> getAxisList (){
//                    axisLists = FXCollections.observableArrayList(
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X"),
//                new AxisDef("1","X","X")
//
//        );
//        return axisLists;
//        }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }


}
