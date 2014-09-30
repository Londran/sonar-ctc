/*
 * Testwell CTC++ Plugin
 * Copyright (C) 2014 Verifysoft Technology GmbH
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
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
