package model;


import com.google.gson.Gson;

import java.io.*;

public class RtlUserSettings {

    public static final String CONFIG_FILE = "confing.cfg";

    private String ExternProgramPath;

    public String getExternProgramPath()
    {

        return this.ExternProgramPath;
    }
    public void setExternProgramPath(String value)
    {

        this.ExternProgramPath = value;
    }

    private String InputDataFolderPath;

    public String getInputDataFolderPath()
    {

        return this.InputDataFolderPath;
    }
    public void setInputDataFolderPath(String value)
    {

        this.InputDataFolderPath = value;
    }

    private String OutputDataFolderPath;

    public String getOutputDataFolderPath()
    {

        return this.OutputDataFolderPath;
    }
    public void setOutputDataFolderPath(String value)
    {

        this.OutputDataFolderPath = value;
    }


    public RtlUserSettings () {
        setExternProgramPath("Cesta k externimu programu");
        setInputDataFolderPath("Adresar vstupnich dat");
        setOutputDataFolderPath("Adresar vystupnich dat");

}

    public static void initConfiguration (){
        Writer fileWriter = null;

        RtlUserSettings userSettings = new RtlUserSettings();
        Gson gson = new Gson();
        try {
            fileWriter = new FileWriter(CONFIG_FILE );
            gson.toJson(userSettings,fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static RtlUserSettings loadUserSettings (){

        RtlUserSettings userSettings = new RtlUserSettings ();
        Gson gson = new Gson();
        try {
            userSettings = gson.fromJson(new FileReader(CONFIG_FILE),RtlUserSettings.class);
        } catch (FileNotFoundException e) {
            initConfiguration(); // pokud nebude soubor config.cfg existovat, tak se vytvori novy soubor s defaultními parametry
            e.printStackTrace();
        }
        return userSettings;
    }




}
