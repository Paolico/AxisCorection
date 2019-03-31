package model;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OutputFileSettings {

  public HashMap<String,ArrayList<AxisDef>> database;


  public OutputFileSettings(Map<String, ObservableList<AxisDef>> configDatabase) {
    database = new HashMap<>();
    for (Map.Entry<String, ObservableList<AxisDef>> entry : configDatabase.entrySet()) {
      database.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    System.out.println("database = " + database);
  }


  public HashMap<String, ArrayList<AxisDef>> getDatabase() {
    return database;
  }
}
