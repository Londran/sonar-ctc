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

@SuppressWarnings("rawtypes")
public class CtcMeasure {


  private CtcMeasure(final File SOURCE, final Collection<Measure>  collection) {
    this.SOURCE = SOURCE;
    this.MEASURES = collection;
  }

  public final File SOURCE;
  public final Collection<Measure> MEASURES;

  public static class FileMeasureBuilder {

    public static final List<Metric> METRICS = CtcMetrics.FILE_METRICS;


    private int totalStatements = 0, totalCoveredStatements = 0, totalConditions = 0, totalCoveredConditions = 0;
    private SortedMap<Integer, Integer> conditionsByLine = Maps.newTreeMap();
    private SortedMap<Integer, Integer> coveredConditionsByLine = Maps.newTreeMap();

    public final File FILE;

    private FileMeasureBuilder(File file) {
      this.FILE = file;
    }

    public FileMeasureBuilder reset() {
      totalStatements = 0;
      totalCoveredStatements = 0;
      totalConditions = 0;
      totalCoveredConditions = 0;
      conditionsByLine.clear();
      coveredConditionsByLine.clear();
      return this;
    }

    public FileMeasureBuilder setStatememts(int covered, int statement) {
      totalStatements =statement;
      totalCoveredStatements = covered;
      return this;
    }

    public FileMeasureBuilder setConditions(int lineId, int conditions, int coveredConditions) {
      if (conditions > 0 && !conditionsByLine.containsKey(lineId)) {
        totalConditions += conditions;
        totalCoveredConditions += coveredConditions;
        conditionsByLine.put(lineId, conditions);
        coveredConditionsByLine.put(lineId, coveredConditions);
      }
      return this;
    }

    public int getCoveredStatements() {
      return totalCoveredStatements;
    }

    public int getStatements() {
      return totalStatements;
    }

    public int getConditions() {
      return totalConditions;
    }

    public int getCoveredConditions() {
      return totalCoveredConditions;
    }

    public SortedMap<Integer, Integer> getConditionsByLine() {
      return Collections.unmodifiableSortedMap(conditionsByLine);
    }

    public SortedMap<Integer, Integer> getCoveredConditionsByLine() {
      return Collections.unmodifiableSortedMap(coveredConditionsByLine);
    }

    public Collection<Measure> createMeasures() {
      Collection<Measure> measures = new ArrayList<Measure>();
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
      return new CtcMeasure(FILE, createMeasures());
    }

    public static FileMeasureBuilder create(File file) {
      return new FileMeasureBuilder(file);
    }
  }

  public static class ProjectMeasureBuilder {

    int measurePoints = 0;

    public ProjectMeasureBuilder setMeasurePoints(int mp) {
      measurePoints = mp;
      return this;
    }

    public Collection<Measure> createMeasures() {
      Collection<Measure> measures = new ArrayList<Measure>();
      if (measurePoints > 0) {
        measures.add(new Measure<Serializable>(CtcMetrics.CTC_MEASURE_POINTS).setValue((double)measurePoints));
      }
      return measures;
    }

    public CtcMeasure build() {
      return new CtcMeasure(null, createMeasures());
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (SOURCE == null) sb.append("Report Measures: ");
    else sb.append("File ").append(SOURCE.toString()).append(" Measures: \n");

    for (Measure measure : MEASURES) {
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