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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.collections.map.HashedMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.sonar.plugins.ctc.api.CtcReportDetailKey.*;

import com.google.common.collect.AbstractIterator;

public class CtcTextParser extends AbstractIterator<CtcFileMeasure> implements CtcParser {

  private final Scanner scanner;
  private final Map<CtcReportDetailKey, String> projectDetails;
  private final Matcher matcher;

  private final static Logger log = LoggerFactory.getLogger(CtcTextParser.class);
  private final static Pattern BLANK = Pattern.compile("\\s*");

  private final static Pattern SECTION_DELIMITER = Pattern.compile("^-{77}$", Pattern.MULTILINE);

  private final static Pattern FILE_MEASURE_BEGIN = Pattern.compile("^={78}$", Pattern.MULTILINE);
  // ***TER 82 % ( 14/ 17) of FILE Calc.java
  private final static Pattern SUMMARY = Pattern.compile("^(?:\\Q***TER\\E|\\s{6}) (?:[ 0-9]{3}) % \\( {0,2}([0-9]{1,3})/ {0,2}([0-9]{1,3})\\) statement$",
    Pattern.MULTILINE);
  private final static Pattern LINE_RESULT_PATTERN = Pattern.compile("^(?: {10}| *([0-9]+)) (?: {10}| *([0-9]+)) [ -][ ]{1,5}([0-9]+).*$", Pattern.MULTILINE);

  public CtcTextParser(File report) throws FileNotFoundException {
    scanner = new Scanner(report);
    projectDetails = new EnumMap<CtcReportDetailKey, String>(CtcReportDetailKey.class);
    matcher = BLANK.matcher("");

    // Get first Project Detail
    scanner.findWithinHorizon(CtcReportDetailKey.MON_SYM.getPattern(), 0);
    StringBuilder monSym = new StringBuilder(scanner.nextLine());
    while (scanner.findInLine(MON_DAT.getPattern()) == null) {
      scanner.skip(BLANK);
      monSym.append(",").append(scanner.nextLine());
    }
    monSym.trimToSize();

    StringBuilder monDat = new StringBuilder(scanner.nextLine());
    while (scanner.findInLine(LIST_DATE.getPattern()) == null) {
      scanner.skip(BLANK);
      monDat.append(",").append(scanner.nextLine());
    }

    String listDate = scanner.nextLine();

    if (scanner.findInLine(COV_VIEW.getPattern()) == null) {
      throw new IllegalReportException();
    }

    String coverageView = scanner.nextLine();

    scanner.useDelimiter(SECTION_DELIMITER);

  }

  @Override
  protected CtcFileMeasure computeNext() {
    if (scanner.hasNext()) {
      if (matcher.reset(scanner.next()).pattern() != LINE_RESULT_PATTERN)
        matcher.usePattern(LINE_RESULT_PATTERN);
      if (matcher.find()) {
        do {
          log.debug("[LINE] {}", matcher.group(3));
          log.trace("  +--  TRUE:  {}", matcher.group(1));
          log.trace("  +--  FALSE: {}", matcher.group(2));
        } while (matcher.find());
      }
      else if (matcher.usePattern(SUMMARY).reset().find()) {
        log.debug("[STMT] ({}/{})", matcher.group(1), matcher.group(2));
      }
      return new CtcFileMeasure();

    }

    return endOfData();
  }

  private CtcFileMeasure parseFromFile() throws IOException {

    return new CtcFileMeasure();
  }

  @Override
  public Map<CtcReportDetailKey, String> getReportDetails() {

    if (projectDetails != null) {
      return projectDetails;
    }

    return projectDetails;
  }

}
