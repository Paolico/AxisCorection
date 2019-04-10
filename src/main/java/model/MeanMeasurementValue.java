package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MeanMeasurementValue {

  private List<Double> both;
  private List<Double> forward;
  private List<Double> back;

  private List<Double> bothMean;
  private List<Double> forwardMean;
  private List<Double> backMean;

  private int runCount;
  private double startPosition;
  private double step;
  private Double shiftedValue;
  private boolean shiftedData;




  public MeanMeasurementValue(RtlFileWrap rtlFileWrap, double startPosition) {
    this.startPosition = startPosition;
    runCount = rtlFileWrap.getRtlRuns().getRunCount();
    this.step = Math.abs(rtlFileWrap.getRtlTargetData().getTargets().get(1) - rtlFileWrap.getRtlTargetData().getTargets().get(0));
    both = new ArrayList<>(rtlFileWrap.getRtlTargetData().getTargetCount());
    forward = new ArrayList<>(rtlFileWrap.getRtlTargetData().getTargetCount());
    back = new ArrayList<>(rtlFileWrap.getRtlTargetData().getTargetCount());
    for (int i = 0; i < rtlFileWrap.getRtlTargetData().getTargetCount(); i++) {
      both.add(i, 0.0);
      forward.add(i, 0.0);
      back.add(i, 0.0);
    }
  }

  public List<Double> getBoth() {
    return both;
  }

  public List<Double> getForward() {
    return forward;
  }

  public List<Double> getBack() {
    return back;
  }

  public void add(int position, double value, boolean forward) {
    both.set(position, both.get(position) + value);
    if (forward) {
      this.forward.set(position, this.forward.get(position) + value);

    } else {
      back.set(position, back.get(position) + value);
    }
  }

  public List<Double> getBothMean() {
    if (bothMean == null) {
      bothMean = both.stream().map(value -> value / runCount).collect(Collectors.toList());
      return bothMean;
    }
    return bothMean;
  }

  public List<Double> getForwardMean() {
    if (forwardMean == null) {
      forwardMean = forward.stream().map(value -> value / (runCount / 2)).collect(Collectors.toList());
      return forwardMean;
    }
    return forwardMean;
  }

  public List<Double> getBackMean() {
    if (backMean == null) {
      backMean = back.stream().map(value -> value / (runCount / 2)).collect(Collectors.toList());
      return backMean;
    }
    return backMean;
  }

  public boolean isShiftedData() {
    return shiftedData;
  }

  public void zeroShift (boolean shift) {

    if (shift && (!shiftedData ) ) {

      shiftedValue =bothMean.stream().findFirst().get();
      bothMean = bothMean.stream().map(value -> value - shiftedValue).collect(Collectors.toList());
      shiftedData = true;

    }
    // unshiftData
    else{

      if (shiftedData ){
        bothMean = bothMean.stream().map(value -> value + shiftedValue).collect(Collectors.toList());
        shiftedValue = Double.valueOf(0);
        shiftedData = false;
      }
    }
  }
}
