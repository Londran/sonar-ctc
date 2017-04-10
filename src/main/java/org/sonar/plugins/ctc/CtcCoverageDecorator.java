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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Metric;

public abstract class CtcCoverageDecorator implements MeasureComputer {

  private static final double WHOLE = 100.0;
  protected final Settings settings;
  private static final Logger LOG = LoggerFactory.getLogger(CtcCoverageDecorator.class);

  public CtcCoverageDecorator(Settings settings) {
    this.settings = settings;
  }

  @Override
  public void compute(MeasureComputerContext context) {
    LOG.trace("Compunting resource: {}", context);
    computeMeasure(context);
  }

  private void computeMeasure(MeasureComputerContext context) {
    if (context.getMeasure(getGeneratedMetric().key()) == null) {
      Integer elements = countElements(context);
      if (elements != null && elements > 0L) {
        Integer uncoveredElements = countUncoveredElements(context);
        context.addMeasure(getGeneratedMetric().key(), calculateCoverage(uncoveredElements, elements));
      }
    }
  }

  private double calculateCoverage(final long uncoveredLines, final long lines) {
    return WHOLE - ((WHOLE * uncoveredLines) / lines);
  }

  @SuppressWarnings("rawtypes")
  protected abstract Metric getGeneratedMetric();

  protected abstract Integer countElements(MeasureComputerContext context);

  protected abstract Integer countUncoveredElements(MeasureComputerContext context);

}
