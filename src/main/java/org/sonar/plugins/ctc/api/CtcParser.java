package org.sonar.plugins.ctc.api;

import java.util.Iterator;
import java.util.Map;

public interface CtcParser extends Iterator<CtcFileMeasure> {

  Map<CtcReportDetailKey, String> getReportDetails();

}
