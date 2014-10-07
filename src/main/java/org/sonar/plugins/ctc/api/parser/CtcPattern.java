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
import static java.util.regex.Pattern.*;

public enum CtcPattern {

  MON_SYM("Symbol file(s) used   : "),
  MON_DAT("Data file(s) used     : "),
  LIS_DTE("Listing produced at   : "),
  COV_VIW("Coverage view         : "),
  SRC_FLS("Source files       : ","(.*)$"),
  SRC_LNS("Source lines       : ","(.*)$"),
  MEA_PTS("Measurement points : ","(.*)$");

  public static final Pattern APPENDAGE = compile("^\\s+(.*)$",MULTILINE);
  public static final Pattern MONI_SRC = compile("^MONITORED (.{6}) FILE : (.*)$",MULTILINE);
  public static final Pattern INST_MOD = compile("^INSTRUMENTATION MODE  : (.*)$",MULTILINE);
  public static final Pattern SECTION_SEP = compile("^-{77}|={77}$",MULTILINE);
  public static final Pattern LINE_RESULT = compile("^(?: {10}| *(\\d+)) (?: {10}| *(\\d+)) -? *(\\d+) (.*)$",MULTILINE);
  public static final Pattern FILE_RESULT = compile("^(?:(\\Q***TER\\E)| {6}) *\\d+ % \\( *(\\d+)/ *(\\d+)\\) (?:of FILE (.*)|(statement))$",MULTILINE);
  public static final Pattern SUMMARY = compile("^SUMMARY$",MULTILINE);

  /*
***TER  82 % ( 14/ 17) of FUNCTION Calc::isPrime()
        91 % ( 10/ 11) statement
   */


  public final Pattern PATTERN;

  private CtcPattern(String key) {
    this(key,"");
  }

  private CtcPattern(String key, String value) {
    PATTERN = compile("^"+quote(key)+value, MULTILINE);
  }

}