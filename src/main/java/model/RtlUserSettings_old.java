package model;


import com.google.gson.Gson;

import java.io.*;

public class RtlUserSettings_old {

    public static final String CONFIG_FILE = "confing.cfg";

    private String externProgramPath;
    private String outputDataFolderPath;
    private String inputDataFolderPath;

    //<editor-fold desc="Getters">
    public String getExternProgramPath() {
        return this.externProgramPath;
    }

    public String getOutputDataFolderPath() {
        return this.outputDataFolderPath;
    }

    public String getInputDataFolderPath() {
        return this.inputDataFolderPath;
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setExternProgramPath(String value) {
        this.externProgramPath = value;
    }

    public void setInputDataFolderPath(String value) {
        this.inputDataFolderPath = value;
    }

    public void setOutputDataFolderPath(String value) {
        this.outputDataFolderPath = value;
    }
    //</editor-fold>

    public RtlUserSettings_old() {
        setExternProgramPath("Cesta k externimu programu");
        setInputDataFolderPath("Adresar vstupnich dat");
        setOutputDataFolderPath("Adresar vystupnich dat");
    }

    public static void initConfiguration (){
        RtlUserSettings_old userSettings = new RtlUserSettings_old();
        Gson gson = new Gson();
        try (FileWriter fileWriter = new FileWriter(CONFIG_FILE)) {
            gson.toJson(userSettings, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RtlUserSettings_old loadUserSettings (){
        RtlUserSettings_old userSettings = new RtlUserSettings_old();
        Gson gson = new Gson();
        try {
            userSettings = gson.fromJson(new FileReader(CONFIG_FILE), RtlUserSettings_old.class);
        } catch (FileNotFoundException e) {
            initConfiguration(); // pokud nebude soubor config.cfg existovat, tak se vytvori novy soubor s defaultními parametry
            e.printStackTrace();
        }
        return userSettings;
    }
}
