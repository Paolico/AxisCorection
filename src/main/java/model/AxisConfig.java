package model;

import javafx.beans.property.SimpleStringProperty;

public class AxisConfig {

    //<editor-fold desc="AxisConfig Properties">
    private /*final*/ transient SimpleStringProperty configName;
    private /*final*/ transient SimpleStringProperty controlName;


    private String cfgName;

    //</editor-fold>

    //<editor-fold desc="Getters ">


    public String getConfigName() {
        return configName.get();
    }

    public SimpleStringProperty configNameProperty() {
        return configName;
    }

    public String getControlName() {
        return controlName.get();
    }

    public SimpleStringProperty controlNameProperty() {
        return controlName;
    }

    public String getCfgName() {
        return cfgName;
    }

    //</editor-fold>

    //<editor-fold desc="Setters">

    public void setConfigName(String configName) {
        this.configName.set(configName);
    }

    public void setControlName(String controlName) {
        this.controlName.set(controlName);
    }

    public void setCfgName(String cfgName) {
        this.cfgName = cfgName;
    }


    //</editor-fold>

    public AxisConfig (String controlName, String configName) {
        this.configName = new SimpleStringProperty(configName);
        this.controlName = new SimpleStringProperty(controlName);

        cfgName = controlName+"."+configName;

    }

    public AxisConfig() {
        this.configName = new SimpleStringProperty();
        this.controlName = new SimpleStringProperty();
    }




}
