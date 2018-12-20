package model;

public enum RtlSections {
  HEADER {
    @Override
    public String toString() {
      return "HEADER::";
    }
  }, TARGET_DATA {
    @Override
    public String toString() {
      return "TARGET DATA::";
    }
  }, USER_TEXT {
    @Override
    public String toString() {
      return "USER-TEXT::";
    }
  }, RUNS {
    @Override
    public String toString() {
      return "RUNS::";
    }
  }, DEVIATIONS {
    @Override
    public String toString() {
      return "DEVIATIONS::";
    }
  }, ENVIRONMENT {
    @Override
    public String toString() {
      return "ENVIRONMENT::";
    }
  }, NONE {
    @Override
    public String toString() {
      return super.toString();
    }
  };
}
