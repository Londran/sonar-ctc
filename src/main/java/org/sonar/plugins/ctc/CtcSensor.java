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
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.ctc.api.measures.CtcMeasure;
import org.sonar.plugins.ctc.api.measures.CtcReport;
import org.sonar.plugins.ctc.api.measures.CtcTextReport;

@SuppressWarnings("rawtypes")
public class CtcSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(CtcSensor.class);

  private Settings settings;

  public CtcSensor(Settings settings) {
    this.settings = settings;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {

    return !settings.getBoolean(CtcPlugin.CTC_DISABLE_SENSOR_KEY);
  }

  @Override
  public void analyse(Project module, SensorContext context) {
    LOG.trace("Module: '{}' Module.getParent(): {} getBranch(): '{}' getModules(): '{}' getRoot(): '{}'", module, module.getParent(), module.getBranch(), module.getModules(),
      module.getRoot());
    java.io.File file = new java.io.File(settings.getString(CtcPlugin.CTC_REPORT_PATH_KEY));
    if (file.canRead()) {
      LOG.debug("Using report file {}", file.toString());
      CtcReport report = new CtcTextReport(file);
      parseReport(report, module, context);
    } else {
      LOG.error("Could not read report!");
    }

  }
  
  private Resource getValidResource(Project module, SensorContext context, CtcMeasure measure) {
    
    Resource resource;

    java.io.File file = measure.getSOURCE();
    
    if (file == null) {
      resource = module;
    } else if (!file.exists()) {
      resource = null;
    } else {
      LOG.debug("FileName: {}", measure.getSOURCE());
      resource = File.fromIOFile(measure.getSOURCE(), module);
      resource = context.getResource(resource);
      if (resource == null) {
        LOG.error("File not mapped to resource!");
      }
    }
    
    return resource;
  }
  
  private void processMeasures(Resource resource, CtcMeasure ctcMeasures, SensorContext context) {
    LOG.debug("Saving measures to: {}", resource);
    for (Measure rawMeasure : ctcMeasures.getMEASURES()) {
      LOG.debug("Resource: {} Measure: {}", resource, rawMeasure);
      context.saveMeasure(resource, rawMeasure);

    }
  }

  private void parseReport(CtcReport report, Project module, SensorContext context) {
    for (CtcMeasure measure : report) {
      Resource resource = getValidResource(module, context, measure);
      
      if (resource != null) {
        processMeasures(resource, measure, context);
      }
    }
  }
}
