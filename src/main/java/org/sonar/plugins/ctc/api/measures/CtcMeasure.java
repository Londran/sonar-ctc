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
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.utils.KeyValueFormat;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

/**
 * This class represents a model of CtcMeasures
 * 
 * It could consist of file- or project-measures.
 * If the source is NULL, then it consists of project measures.
 * @author Sebastian Götzinger <goetzinger@verifysoft.com>
 *
 */
@SuppressWarnings("rawtypes")
public class CtcMeasure {

  private static final Logger LOG = LoggerFactory.getLogger(CtcMeasure.class);

  private final File source;
  private final Collection<Measure> ctcMeasures;

  private CtcMeasure(final File source, final Collection<Measure> collection) {
    this.source = source;
    this.ctcMeasures = collection;
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
   * @return the sourcefile or null of these are project measures
   */
  public File getSOURCE() {
    return source;
  }
  
  /**
   * Returns the scanned measures as iterable collection
   * @return The Ctc-Measures
   */
  public Collection<Measure> getMEASURES() {
    return ctcMeasures;
  }

  /**
   * Builderclass for convenient CtcMeasure-Building
   * Used for FileMeasures
   * @author Sebastian Goetzinger <goetzinger@verifysoft.com>
   *
   */
  public static class FileMeasureBuilder {
    /**
     * The File-Metrics
     */
    public static final List<Metric> METRICS = CtcMetrics.FILE_METRICS;

    private long totalCoveredLines = 0, totalStatements = 0, totalCoveredStatements = 0, totalConditions = 0, totalCoveredConditions = 0;
    private SortedMap<Long, Long> conditionsByLine = Maps.newTreeMap();
    private SortedMap<Long, Long> coveredConditionsByLine = Maps.newTreeMap();
    private SortedMap<Long, Long> hitsByLine = Maps.newTreeMap();

    private final File file;

    private FileMeasureBuilder(File file) {
      this.file = file;
    }
    /**
     * Resets the builder and returns itself
     * @return the builder
     */
    public FileMeasureBuilder reset() {
      totalStatements = 0;
      totalCoveredStatements = 0;
      totalConditions = 0;
      totalCoveredConditions = 0;
      totalCoveredLines = 0;
      conditionsByLine.clear();
      coveredConditionsByLine.clear();
      return this;
    }

    /**
     * Sets the Statements of this builder
     * @param covered amount of covered statements
     * @param statement amount of statements
     * @return the builder
     */
    public FileMeasureBuilder setStatememts(long covered, long statement) {
      totalStatements = statement;
      totalCoveredStatements = covered;
      return this;
    }

    /**
     * Sets the hits per Line
     * @param lineId hitted line
     * @param hits number of hits
     * @return the builder
     */
    public FileMeasureBuilder setHits(long lineId, long hits) {
      if (!hitsByLine.containsKey(lineId)) {
        hitsByLine.put(lineId, hits);
        if (hits > 0) {
          totalCoveredLines += 1;
        }
      }
      return this;
    }

    /**
     * Sets the conditions per line.
     * @param lineId 
     * @param conditions
     * @param coveredConditions
     * @return
     */
    public FileMeasureBuilder setConditions(long lineId, long conditions, long coveredConditions) {
      if (conditions > 0 && !conditionsByLine.containsKey(lineId)) {
        totalConditions += conditions;
        totalCoveredConditions += coveredConditions;
        conditionsByLine.put(lineId, conditions);
        coveredConditionsByLine.put(lineId, coveredConditions);
      }
      return this;
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

    public Collection<Measure> createMeasures() {
      Collection<Measure> measures = new ArrayList<Measure>();
      if (getLinesToCover() > 0) {
        measures.add(new Measure(CtcMetrics.CTC_LINES_TO_COVER, (double) getLinesToCover()));
        measures.add(new Measure(CtcMetrics.CTC_UNCOVERED_LINES, (double) (getLinesToCover() - getCoveredLines())));
        measures.add(new Measure(CtcMetrics.CTC_COVERAGE_LINE_HITS_DATA).setData(KeyValueFormat.format(hitsByLine)).setPersistenceMode(PersistenceMode.DATABASE));
      }
      if (getStatements() > 0) {
        measures.add(new Measure(CtcMetrics.CTC_STATEMENTS_TO_COVER, (double) getStatements()));
        measures.add(new Measure(CtcMetrics.CTC_UNCOVERED_STATEMENTS, (double) (getStatements() - getCoveredStatements())));
      }
      if (getConditions() > 0) {
        measures.add(new Measure(CtcMetrics.CTC_CONDITIONS_TO_COVER, (double) getConditions()));
        measures.add(new Measure(CtcMetrics.CTC_UNCOVERED_CONDITIONS, (double) (getConditions() - getCoveredConditions())));
        measures.add(createConditionsByLine());
        measures.add(createCoveredConditionsByLine());
      }
      return measures;
    }

    private Measure createCoveredConditionsByLine() {
      return new Measure(CtcMetrics.CTC_COVERED_CONDITIONS_BY_LINE)
        .setData(KeyValueFormat.format(coveredConditionsByLine))
        .setPersistenceMode(PersistenceMode.DATABASE);
    }

    private Measure createConditionsByLine() {
      return new Measure(CtcMetrics.CTC_CONDITIONS_BY_LINE)
        .setData(KeyValueFormat.format(conditionsByLine))
        .setPersistenceMode(PersistenceMode.DATABASE);
    }

    public CtcMeasure build() {
      return new CtcMeasure(file, createMeasures());
    }

    public static FileMeasureBuilder create(File file) {
      if (!file.exists()) {
        LOG.error("File {} not found!", file);
      }
      LOG.debug("Absolute Filepath: '{}'", file.getAbsolutePath());
      return new FileMeasureBuilder(file);
    }
  }

  public static class ProjectMeasureBuilder {

    long measurePoints = 0;

    public ProjectMeasureBuilder setMeasurePoints(long mp) {
      measurePoints = mp;
      return this;
    }

    public Collection<Measure> createMeasures() {
      Collection<Measure> measures = new ArrayList<Measure>();
      if (measurePoints > 0) {
        measures.add(new Measure<Serializable>(CtcMetrics.CTC_MEASURE_POINTS).setValue((double) measurePoints));
      }
      return measures;
    }

    public CtcMeasure build() {
      return new CtcMeasure(null, createMeasures());
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (source == null) {
      sb.append("Report Measures: ");
    } else {
      sb.append("File ").append(source.toString()).append(" Measures: \n");
    }

    for (Measure measure : ctcMeasures) {
      Metric metric = measure.getMetric();
      sb.append("  Metric: ").append(metric.getName()).append("\n   Value: ");
      switch (metric.getType()) {
        case DATA:
          sb.append(measure.getData());
          break;
        case FLOAT:
          sb.append(measure.getValue());
          break;
        case INT:
          sb.append(measure.getIntValue());
          break;
        case MILLISEC:
          break;
        case PERCENT:
        default:
          sb.append(measure.toString());
          break;

      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
