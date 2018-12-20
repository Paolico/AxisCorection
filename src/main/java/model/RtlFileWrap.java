package model;

public class RtlFileWrap {

  private  RtlHeader rtlHeader;
  private  RtlTargetData rtlTargetData;
  private  RtlUserText rtlUserText;
  private  RtlRuns rtlRuns;
  private  RtlDeviations rtlDeviations;
  private  RtlEnvironment rtlEnvironment;

  public RtlFileWrap(RtlHeader rtlHeader, RtlTargetData rtlTargetData, RtlUserText rtlUserText, RtlRuns rtlRuns, RtlDeviations rtlDeviations, RtlEnvironment rtlEnvironment) {
    this.rtlHeader = rtlHeader;
    this.rtlTargetData = rtlTargetData;
    this.rtlUserText = rtlUserText;
    this.rtlRuns = rtlRuns;
    this.rtlDeviations = rtlDeviations;
    this.rtlEnvironment = rtlEnvironment;
  }

  public RtlHeader getRtlHeader() {
    return rtlHeader;
  }

  public RtlTargetData getRtlTargetData() {
    return rtlTargetData;
  }

  public RtlUserText getRtlUserText() {
    return rtlUserText;
  }

  public RtlRuns getRtlRuns() {
    return rtlRuns;
  }

  public RtlDeviations getRtlDeviations() {
    return rtlDeviations;
  }

  public RtlEnvironment getRtlEnvironment() {
    return rtlEnvironment;
  }
}
