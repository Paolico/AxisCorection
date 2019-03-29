package model;

import javafx.beans.property.SimpleStringProperty;

public class AxisDef {


    //<editor-fold desc="AxisDef Properties">
    private final SimpleStringProperty axisIndex;
    private final SimpleStringProperty axisName;
    private final SimpleStringProperty axisLabel;
    //</editor-fold>

    //<editor-fold desc="AxisDef Getters Setters">
    public String getAxisIndex() {
        return axisIndex.get();
    }

    public SimpleStringProperty axisIndexProperty() {
        return axisIndex;
    }

    public void setAxisIndex(String axisIndex) {
        this.axisIndex.set(axisIndex);
    }

    public String getAxisName() {
        return axisName.get();
    }

    public SimpleStringProperty axisNameProperty() {
        return axisName;
    }

    public void setAxisName(String axisName) {
        this.axisName.set(axisName);
    }

    public String getAxisLabel() {
        return axisLabel.get();
    }

    public SimpleStringProperty axisLabelProperty() {
        return axisLabel;
    }

    public void setAxisLabel(String axisLabel) {
        this.axisLabel.set(axisLabel);
    }
    //</editor-fold>

    public AxisDef(String axisIndex, String axisName, String axisLabel) {
  //
        this.axisIndex = new SimpleStringProperty(axisIndex);
        this.axisName = new SimpleStringProperty(axisName);
        this.axisLabel= new SimpleStringProperty(axisLabel);

    }

}


