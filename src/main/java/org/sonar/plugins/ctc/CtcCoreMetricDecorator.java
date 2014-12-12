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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;

import com.google.common.collect.ImmutableList;

public class CtcCoreMetricDecorator implements Decorator {

  private static final Logger LOG = LoggerFactory
    .getLogger(CtcCoreMetricDecorator.class);

  private final Settings settings;
  @SuppressWarnings("rawtypes")
  private static final Metric[][] CONV = {
    {CtcMetrics.CTC_STATEMENTS_TO_COVER, CoreMetrics.STATEMENTS},
    {CtcMetrics.CTC_CONDITIONS_TO_COVER, CoreMetrics.CONDITIONS_TO_COVER},
    {CtcMetrics.CTC_CONDITIONS_BY_LINE, CoreMetrics.CONDITIONS_BY_LINE},
    {CtcMetrics.CTC_UNCOVERED_CONDITIONS, CoreMetrics.UNCOVERED_CONDITIONS},
    {CtcMetrics.CTC_COVERED_CONDITIONS_BY_LINE, CoreMetrics.COVERED_CONDITIONS_BY_LINE}
  };

  public CtcCoreMetricDecorator(Settings settings) {
    this.settings = settings;
  }

  @SuppressWarnings("rawtypes")
  @DependsUpon
  public List<Metric> dependsUponMetrics() {
    return ImmutableList.<Metric>of(CtcMetrics.CTC_STATEMENTS_TO_COVER,
      CtcMetrics.CTC_CONDITIONS_TO_COVER,
      CtcMetrics.CTC_CONDITIONS_BY_LINE,
      CtcMetrics.CTC_UNCOVERED_CONDITIONS);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return settings.getBoolean(CtcPlugin.CTC_CORE_METRIC_KEY);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void decorate(Resource resource, DecoratorContext context) {
    if (resource.getScope().equals(Scopes.FILE)) {
      Map lineHits = new HashMap<Integer, Integer>();
      context.saveMeasure(CoreMetrics.LINES_TO_COVER, context.getMeasure(CoreMetrics.LINES).getValue());
      context.saveMeasure(CoreMetrics.UNCOVERED_LINES, 0.0);

      for (int i = 1; i <= context.getMeasure(CoreMetrics.LINES).getIntValue(); i++) {
        lineHits.put(i, 1);
      }
      context.saveMeasure(new Measure(CoreMetrics.COVERAGE_LINE_HITS_DATA).setData(KeyValueFormat.format(lineHits)).setPersistenceMode(PersistenceMode.DATABASE));
    }

    for (Metric[] entry : CONV) {

      applyAndSaveMeasure(entry, context, resource);

    }
  }
  
  @SuppressWarnings({"rawtypes"})
  private void applyAndSaveMeasure(Metric[] entry, DecoratorContext context, Resource resource) {
    Measure ctc = context.getMeasure(entry[0]);
    Measure sonar = context.getMeasure(entry[1]);
    switch (entry[0].getType()) {
      case DATA:
        if (ctc.getData() != null) {
          sonar.setData(ctc.getData());
        }
        break;
      case INT:
        if (ctc.getIntValue() != null) {
          sonar.setIntValue(ctc.getIntValue());
        }
        break;
      default:
        LOG.error("Illegal Type for Core Conversion!");
        break;
    }
    LOG.trace("Saving Metric: {}", sonar);
    context.saveMeasure(sonar);
    if (sonar == null) {
      sonar = new Measure(entry[1]);
    }

    LOG.trace("{} -> {}", entry[0].getName(), entry[1].getName());
    if (ctc == null) {
      LOG.trace("No Measures found for {} {}", resource, entry[0]);
    }
  }

}
