package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class RtlParser {

  RtlFileWrap rtlFileWrap;

  private RtlHeader rtlHeader;
  private RtlTargetData rtlTargetData;
  private RtlUserText rtlUserText;
  private RtlRuns rtlRuns;
  private RtlDeviations rtlDeviations;
  private RtlEnvironment rtlEnvironment;

  private Path path;

  private List<String> splitLines = new ArrayList<>();
  private StringBuilder textBuilder;

  private AtomicReference<RtlSections> section = new AtomicReference<>(RtlSections.NONE);

  public RtlParser(Path path) {
    this.path = path;
    rtlHeader = new RtlHeader();
    rtlTargetData = new RtlTargetData();
    rtlUserText = new RtlUserText();
    rtlRuns = new RtlRuns();
    rtlDeviations = new RtlDeviations();
    rtlEnvironment = new RtlEnvironment();
  }

  public RtlFileWrap parse() {
    textBuilder = new StringBuilder();
    try (Stream<String> stream = Files.lines(path)) {
      stream.forEach(s -> {
        textBuilder.append(s);
        textBuilder.append(System.getProperty("line.separator"));
        if (!s.isEmpty()) {
          splitLines.add(s);
        }
      });

    } catch (IOException e) {
      e.printStackTrace();
    }

    splitLines.forEach(line -> {
      switch (section.get()) {
        case HEADER:
          if (tryChangeSection(line)) {
            break;
          }
          if (line.startsWith("File type")) {
            String[] split = line.split(":", 2);
            rtlHeader.setFileType(split[1].trim());
          } else if (line.startsWith("Owner")) {
            String[] split = line.split(":", 2);
            rtlHeader.setOwner(split[1].trim());
          } else if (line.startsWith("Version no")) {
            String[] split = line.split(":", 2);
            rtlHeader.setVersionNo(split[1].trim());
          }
          break;
        case TARGET_DATA:
          if (tryChangeSection(line)) {
            break;
          }
          if (line.startsWith("Filetype")) {
            String[] split = line.split(":", 2);
            rtlTargetData.setFileType(split[1].trim());
          } else if (line.startsWith("Target-count")) {
            String[] split = line.split(":", 2);
            rtlTargetData.setTargetCount(Integer.valueOf(split[1].trim()));
          } else if (line.startsWith("Flags")) {
            String[] split = line.split(":", 2);
            rtlTargetData.setFlags(split[1].trim());
          } else if (line.startsWith("Targets")) {
            break;
          } else if (Character.isDigit(line.charAt(0))){ // Targets položky začínají číslicí
            String[] split = line.split("\\s+");
            Arrays.stream(split).forEach(s -> rtlTargetData.getTargets().add(Double.valueOf(s)));
          }
          break;
        case USER_TEXT:
          if (tryChangeSection(line)) {
            break;
          }
          if (line.startsWith("Machine")) {
            String[] split = line.split(":", 2);
            rtlUserText.setMachine(split[1].trim());
          } else if (line.startsWith("Serial No")) {
            String[] split = line.split(":", 2);
            rtlUserText.setSerialNo(split[1].trim());
          } else if (line.startsWith("Date")) {
            String[] split = line.split(":", 2);
            rtlUserText.setDate(split[1].trim());
          } else if (line.startsWith("By")) {
            String[] split = line.split(":", 2);
            rtlUserText.setBy(split[1].trim());
          } else if (line.startsWith("Axis")) {
            String[] split = line.split(":", 2);
            rtlUserText.setAxis(split[1].trim());
          } else if (line.startsWith("Location")) {
            String[] split = line.split(":", 2);
            rtlUserText.setLocation(split[1].trim());
          } else if (line.startsWith("TITLE")) {
            String[] split = line.split(":", 2);
            rtlUserText.setTitle(split[1].trim());
          }
          break;
        case RUNS:
          if (tryChangeSection(line)) {
            break;
          }
          if (line.startsWith("Run-count")) {
            String[] split = line.split(":", 2);
            rtlRuns.setRunCount(Integer.valueOf(split[1].trim()));
          }
          break;
        case DEVIATIONS:
          if (tryChangeSection(line)) {
            break;
          }
          if (line.startsWith("Run Target Data")) {
            break;
          } else if (Character.isDigit(line.charAt(0))){ // Targets položky začínají číslicí
            String[] split = line.split("\\s+", 3);
            rtlDeviations.getRun().add(Integer.valueOf(split[0]));
            rtlDeviations.getTarget().add(Integer.valueOf(split[1]));
            rtlDeviations.getData().add(Double.valueOf(split[2]));
          }
          break;
        case ENVIRONMENT:
          if (tryChangeSection(line)) {
            break;
          }
          if (line.startsWith("Air temp")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setAirTemp(values);
          } else if (line.startsWith("Air press")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setAirPress(values);
          } else if (line.startsWith("Air humid")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setAirHumid(values);
          } else if (line.startsWith("Mat temp 1")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setMatTemp_1(values);
          } else if (line.startsWith("Mat temp 2")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setMatTemp_2(values);
          } else if (line.startsWith("Mat temp 3")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setMatTemp_3(values);
          } else if (line.startsWith("Env factor")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setEnvFactor(values);
          } else if (line.startsWith("Exp coeff")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setExpCoeff(values);
          } else if (line.startsWith("Date Time")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\"", 5);
            rtlEnvironment.setDateTime(new String[]{values[1], values[3], values[4]});
          } else if (line.startsWith("Final Data")) {
            String[] split = line.split(":", 2);
            String[] values = split[1].trim().split("\\s+", 3);
            rtlEnvironment.setFinalData(values);
          }
          break;
        case NONE:
          try {
            String value = line.replace("::", "");
            section.set(RtlSections.valueOf(value));
          } catch(IllegalArgumentException ex) {
            System.err.println("Soubor nezačíná sekcí HEADER!");
          }
          break;
      }
    });
    return rtlFileWrap = new RtlFileWrap(rtlHeader, rtlTargetData, rtlUserText, rtlRuns, rtlDeviations, rtlEnvironment);
  }

  private boolean tryChangeSection(String value) {
    value = value.replace("::", "");
    value = value.replace(" ", "_");
    value = value.replace("-", "_");
    try {
      this.section.set(RtlSections.valueOf(value));
      return true;
    } catch(IllegalArgumentException ex) {
      return false;
    }
  }

  public RtlFileWrap getRtlFileWrap() {
    return rtlFileWrap;
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

  public String getText() {
    return textBuilder.toString();
  }
}
