package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

    public static   Map<String, ArrayList<AxisDef>> axisListdatabase;
    public static MeanMeasurementValue  meanMeasurementValue;
    public static RtlFileWrap  rtlFileWrap;

    public MeanMeasurementValue getMeanMeasurementValue() {
        return meanMeasurementValue;
    }

    public void setMeanMeasurementValue(MeanMeasurementValue meanMeasurementValue) {
        this.meanMeasurementValue = meanMeasurementValue;
    }

    public RtlFileWrap getRtlFileWrap() {
        return rtlFileWrap;
    }

    public void setRtlFileWrap(RtlFileWrap rtlFileWrap) {
        this.rtlFileWrap = rtlFileWrap;
    }


    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    private UserSettings userSettings;

    public void setAxisListdatabase(Map<String, ArrayList<AxisDef>> axisListdatabase) {
        this.axisListdatabase = axisListdatabase;
    }

    public static Map<String, ArrayList<AxisDef>> getAxisListdatabase() {
        return axisListdatabase;
    }

     public  Gson gson;

    public Database() {
       gson = new Gson();
        this. userSettings = UserSettings.getInstance();
        this.axisListdatabase = loadOutputFileSettings();

        if (this.axisListdatabase == null) {
            axisListdatabase = FXCollections.emptyObservableMap();
        }
    }

    public  Map<String, ArrayList<AxisDef>> loadOutputFileSettings () {
        File dir = new File(Constants.DEFAULT_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(Constants.OUTPUT_FILE_SETTINGS);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader fr = new FileReader(file)) {
                String content = FileUtils.readFileToString(file);
        //  return gson.fromJson(content, HashMap.class);
                return gson.fromJson(content, new TypeToken<Map<String, ArrayList<AxisDef>>>(){}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
