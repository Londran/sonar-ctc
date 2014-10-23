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

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.Builder;
import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.SumChildValuesFormula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static org.sonar.api.measures.CoreMetrics.DOMAIN_OVERALL_TESTS;
import static org.sonar.api.measures.Metric.DIRECTION_BETTER;
import static org.sonar.api.measures.Metric.ValueType.DATA;
import static org.sonar.api.measures.Metric.ValueType.INT;
import static org.sonar.api.measures.Metric.ValueType.PERCENT;

@SuppressWarnings("rawtypes")
public class CtcMetrics implements Metrics {

  public static final String CTC_STATEMENTS_TO_COVER_KEY = "ctc_statements_to_cover";
  public static final Metric<Integer> CTC_STATEMENTS_TO_COVER = new Builder(CTC_STATEMENTS_TO_COVER_KEY, "Overall statements to cover", INT)
    .setDescription("Overall statements to cover by all tests")
    .setDirection(Metric.DIRECTION_NONE)
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setQualitative(false)
    .setFormula(new SumChildValuesFormula(false))
    .setHidden(true)
    .setDeleteHistoricalData(true)
    .create();

  public static final String CTC_UNCOVERED_STATEMENTS_KEY = "ctc_uncovered_statements";
  public static final Metric<Integer> CTC_UNCOVERED_STATEMENTS = new Builder(CTC_UNCOVERED_STATEMENTS_KEY, "Overall uncovered statements", INT)
    .setDescription("Uncovered lines by all tests")
    .setDirection(Metric.DIRECTION_WORST)
    .setQualitative(false)
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setFormula(new SumChildValuesFormula(false))
    .create();

  public static final String CTC_STATEMENT_COVERAGE_KEY = "ctc_statement_coverage";
  public static final Metric<Double> CTC_STATEMENT_COVERAGE = new Builder(CTC_STATEMENT_COVERAGE_KEY, "Overall statement coverage", PERCENT)
    .setDescription("Statement coverage by all tests")
    .setDirection(Metric.DIRECTION_BETTER)
    .setQualitative(true)
    .setDomain(DOMAIN_OVERALL_TESTS)
    .create();

  public static final String CTC_CONDITIONS_TO_COVER_KEY = "ctc_conditions_to_cover";
  public static final Metric<Integer> CTC_CONDITIONS_TO_COVER = new Builder(CTC_CONDITIONS_TO_COVER_KEY, "Overall branches to cover", INT)
    .setDescription("Conditions to cover by all tests")
    .setDirection(Metric.DIRECTION_BETTER)
    .setQualitative(false)
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setFormula(new SumChildValuesFormula(false))
    .setHidden(true)
    .create();

  public static final String CTC_UNCOVERED_CONDITIONS_KEY = "ctc_uncovered_conditions";
  public static final Metric<Integer> CTC_UNCOVERED_CONDITIONS = new Builder(CTC_UNCOVERED_CONDITIONS_KEY, "Overall uncovered conditions", INT)
    .setDescription("Uncovered conditions by all tests")
    .setDirection(Metric.DIRECTION_WORST)
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setFormula(new SumChildValuesFormula(false))
    .create();

  public static final String CTC_CONDITIONS_BY_LINE_KEY = "ctc_conditions_by_line";
  public static final Metric<String> CTC_CONDITIONS_BY_LINE = new Builder(CTC_CONDITIONS_BY_LINE_KEY, "Overall conditions by line", DATA)
    .setDescription("Overall conditions by all tests and by line")
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setDeleteHistoricalData(true)
    .create();

  public static final String CTC_COVERED_CONDITIONS_BY_LINE_KEY = "ctc_covered_conditions_by_line";
  public static final Metric<String> CTC_COVERED_CONDITIONS_BY_LINE = new Builder(CTC_COVERED_CONDITIONS_BY_LINE_KEY, "Overall covered conditions by line",
    Metric.ValueType.DATA)
    .setDescription("Overall covered conditions by all tests and by line")
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setDeleteHistoricalData(true)
    .create();

  public static final String CTC_CONDITION_COVERAGE_KEY = "ctc_condition_coverage";
  public static final Metric<Double> CTC_CONDITION_COVERAGE = new Metric.Builder(CTC_CONDITION_COVERAGE_KEY, "Overall branch coverage", PERCENT)
    .setDescription("Condition coverage by all tests")
    .setDirection(Metric.DIRECTION_BETTER)
    .setQualitative(true)
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setWorstValue(0.0)
    .setBestValue(100.0)
    .create();

  public static final String CTC_MEASURE_POINTS_KEY = "ctc_measure_points";
  public static final Metric<Integer> CTC_MEASURE_POINTS = new Builder(CTC_MEASURE_POINTS_KEY, "Overall used measurement points", INT)
    .setDescription("Overall used measurement points by CTC++")
    .setDomain(DOMAIN_OVERALL_TESTS)
    .setDirection(DIRECTION_BETTER)
    .setQualitative(false)
    .create();



  public static final List<Metric> FILE_METRICS = new ListBuilder()
    .add(CTC_CONDITIONS_TO_COVER,CTC_CONDITIONS_BY_LINE,CTC_COVERED_CONDITIONS_BY_LINE,CTC_UNCOVERED_CONDITIONS)
    .add(CTC_STATEMENT_COVERAGE,CTC_UNCOVERED_STATEMENTS)
    .build();
  public static final List<Metric> PROJECT_METRICS = new ListBuilder()
    .add(CTC_MEASURE_POINTS)
    .build();
  public static final List<Metric> RELATIVE_METRICS = new ListBuilder()
    .add(CTC_CONDITION_COVERAGE,CTC_STATEMENT_COVERAGE)
    .build();
  public static final List<Metric> METRICS = new ListBuilder()
    .add(FILE_METRICS).add(PROJECT_METRICS).add(RELATIVE_METRICS).build();

  @Override
  public List<Metric> getMetrics() {
    return METRICS;
  }

  private static class ListBuilder {

    private LinkedHashSet<Metric> elements;

    private ListBuilder() {
      elements = new LinkedHashSet<Metric>();
    }

    private ListBuilder add(Collection<Metric> metrics) {
      elements.addAll(metrics);
      return this;
    }

    private ListBuilder add(Metric ... metrics) {
      for (Metric metric : metrics) {
        elements.add(metric);
      }
      return this;
    }

    private List<Metric> build() {
      return new ArrayList<Metric>(elements);
    }
  }
}
