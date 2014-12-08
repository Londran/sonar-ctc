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
package org.sonar.plugins.ctc.api.parser;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public enum CtcResult {

  MON_SYM("Symbol file\\(s\\) used   : (.*$(?:\\s+^ +.*$)*)"),
  MON_DAT("Data file\\(s\\) used     : (.*$(?:\\s+^ +.*$)*)"),
  LIS_DTE("Listing produced at   : (.*)$"),
  COV_VIW("Coverage view         : (.*)$"),
  SRC_FLS("Source files       : (.*)$"),
  SRC_LNS("Source lines       : (.*)$"),
  MEA_PTS("Measurement points : (.*)$"),
  FILE_MONI("MONITORED (?:.*) FILE : (.*)$"),
  FILE_INST("INSTRUMENTATION MODE  : (.*)$"),
  FILE_COND("^\\Q***TER\\E +\\d+ % \\( *(\\d+)/ *(\\d+)\\) of FILE (?:.*)$"),
  FILE_STMT("^ {6} +\\d+ % \\( *(\\d+)/ *(\\d+)\\) statement$");

  public static final Pattern REPORT_HEADER = Pattern.compile(MON_SYM.PATTERN_STRING + "\\s+" + MON_DAT.PATTERN_STRING
    + "\\s+" + LIS_DTE.PATTERN_STRING + "\\s+" + COV_VIW.PATTERN_STRING,MULTILINE);
  public static final Pattern REPORT_FOOTER = Pattern.compile(SRC_FLS.PATTERN_STRING + "\\s+" + SRC_LNS.PATTERN_STRING
    + "\\s+" + MEA_PTS.PATTERN_STRING,MULTILINE);
  public static final Pattern FILE_HEADER = Pattern.compile(FILE_MONI.PATTERN_STRING + "\\s+" + FILE_INST.PATTERN_STRING, MULTILINE);
  public static final Pattern SECTION_SEP = compile("^-{77}|={77}$",MULTILINE);
  public static final Pattern LINE_RESULT = compile("^(?: {10}| *(\\d+)) (?: {10}| *(\\d+)) -? *(\\d+) (.*)$",MULTILINE);
  public static final Pattern FILE_RESULT = compile(FILE_COND.PATTERN_STRING + "\\s+" + FILE_STMT.PATTERN_STRING,MULTILINE);


  private final String PATTERN_STRING;

  /*
         1                  8     1: T || _ || _
         0         58 -    15     if (value % divisor == 0)
   */

  private CtcResult(String key) {
     PATTERN_STRING = key;
  }


}
