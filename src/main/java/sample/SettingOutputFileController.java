package sample;



import com.sun.xml.internal.ws.util.StringUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AxisList;
import sun.awt.SunHints;

import java.net.URL;
import java.util.ResourceBundle;


public class SettingOutputFileController implements Initializable {

    //<editor-fold desc="FXML properties">

    //<editor-fold desc="FXML Table">
    @FXML
    private TableColumn<AxisList,String> columnAxisIndex;

    @FXML
    private TableColumn<AxisList, String> columnAxisName;

    @FXML
    private TableColumn<AxisList, String> columnAxisLabel;

    @FXML
    private TableView<AxisList> tableOutputFileSetting;
    //</editor-fold>

    //<editor-fold desc="FXML buttons">

    @FXML
    private Button buttonSaveConfigAxisList;

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

    private ObservableList<AxisList> axisLists;

    //</editor-fold>

    //<editor-fold desc="FXML Actions">

    @FXML
    void handleOnActionButtonAddTableRow(ActionEvent event) {

        String axisName = textFieldAxisName.getText();
        String axisLabel = textFieldAxisLabel.getText();
        int axisListsCount = axisLists.size() +1 ;


      if ( !isNullOrEmpty(axisName) &&  !isNullOrEmpty(axisLabel)) {
          axisLists.add(new AxisList(String.valueOf(axisListsCount), axisName, axisLabel));
      }

    }

    @FXML
    void handleOnActionButtonDeleteTableRow(ActionEvent event) {
        AxisList selectItem= null;

    if (!axisLists.isEmpty() ) {
         selectItem = tableOutputFileSetting.getSelectionModel().getSelectedItem();
         axisLists.remove(selectItem);

    }
    //    System.out.println( selectItem.getAxisIndex().toString());

    }

    //</editor-fold>


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        columnAxisIndex.setCellValueFactory(( new PropertyValueFactory<AxisList,String> ("axisIndex")));
        columnAxisName.setCellValueFactory(( new PropertyValueFactory<AxisList,String> ("axisName")));
        columnAxisLabel.setCellValueFactory(( new PropertyValueFactory<AxisList,String> ("axisLabel")));
        tableOutputFileSetting.setItems(getAxisList());

    }


    public ObservableList<AxisList> getAxisList (){
                    axisLists = FXCollections.observableArrayList(
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X"),
                new AxisList("1","X","X")

        );
        return axisLists;
        }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }


}
