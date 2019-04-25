package model;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class AxisDef implements Serializable{

    //<editor-fold desc="AxisDef Properties">
    private /*final*/ transient SimpleStringProperty axisIndex;
    private /*final*/ transient SimpleStringProperty axisName;
    private /*final*/ transient SimpleStringProperty axisLabel;


    private String index;
    private String name;
    private String label;

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


    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    //</editor-fold>

    public AxisDef(String axisIndex, String axisName, String axisLabel) {
  //
        this.axisIndex = new SimpleStringProperty(axisIndex);
        this.axisName = new SimpleStringProperty(axisName);
        this.axisLabel= new SimpleStringProperty(axisLabel);

        index = axisIndex;
        name = axisName;
        label = axisLabel;

    }

    public AxisDef() {
        this.axisIndex = new SimpleStringProperty();
        this.axisName = new SimpleStringProperty();
        this.axisLabel= new SimpleStringProperty();
    }


}


