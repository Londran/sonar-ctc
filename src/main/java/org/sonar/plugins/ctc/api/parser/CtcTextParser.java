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

import com.google.common.collect.AbstractIterator;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.ctc.api.exceptions.CtcInvalidReportException;
import org.sonar.plugins.ctc.api.measures.CtcMeasure;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

//import org.sonar.plugins.ctc.api.parser.CtcCondition;

import static org.sonar.plugins.ctc.api.parser.CtcResult.FILE_HEADER;
import static org.sonar.plugins.ctc.api.parser.CtcResult.FILE_RESULT;
import static org.sonar.plugins.ctc.api.parser.CtcResult.LINE_RESULT;
import static org.sonar.plugins.ctc.api.parser.CtcResult.SECTION_SEP;

public class CtcTextParser extends AbstractIterator<CtcMeasure> implements CtcParser {

  private static final int FALSE_CONDS = 1;
  private static final int TRUE_CONDS = 2;
  private static final int FROM_START = 0;
  private static final int WHOLE_GROUP = 0;
  private static final int MP_GROUP = 3;
  private static final int STMNT_TO_COVER_GROUP = 4;
  private static final int STMNT_COVERED_GROUP = 3;
  private static final int LINE_NR_GROUP = 3;
  private final Scanner scanner;
  private final Matcher matcher;
  private State state;
  private final CtcMeasure projectBuilder;

  private enum State {
    BEGIN,
    PARSING,
    END;
  }

  private static final Logger LOG = LoggerFactory.getLogger(CtcTextParser.class);

  public CtcTextParser(File report) throws FileNotFoundException {
    scanner = new Scanner(report).useDelimiter(SECTION_SEP);
    matcher = CtcResult.FILE_HEADER.matcher("");
    state = State.BEGIN;
    projectBuilder = new CtcMeasure(null);
  }

