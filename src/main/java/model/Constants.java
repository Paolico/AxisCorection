package model;

import java.io.File;

public class Constants {

    public static final String DEFAULT_FOLDER = System.getProperty("user.home") + File.separator + "AxisCorrection";
    public static final String CONFIG_FILE = DEFAULT_FOLDER + File.separator + "config.json";
    public static final String OUTPUT_FILE_SETTINGS =DEFAULT_FOLDER + File.separator + "fileSettings.json";

    public static final String iTNC530 = "HEIDENHAIN iTNC530";
    public static final String TNC640 = "HEIDENHAIN TNC640";
    public static final String SIN840D = "SINUMERIK 840D" ;

}
