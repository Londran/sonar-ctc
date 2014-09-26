package org.sonar.plugins.ctc.api;

import java.util.regex.Pattern;

public enum CtcReportDetailKey {

  MON_SYM("^\\QSymbol file(s) used   : \\E"),
  MON_DAT("^\\QData file(s) used     : \\E"),
  LIST_DATE("^\\QListing produced at   : \\E"),
  COV_VIEW("^\\QCoverage view         : \\E"),
  SRC_FILES("^\\QSource files       : \\E"),
  SRC_LINES("^\\QSource lines       : \\E"),
  MEASUREMENT_POINTS("^\\QMeasurement points : \\E"),
  TER_CONDITION("^\\QTER                : \\E"),
  TER_STATEMENT("^\\QTER                : \\E");

  private final Pattern pattern;

  private CtcReportDetailKey(String pattern) {
    this.pattern = Pattern.compile(pattern, Pattern.MULTILINE);
  }

  public Pattern getPattern() {
    return pattern;
  }

}