  @Override
  protected CtcMeasure computeNext() {
    LOG.debug("Computing next in state: {}", state);
    switch (state) {
      case BEGIN:
        return parseReportHead();
      case PARSING:
        return parseUnit();
      case END:
        return endOfData();
      default:
        throw new IllegalStateException("No known state!");
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
	  LOG.info(matcher.toString());
    if (matcher.usePattern(FILE_HEADER).find(FROM_START)) {
      return parseFileUnit();
    } else if (matcher.usePattern(CtcResult.REPORT_FOOTER).find(FROM_START)) {
      parseReportUnit();
      state = State.END;
      scanner.close();
      return projectBuilder.build();
    } else {
      LOG.error("Illegal format!");
      throw new CtcInvalidReportException("Neither File Header nor Report Footer found!");
    }

  }

  private CtcMeasure parseFileUnit() {
    File file = new File("./" + matcher.group(1));
    CtcMeasure bob = new CtcMeasure(file);
    try {
      addLines(bob);
    } catch (NoSuchElementException e) {
      Log.error("Lines could not be added!", e);
      throw new CtcInvalidReportException("File Section not ended properly");
    }
    addStatements(bob);
    matcher.reset(scanner.next());
    return bob.build();
  }

  private void addEachLine(Map<Long, Set<CtcCondition>> buffer) {
    long nextLineId;
    String condsTrue;
    String condsFalse;
    CtcCondition lastCtcCondition = null;
    CtcCondition ctcCondition = null;
    
    do {
      long lindId = Long.parseLong(matcher.group(LINE_NR_GROUP));
      Set<CtcCondition> line = buffer.get(lindId);
      if (line == null) {
        line = new HashSet<CtcCondition>();
        buffer.put(lindId, line);
      }        
      
      condsTrue = matcher.group(TRUE_CONDS);
      condsFalse = matcher.group(FALSE_CONDS);
      if ((condsTrue != null) || (condsFalse != null)) {
        ctcCondition = new CtcCondition(lindId, 
          (condsTrue != null ? new BigDecimal(condsTrue).longValueExact() : 0), 
          (condsFalse != null ? new BigDecimal(condsFalse).longValueExact() : 0), 
          (condsTrue != null && condsFalse != null));
        line.add(ctcCondition);
      }
      else
      {
        String blockEnd = matcher.group(4);
        if (blockEnd != null) {
          if (blockEnd.endsWith("-")) {
            ctcCondition = new CtcCondition(lindId, 0, 0, false);
          } else if (blockEnd.endsWith("+")) {
            ctcCondition = new CtcCondition(lindId, 1, 1, false);
          }
          line.add(ctcCondition);
        }
      }

      if (lastCtcCondition != null) {
        nextLineId = lastCtcCondition.getLineId() + 1;
        while (nextLineId < lindId) {
          line.add(new CtcCondition(nextLineId, 0, lastCtcCondition.getConditionFalse(), false));
          nextLineId++;
        }
        buffer.put(nextLineId, line);
      }
      lastCtcCondition = ctcCondition;
    } while (matcher.find());
  }

  private void parseLineSection(Map<Long, Set<CtcCondition>> buffer) {
    LOG.trace("Found linesection...");
    
    if (matcher.usePattern(LINE_RESULT).find(FROM_START)) {
      addEachLine(buffer);
    } else  {
      LOG.error("Neither File Result nor Line Result after FileHeader!");
      LOG.trace("Matcher: {}", matcher);
      throw new CtcInvalidReportException("Neither FileResult nor FileHeader.");
    }
  }

  private void addLines(CtcMeasure bob) throws NoSuchElementException {
    LOG.trace("Adding lines...");
    Map<Long, Set<CtcCondition>> buffer = new TreeMap<Long, Set<CtcCondition>>();
    while (!matcher.reset(scanner.next()).usePattern(FILE_RESULT).find()) {
      parseLineSection(buffer);
    }
    LOG.trace("Found matches: {}", buffer);
    
    for (Entry<Long, Set<CtcCondition>> line : buffer.entrySet()) {
      addConditions(line, bob);
      addLineHit(line, bob);
    }
  }
  
  private void addLineHit(Entry<Long, Set<CtcCondition>> line, CtcMeasure bob) {

    for (CtcCondition result : line.getValue()) {
        bob.setHits(result.getLineId(), result.getLineHits());
    }
  }

  private void addConditions(Entry<Long, Set<CtcCondition>> line, CtcMeasure bob) {
    long lineId = line.getKey();
    LOG.trace("LineId: {}", lineId);
    long conditions = 0;
    long coveredConditions = 0;
    for (CtcCondition result : line.getValue()) {
      
      if (result.isCondition() == true)
      {
        conditions += 2;
        if (result.getConditionFalse() > 0) {
          coveredConditions++;
        }
        if (result.getConditionTrue() > 0) {
          coveredConditions++;
        }
        
        LOG.trace("Conditioncoverage: {}/{}", coveredConditions, conditions);
        bob.setConditions(lineId, conditions, coveredConditions);
      }
    }
  }

  private void addStatements(CtcMeasure bob) {
    try {
      long covered = Long.parseLong(matcher.group(STMNT_COVERED_GROUP));
      long statement = Long.parseLong(matcher.group(STMNT_TO_COVER_GROUP));
      bob.setStatememts(covered, statement);
      LOG.trace("Statements: {}/{}", covered, statement);
    } catch (NumberFormatException e) {
      LOG.error("Could not read File Statments!");
      LOG.trace("Matcher: {}", matcher);
    }
  }

  private void parseReportUnit() {
    long mp = 0;
    try {
      mp = Long.parseLong(matcher.group(MP_GROUP));
    } catch (NumberFormatException e) {
      LOG.error("Could not parse '{}' to Integer", matcher.group(MP_GROUP));
      LOG.trace("Whole Group: {}", matcher.group(WHOLE_GROUP));
    }
    projectBuilder.setMeasurePoints(mp);
  }

}
