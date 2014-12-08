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

import static org.sonar.plugins.ctc.api.parser.CtcResult.FILE_HEADER;
import static org.sonar.plugins.ctc.api.parser.CtcResult.FILE_RESULT;
import static org.sonar.plugins.ctc.api.parser.CtcResult.LINE_RESULT;
import static org.sonar.plugins.ctc.api.parser.CtcResult.SECTION_SEP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.ctc.api.exceptions.CtcInvalidReportException;
import org.sonar.plugins.ctc.api.measures.CtcMeasure;
import org.sonar.plugins.ctc.api.measures.CtcMeasure.FileMeasureBuilder;

import com.google.common.collect.AbstractIterator;

public class CtcTextParser extends AbstractIterator<CtcMeasure> implements CtcParser  {

  private final Scanner scanner;
  private final Matcher matcher;
  private State state;
  private final CtcMeasure.ProjectMeasureBuilder projectBuilder;

  private enum State {
    BEGIN,
    PARSING,
    END;
  }

  private final static Logger log = LoggerFactory.getLogger(CtcTextParser.class);


  @SuppressWarnings("resource")
public CtcTextParser(File report) throws FileNotFoundException {
    scanner = new Scanner(report).useDelimiter(SECTION_SEP);
    matcher = CtcResult.FILE_HEADER.matcher("");
    state = State.BEGIN;
    projectBuilder = new CtcMeasure.ProjectMeasureBuilder();
  }

  @Override
  protected CtcMeasure computeNext() {
    log.debug("Computing next in state: {}", state);
    switch (state) {
      case BEGIN : return parseReportHead();
      case PARSING : return parseUnit();
      case END : return endOfData();
      default : throw new IllegalStateException("No known state!");
    }
  }

  private CtcMeasure parseReportHead() {
    if (!matcher.reset(scanner.next()).find()) {
      throw new CtcInvalidReportException("File Header not found!");
    }
    state = State.PARSING;
    return parseUnit();
  }

  private CtcMeasure parseUnit() {
    if (matcher.usePattern(FILE_HEADER).find(0)) {
      return parseFileUnit();
    } else if (matcher.usePattern(CtcResult.REPORT_FOOTER).find(0)) {
      parseReportUnit();
      state = State.END;
      scanner.close();
      return projectBuilder.build();
    } else {
      log.error("Illegal format!");
      throw new CtcInvalidReportException ("Neither File Header nor Report Footer found!");
    }

  }

  private CtcMeasure parseFileUnit() {
    File file = new File("./" + matcher.group(1));
    CtcMeasure.FileMeasureBuilder bob = FileMeasureBuilder.create(file);
    try {
      addLines(bob);
    } catch (NoSuchElementException e) {
      throw new CtcInvalidReportException("File Section not ended properly");
    }
    addStatements(bob);
    matcher.reset(scanner.next());
    return bob.build();
  }

  private void addLines(CtcMeasure.FileMeasureBuilder bob) throws NoSuchElementException {
    log.debug("Adding lines...");
    Map<Integer,HashSet<MatchResult>> buffer = new TreeMap<Integer, HashSet<MatchResult>>();
    while (!matcher.reset(scanner.next()).usePattern(FILE_RESULT).find()) {
      log.debug("Found linesection...");
      if (matcher.usePattern(LINE_RESULT).find(0)) {
    	int start = Integer.parseInt(matcher.group(3));
    	int last;
    	log.debug("Function start: {}", start);
        do {
          last = Integer.parseInt(matcher.group(3));
          HashSet<MatchResult> line = buffer.get(last);
          if (line == null) {
            line = new HashSet<MatchResult>();
            buffer.put(Integer.parseInt(matcher.group(3)), line);
          }
          log.debug("Added line: {}",matcher.toMatchResult());
          line.add(matcher.toMatchResult());
        } while (matcher.find());
        log.debug("Function end: {}", last);
        
      } else {
        log.error("Neither File Result nor Line Result after FileHeader!");
        log.debug("Matcher: {}",matcher);
        throw new CtcInvalidReportException("Neither FileResult nor FileHeader.");
      }
    }
    log.debug("Found matches: {}",buffer);
    Entry<Integer,HashSet<MatchResult>> prev = null;
    for (Entry<Integer,HashSet<MatchResult>> line : buffer.entrySet()) {
      int lineId = line.getKey();
      log.debug("LineId: {}",lineId);
      int conditions = 0;
      int coveredConditions = 0;
      for (MatchResult result : line.getValue()) {
        for (int i = 1; i <= 2; i++) {
          String s = result.group(i);

          if (s != null) {
            conditions++;
            if (Integer.parseInt(s) > 0) {
              coveredConditions++;
            }
          }
        }
        
        if (prev != null) {
        	if (conditions == 0)
        	for (int i = prev.getKey(); i < line.getKey(); i++) {
        		bob.setHits(i, coveredConditions);
        	}
        }
        prev = line;
        
      }
      log.trace("Conditioncoverage: {}/{}",coveredConditions,conditions);
      bob.setConditions(lineId, conditions, coveredConditions);
    }
  }


  private void addStatements(CtcMeasure.FileMeasureBuilder bob) {
    try {
      int covered = Integer.parseInt(matcher.group(3));
      int statement = Integer.parseInt(matcher.group(4));
      bob.setStatememts(covered, statement);
      log.debug("Statements: {}/{}",covered,statement);
    } catch (NumberFormatException e) {
      log.error("Could not read File Statments!");
      log.debug("Matcher: {}", matcher);
    }
  }

  private void parseReportUnit() {
    int mp = 0;
    try {
    mp = Integer.parseInt(matcher.group(3));
    } catch (NumberFormatException e) {
      log.error("Could not parse '{}' to Integer", matcher.group(3));
      log.debug("Whole Group: {}", matcher.group(0));
    }
    projectBuilder.setMeasurePoints(mp);
  }

}
