package model;

import java.util.ArrayList;
import java.util.List;

public class RtlDeviations {
  private List<Integer> run;
  private List<Integer> target;
  private List<Double> data;

  public List<Integer> getRun() {
    if (run != null) {
      return run;
    }
    return run = new ArrayList<>();
  }

  public void setRun(List<Integer> run) {
    this.run = run;
  }

  public List<Integer> getTarget() {
    if (target != null) {
      return target;
    }
    return target = new ArrayList<>();
  }

  public void setTarget(List<Integer> target) {
    this.target = target;
  }

  public List<Double> getData() {
    if (data != null) {
      return data;
    }
    return data = new ArrayList<>();
  }

  public void setData(List<Double> data) {
    this.data = data;
  }
}
