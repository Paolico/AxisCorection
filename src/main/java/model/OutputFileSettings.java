package model;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OutputFileSettings {
//  public HashMap<String,String[][]> database;
  public HashMap<String,ArrayList<AxisDef>> database2;

//  public OutputFileSettings(Map<String, ObservableList<AxisDef>> configDatabase) {
//    database = new HashMap<>();
//    int it = 0;
//    for (Map.Entry<String, ObservableList<AxisDef>> entry : configDatabase.entrySet()) {
////      String[][] items = new String[it][3];
//      String[][] items = new String[100][];
//      for (AxisDef def : entry.getValue()) {
//        String[] val = {def.getAxisIndex(), def.getAxisName(), def.getAxisLabel()};
//        items[it] = val;
//      }
//      it++;
//      database.put(entry.getKey(), items);
//    }
//    System.out.println("database = " + database);
//  }

  public OutputFileSettings(Map<String, ObservableList<AxisDef>> configDatabase) {
    database2 = new HashMap<>();
    for (Map.Entry<String, ObservableList<AxisDef>> entry : configDatabase.entrySet()) {
      database2.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    System.out.println("database = " + database2);
  }

//  public Map<String, String[][]> getDatabase() {
//    return database;
//  }

  public HashMap<String, ArrayList<AxisDef>> getDatabase2() {
    return database2;
  }
}
