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
package org.sonar.plugins.ctc;

import com.google.common.collect.ImmutableList;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CtcStatementCoverageDecorator extends CtcCoverageDecorator {

  public CtcStatementCoverageDecorator(Settings settings) {
    super(settings);
  }

  @DependsUpon
  public List<Metric> dependsUponMetrics() {
    return ImmutableList.<Metric>of(CtcMetrics.CTC_UNCOVERED_STATEMENTS, CtcMetrics.CTC_STATEMENTS_TO_COVER);
  }

  @Override
  protected Metric getGeneratedMetric() {
    return CtcMetrics.CTC_STATEMENT_COVERAGE;
  }

  @Override
  protected Integer countElements(DecoratorContext context) {

    Measure measure = context.getMeasure(CtcMetrics.CTC_STATEMENTS_TO_COVER);
    if (measure == null) {
      return null;
    } else {
      return measure.getIntValue();
    }
  }

  @Override
  protected Integer countUncoveredElements(DecoratorContext context) {
    Measure measure = context.getMeasure(CtcMetrics.CTC_UNCOVERED_STATEMENTS);
    if (measure == null) {
      return null;
    } else {
      return measure.getIntValue();
    }
  }
}
