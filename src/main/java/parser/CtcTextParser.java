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
package parser;

import org.sonar.plugins.ctc.api.exceptions.CtcInvalidReportException;
import org.sonar.plugins.ctc.api.measures.CtcFileMeasure;
import org.sonar.plugins.ctc.api.measures.CtcFileMeasure.Builder;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.Marker;
import com.google.common.collect.AbstractIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;

import static parser.CtcPattern.*;

public class CtcTextParser implements CtcParser {

  private final Scanner scanner;
  private final Map<CtcPattern, String> projectDetails;
  private final Matcher matcher;
  private final StringBuilder sb;

  private final static Marker FOUND = MarkerFactory.getMarker("FOUND");


  private CtcFileMeasure nextElement;


  private final static Logger log = LoggerFactory.getLogger(CtcTextParser.class);

  public CtcTextParser(File report) throws FileNotFoundException {
    scanner = new Scanner(report).useDelimiter(SECTION_SEP);
    matcher = APPENDAGE.matcher("");
    projectDetails = new EnumMap<CtcPattern, String>(CtcPattern.class);
    sb = new StringBuilder();

    if (scanner.findWithinHorizon(MON_SYM.PATTERN,0) == null) {
      logInvalidReport();
    }
    addAppended(MON_DAT);
    projectDetails.put(MON_SYM, ","+sb.toString());
    // MON.sym added + additional *.sym-files
    // MON.dat has been found and could be added instantly

    sb.setLength(0);
    sb.append(scanner.nextLine());
    addAppended(LIS_DTE);
    projectDetails.put(MON_DAT, sb.toString());
    projectDetails.put(LIS_DTE, scanner.nextLine());

    if (scanner.findInLine(COV_VIW.PATTERN) == null) {
      logInvalidReport();
    }
    projectDetails.put(COV_VIW, scanner.nextLine());
  }
  /* Appends additional files. Scannercursor points at value of
   * nextKey.
   *
   */
  private void addAppended(CtcPattern nextKey) {
    sb.append(scanner.nextLine());
    while(scanner.findInLine(nextKey.PATTERN) == null) {
      if (matcher.reset(scanner.nextLine()).matches()) {
        sb.append(matcher.group(1));
      } else {
        logInvalidReport();
      }
    }
  }

  private void parseFileHeader() {
    matcher.usePattern(MONI_SRC);
    if (matcher.find()) {
      log.debug(FOUND,"MONITORED {} FILE : {}",matcher.group(1),matcher.group(2));
    }
    matcher.usePattern(INST_MOD);
    if (matcher.find()) {
      log.debug(FOUND,"MONITORED {} FILE : {}",matcher.group(1),matcher.group(2));
    }
  }

  private void parseFileBody() {
    
  }

  private void logInvalidReport() {
    log.error("INVALID REPORT!");
    throw new CtcInvalidReportException();
  }

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public CtcFileMeasure next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return this.nextElement;
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
