package model;


import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class RtlUserSettings {

  public static final String DEFAULT_FOLDER = System.getProperty("user.home") + File.separator + "AxisCorection";
  private static final String CONFIG_FILE = DEFAULT_FOLDER + File.separator + "config.json";

  // transient = neserializovat, serializace = prevedeni instantace objektu na posloupnost bitů , které lze ulozit na uloziste

  private transient Gson gson;
  private transient boolean created = false;
  private String externProgramPath = "";
  private String outputDataFolderPath = DEFAULT_FOLDER;
  private String inputDataFolderPath = DEFAULT_FOLDER;

  private static RtlUserSettings instance = null;

  // bezparametricky konstruktor pro potreba Gson
  private RtlUserSettings() {}

  // Parametricky konstruktor
  private RtlUserSettings(boolean init) throws IOException {
    gson = new Gson();
    File dir = new File(DEFAULT_FOLDER);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File file = new File(CONFIG_FILE);
    if (!file.exists()) {
      file.createNewFile();
      created = true;
    } else {
      try (FileReader fr = new FileReader(file)) {
        String content = FileUtils.readFileToString(file);
        RtlUserSettings fromJson = gson.fromJson(content, RtlUserSettings.class);
        externProgramPath = fromJson.getExternProgramPath();
        outputDataFolderPath = fromJson.getOutputDataFolderPath();
        inputDataFolderPath = fromJson.getInputDataFolderPath();
      }
    }
  }

  public static RtlUserSettings getInstance() {
    if (instance == null) {
      try {
        instance = new RtlUserSettings(true);
        if (instance.created) {
          instance.save();
          instance.created = false;
        }
        return instance;
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return instance;
    }
  }

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

  public void save() throws IOException {
    try (FileWriter fw = new FileWriter(CONFIG_FILE)) {
      fw.write(gson.toJson(instance));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
