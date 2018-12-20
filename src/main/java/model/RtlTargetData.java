package model;

import java.util.ArrayList;
import java.util.List;

public class RtlTargetData {
  private String fileType;
  private int TargetCount;
  private List<Double> targets;
  private String flags;

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public int getTargetCount() {
    return TargetCount;
  }

  public void setTargetCount(int targetCount) {
    TargetCount = targetCount;
  }

  public List<Double> getTargets() {
    if (targets != null) {
      return targets;
    }
    return targets = new ArrayList<>();
  }

  public void setTargets(List<Double> targets) {
    this.targets = targets;
  }

  public String getFlags() {
    return flags;
  }

  public void setFlags(String flags) {
    this.flags = flags;
  }
}
