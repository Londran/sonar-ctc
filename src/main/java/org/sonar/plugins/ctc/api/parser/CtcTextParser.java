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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.sonar.plugins.ctc.api.exceptions.CtcInvalidReportException;
import org.sonar.plugins.ctc.api.measures.CtcFileMeasure;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;

import static org.sonar.plugins.ctc.api.parser.CtcPattern.*;

public class CtcTextParser implements CtcParser {

  private final Scanner scanner;
  private final Map<CtcPattern, String> projectDetails;
  private final Matcher matcher;
  private final StringBuilder sb;

  private final static Marker FOUND = MarkerFactory.getMarker("FOUND");

  private final static Logger log = LoggerFactory.getLogger(CtcTextParser.class);

  public CtcTextParser(File report) throws FileNotFoundException {
    this(report,new EnumMap<CtcPattern, String>(CtcPattern.class));
  }

  public CtcTextParser(File report, Map<CtcPattern,String> map) throws FileNotFoundException {
    scanner = new Scanner(report).useDelimiter(SECTION_SEP);
    matcher = APPENDAGE.matcher("");
    projectDetails = map;
    sb = new StringBuilder();

    if (scanner.findWithinHorizon(MON_SYM.PATTERN,0) == null) {
      logInvalidReport();
    }
    addAppended(MON_DAT);
    addDetail(MON_SYM, ","+sb.toString());
    // MON.sym added + additional *.sym-files
    // MON.dat has been found and could be added instantly

    sb.setLength(0);

    addAppended(LIS_DTE);
    addDetail(MON_DAT, sb.toString());
    addDetail(LIS_DTE,scanner.nextLine());

    if (scanner.findInLine(COV_VIW.PATTERN) == null) {
      logInvalidReport();
    }
    addDetail(COV_VIW, scanner.nextLine());
    matcher.reset(scanner.next());
  }

  private void addDetail(CtcPattern key, String value) {
    projectDetails.put(key, value);
    log.debug(FOUND,"Detail '{}':'{}'",key,value);
  }
  /* Appends additional files. Scannercursor points at value of
   * nextKey.
   *
   */
  private void addAppended(CtcPattern nextKey) {
    sb.append(scanner.nextLine());
    while(scanner.findInLine(nextKey.PATTERN) == null) {
      if (matcher.reset(scanner.nextLine()).matches()) {
        sb.append(",");
        sb.append(matcher.group(1));
      } else {
        logInvalidReport();
      }
    }
  }

  private void parseFileHeader() {
    matcher.usePattern(MONI_SRC).reset();
    if (matcher.find()) {
      log.debug(FOUND,"MONITORED {} FILE : {}",matcher.group(1),matcher.group(2));
    } else {
      logInvalidReport();
    }
    matcher.usePattern(INST_MOD);
    if (matcher.find()) {
      log.debug(FOUND,"INSTRUMENTATION MODE: {}",matcher.group(1));
    } else {
      logInvalidReport();
    }
    parseFileBody();
  }

  private void parseFileBody() {
    if(matcher.usePattern(LINE_RESULT).reset(scanner.next()).find()) {
      do {
        log.trace(FOUND,"Line found!");
      } while (matcher.find());
      parseFileBody();
    } else if (matcher.usePattern(FILE_RESULT).reset().find()) {
      parseFileFooter();
    } else {
      logInvalidReport();
    }

  }

  private void parseFileFooter() {
    if (matcher.group(1) == null) {
      logInvalidReport();
    }
    log.debug(FOUND,"Found Result for: {}",matcher.group(4));
    log.trace(FOUND,"({}/{})",matcher.group(2),matcher.group(3));

    if (matcher.find()) {
      if (matcher.group(1) != null | matcher.group(5) == null) {
        logInvalidReport();
      }
      log.trace(FOUND,"Found statements ({}/{})",matcher.group(2),matcher.group(3));
    }
    if (matcher.usePattern(SUMMARY).reset(scanner.next()).find()) {
      log.debug("Found end of Report!");
      for (CtcPattern key : Arrays.asList(SRC_FLS,SRC_LNS,MEA_PTS)) {
        if (!matcher.usePattern(key.PATTERN).find()) {
          logInvalidReport();
        } else {
          addDetail(key, matcher.group(1));
        }
      }
    }
  }

  private void logInvalidReport() {
    log.error("INVALID REPORT!");
    log.trace(matcher.toString());

    throw new CtcInvalidReportException();
  }

  @Override
  public boolean hasNext() {
    return scanner.hasNext();
  }

  @Override
  public CtcFileMeasure next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    parseFileHeader();
    return null;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("");

  }

  @Override
  public Map<CtcPattern, String> getReportDetails() {
    // TODO Auto-generated method stub
    return null;
  }
}
