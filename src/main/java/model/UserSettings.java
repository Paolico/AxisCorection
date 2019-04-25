package model;


import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class UserSettings {


  // transient = neserializovat, serializace = prevedeni instantace objektu na posloupnost bitů , které lze ulozit na uloziste

  private transient Gson gson;
  private transient boolean created = false;
  private String externProgramPath = "";
  private String outputDataFolderPath = Constants.DEFAULT_FOLDER;
  private String inputDataFolderPath = Constants.DEFAULT_FOLDER;

  private static UserSettings instance = null;

  // bezparametricky konstruktor pro potreba Gson
  private UserSettings() {}

  // Parametricky konstruktor
  private UserSettings(boolean init) throws IOException {
    gson = new Gson();
    File dir = new File(Constants.DEFAULT_FOLDER);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File file = new File(Constants.CONFIG_FILE);
    if (!file.exists()) {
      file.createNewFile();
      created = true;
    } else {
      try (FileReader fr = new FileReader(file)) {
        String content = FileUtils.readFileToString(file);
        UserSettings fromJson = gson.fromJson(content, UserSettings.class);
        externProgramPath = fromJson.getExternProgramPath();
        outputDataFolderPath = fromJson.getOutputDataFolderPath();
        inputDataFolderPath = fromJson.getInputDataFolderPath();
      }
    }
  }

  public static UserSettings getInstance() {
    if (instance == null) {
      try {
        instance = new UserSettings(true);
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
    try (FileWriter fw = new FileWriter(Constants.CONFIG_FILE)) {
      fw.write(gson.toJson(instance));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
