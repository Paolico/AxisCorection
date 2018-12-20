package model;

public class RtlEnvironment {

  //<editor-fold desc="Teplota (start, end, ?)">
  /**Air temp   : 20.337280 20.384956 0
   * 20.337280
   * 20.384956
   * 0
   * */
  private String[] airTemp;
  public String getAirTempStart() {
    return airTemp[0];
  }

  public String getAirTempEnd() {
    return airTemp[1];
  }

  public String getAirTempTodo() {
    return airTemp[2];
  }

  public String[] getAirTemp() {
    return airTemp;
  }

  public void setAirTemp(String[] airTemp) {
    this.airTemp = airTemp;
  }

  //</editor-fold>

  //<editor-fold desc="Tlak (start, end, ?)">
  /**Air press  : 99.039005 99.049346 1
   * 99.039005
   * 99.049346
   * 1
   * */
  private String[] airPress;
  public String getAirPressStart() {
    return airPress[0];
  }

  public String getAirPressEnd() {
    return airPress[1];
  }

  public String getAirPressTodo() {
    return airPress[2];
  }

  public String[] getAirPress() {
    return airPress;
  }

  public void setAirPress(String[] airPress) {
    this.airPress = airPress;
  }

  //</editor-fold>

  //<editor-fold desc="Vlhlost (start, end)">
  /**Air humid  : 46.954897 46.893887
   * 46.954897
   * 46.893887
   * */
  private String[] airHumid;
  public String getAirHumidStart() {
    return airHumid[0];
  }

  public String getAirHumidEnd() {
    return airHumid[1];
  }

  public String getAirHumidTodo() {
    return airHumid[2];
  }

  public String[] getAirHumid() {
    return airHumid;
  }

  public void setAirHumid(String[] airHumid) {
    this.airHumid = airHumid;
  }

  //</editor-fold>

  //<editor-fold desc="Teplota materiálu 1 (start, end, ?)">
  /**Mat temp 1 : 20.203292 20.321342 0
   * 20.203292
   * 20.321342
   * 0
   * */
  private String[] matTemp_1;
  public String getMatTemp_1Start() {
    return matTemp_1[0];
  }

  public String getMatTemp_1End() {
    return matTemp_1[1];
  }

  public String getMatTemp_1Todo() {
    return matTemp_1[2];
  }

  public String[] getMatTemp_1() {
    return matTemp_1;
  }

  public void setMatTemp_1(String[] matTemp_1) {
    this.matTemp_1 = matTemp_1;
  }

  //</editor-fold>

  //<editor-fold desc="Teplota materiálu 2 (start, end, ?)">
  /**Mat temp 2 : -10.000000 -10.000000 0
   * -10.000000
   * -10.000000
   * 0
   * */
  private String[] matTemp_2;
  public String getMatTemp_2Start() {
    return matTemp_2[0];
  }

  public String getMatTemp_2End() {
    return matTemp_2[1];
  }

  public String getMatTemp_2Todo() {
    return matTemp_2[2];
  }

  public String[] getMatTemp_2() {
    return matTemp_2;
  }

  public void setMatTemp_2(String[] matTemp_2) {
    this.matTemp_2 = matTemp_2;
  }

  //</editor-fold>

  //<editor-fold desc="Teplota materiálu 2 (start, end, ?)">
  /**Mat temp 3 : -10.000000 -10.000000 0
   * -10.000000
   * -10.000000
   * 0
   * */
  private String[] matTemp_3;
  public String getMatTemp_3Start() {
    return matTemp_3[0];
  }

  public String getMatTemp_3End() {
    return matTemp_3[1];
  }

  public String getMatTemp_3Todo() {
    return matTemp_3[2];
  }

  public String[] getMatTemp_3() {
    return matTemp_3;
  }

  public void setMatTemp_3(String[] matTemp_3) {
    this.matTemp_3 = matTemp_3;
  }

  //</editor-fold>

  //<editor-fold desc="Enviromentální faktor (?, ?)">
  /**Env factor : 0.31641096 0.31641067
   * 0.31641096
   * 0.31641067
   * */
  private String[] envFactor;
  public String getEnvFactorStart() {
    return envFactor[0];
  }

  public String getEnvFactorEnd() {
    return envFactor[1];
  }

  public String getEnvFactorTodo() {
    return envFactor[2];
  }

  public String[] getEnvFactor() {
    return envFactor;
  }

  public void setEnvFactor(String[] envFactor) {
    this.envFactor = envFactor;
  }

  //</editor-fold>

  //<editor-fold desc="Koeficient roztažnosti skla (start, end, ?)">
  /**Exp coeff  : 8.000000 8.000000 0
   * 8.000000
   * 8.000000
   * 0
   * */
  private String[] expCoeff;
  public String getExpCoeffStart() {
    return expCoeff[0];
  }

  public String getExpCoeffEnd() {
    return expCoeff[1];
  }

  public String getExpCoeffTodo() {
    return expCoeff[2];
  }

  public String[] getExpCoeff() {
    return expCoeff;
  }

  public void setExpCoeff(String[] expCoeff) {
    this.expCoeff = expCoeff;
  }

  //</editor-fold>

  //<editor-fold desc="Měření (start, end)">
  /**Date Time  : "10:46 Dec 12 2016" "11:01 Dec 12 2016"
   * "10:46 Dec 12 2016"
   * "11:01 Dec 12 2016"
   * */
  private String[] dateTime;
  public String getDateTimeStart() {
    return dateTime[0];
  }

  public String getDateTimeEnd() {
    return dateTime[1];
  }

  public String getDateTimeTodo() {
    return dateTime[2];
  }

  public String[] getDateTime() {
    return dateTime;
  }

  public void setDateTime(String[] dateTime) {
    this.dateTime = dateTime;
  }

  //</editor-fold>

  //<editor-fold desc="Final Data (?, ?)">
  /**Final Data : 1 0
   * 1
   * 0
   * */
  private String[] finalData;
  public String getFinalDataStart() {
    return finalData[0];
  }

  public String getFinalDataEnd() {
    return finalData[1];
  }

  public String getFinalDataTodo() {
    return finalData[2];
  }

  public String[] getFinalData() {
    return finalData;
  }

  public void setFinalData(String[] finalData) {
    this.finalData = finalData;
  }

  //</editor-fold>

  //<editor-fold desc="Constructors">
  public RtlEnvironment() {
    airTemp   = new String[3];
    airPress  = new String[3];
    airHumid  = new String[3];
    matTemp_1 = new String[3];
    matTemp_2 = new String[3];
    matTemp_3 = new String[3];
    envFactor = new String[3];
    expCoeff  = new String[3];
    dateTime  = new String[3];
    finalData = new String[3];
  }

  public RtlEnvironment(String[] airTemp, String[] airPress, String[] airHumid, String[] matTemp_1, String[] matTemp_2, String[] matTemp_3, String[] envFactor, String[] expCoeff, String[] dateTime, String[] finalData) {
    this.airTemp   = airTemp;
    this.airPress  = airPress;
    this.airHumid  = airHumid;
    this.matTemp_1 = matTemp_1;
    this.matTemp_2 = matTemp_2;
    this.matTemp_3 = matTemp_3;
    this.envFactor = envFactor;
    this.expCoeff  = expCoeff;
    this.dateTime  = dateTime;
    this.finalData = finalData;
  }
  //</editor-fold>
}
