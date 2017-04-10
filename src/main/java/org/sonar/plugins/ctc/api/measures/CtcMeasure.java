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
package org.sonar.plugins.ctc.api.measures;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.SortedMap;

/**
 * This class represents a model of CtcMeasures
 *
 * It could consist of file- or project-measures. If the source is NULL, then it
 * consists of project measures.
 *
 * @author Sebastian Goetzinger <goetzinger@verifysoft.com>
 *
 */
@SuppressWarnings("rawtypes")
public class CtcMeasure {

  private static final Logger LOG = LoggerFactory.getLogger(CtcMeasure.class);

  private final File source;

  /**
   * The File-Metrics
   */
  private long totalCoveredLines = 0, totalStatements = 0, totalCoveredStatements = 0, totalConditions = 0, totalCoveredConditions = 0, measurePoints = 0;
  private final SortedMap<Long, Long> conditionsByLine = Maps.newTreeMap();
  private final SortedMap<Long, Long> coveredConditionsByLine = Maps.newTreeMap();
  private final SortedMap<Long, Long> hitsByLine = Maps.newTreeMap();

  public CtcMeasure(final File source) {
    this.source = source;
    if (source != null && !source.exists()) {
      LOG.error("File {} does not exist!", source);

    }
    if (source != null) {
      LOG.trace("AbsoluteFilePath of {} : {}", source, source.getAbsolutePath());
    } else {
      LOG.trace("PROJECTMEASURE");
    }

  }

  /**
   * Returns the measured source-file.
   *
   * @return the sourcefile or null if these are project measures
   */
  public File getSource() {
    return source;
  }

  /**
   * Resets the builder and returns itself
   */
  public void reset() {
    totalStatements = 0;
    totalCoveredStatements = 0;
    totalConditions = 0;
    totalCoveredConditions = 0;
    totalCoveredLines = 0;
    conditionsByLine.clear();
    coveredConditionsByLine.clear();
  }

  /**
   * Sets the Statements of this builder
   *
   * @param covered amount of covered statements
   * @param statement amount of statements
   */
  public void setStatememts(long covered, long statement) {
    totalCoveredStatements = covered;
    totalStatements = statement;
  }

  /**
   * Sets the hits per Line
   *
   * @param lineId hitted line
   * @param hits number of hits
   */
  public void setHits(long lineId, long hits) {
    if (!hitsByLine.containsKey(lineId)) {
      hitsByLine.put(lineId, hits);
      if (hits > 0) {
        totalCoveredLines += 1;
      }
    }
  }

  /**
   * Sets the conditions per line.
   *
   * @param lineId
   * @param conditions
   * @param coveredConditions
   */
  public void setConditions(long lineId, long conditions, long coveredConditions) {
    if (conditions > 0 && !conditionsByLine.containsKey(lineId)) {
      totalConditions += conditions;
      totalCoveredConditions += coveredConditions;
      conditionsByLine.put(lineId, conditions);
      coveredConditionsByLine.put(lineId, coveredConditions);
    }
  }

  public long getCoveredLines() {
    return totalCoveredLines;
  }

  public int getLinesToCover() {
    return hitsByLine.size();
  }

  public SortedMap<Long, Long> getHitsByLine() {
    return Collections.unmodifiableSortedMap(hitsByLine);
  }

  public long getCoveredStatements() {
    return totalCoveredStatements;
  }

  public long getStatements() {
    return totalStatements;
  }

  public long getConditions() {
    return totalConditions;
  }

  public long getCoveredConditions() {
    return totalCoveredConditions;
  }

  public SortedMap<Long, Long> getConditionsByLine() {
    return Collections.unmodifiableSortedMap(conditionsByLine);
  }

  public SortedMap<Long, Long> getCoveredConditionsByLine() {
    return Collections.unmodifiableSortedMap(coveredConditionsByLine);
  }

  public CtcMeasure build() {
    return this;
  }

  public void setMeasurePoints(long mp) {
    measurePoints = mp;
  }

  public long getMeasurePoints() {
    return measurePoints;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (source == null) {
      sb.append("Report Measures: ");
    } else {
      sb.append("File ").append(source.toString()).append(" Measures: \n");
    }
    return sb.toString();
  }
}
