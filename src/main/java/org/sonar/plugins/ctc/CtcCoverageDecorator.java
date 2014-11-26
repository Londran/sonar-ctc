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

import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.batch.Decorator;

public abstract class CtcCoverageDecorator implements Decorator, CoverageExtension {

  protected final Settings settings;
  private static Logger log = LoggerFactory.getLogger(CtcCoverageDecorator.class);

  public CtcCoverageDecorator(Settings settings) {
    this.settings =  settings;
  }


  @DependsUpon
  public Collection<Metric> generatedMetrics() {
	    return Arrays.asList(getGeneratedMetric());
	  }
  
  @Override
  public boolean shouldExecuteOnProject(Project project) {

    return !settings.getBoolean(CtcPlugin.CTC_DISABLE_DECORATOR_KEY);
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {
	log.debug("Decorating resource: {}",resource);
    computeMeasure(context);
  }

  private void computeMeasure(DecoratorContext context) {
    if (context.getMeasure(getGeneratedMetric()) == null) {
      Integer elements = countElements(context);
      if (elements != null && elements > 0L) {
        Integer uncoveredElements = countUncoveredElements(context);
        context.saveMeasure(getGeneratedMetric(), calculateCoverage(uncoveredElements, elements));
      }
    }
  }

  private double calculateCoverage(final long uncoveredLines, final long lines) {
    return 100.0 - ((100.0*uncoveredLines) / lines);
  }

  @SuppressWarnings("rawtypes")
  protected abstract Metric getGeneratedMetric();

  protected abstract Integer countElements(DecoratorContext context);

  protected abstract Integer countUncoveredElements(DecoratorContext context);

}
