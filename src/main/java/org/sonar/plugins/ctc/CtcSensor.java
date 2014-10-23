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

import org.sonar.api.measures.Metric;

import java.util.Map;
import java.util.Map.Entry;
import org.sonar.api.resources.Resource;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.File;
import org.sonar.plugins.ctc.api.measures.CtcMeasure;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.sonar.plugins.ctc.api.measures.CtcTextReport;
import org.sonar.plugins.ctc.api.measures.CtcReport;
import org.sonar.api.config.Settings;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.batch.Sensor;

@SuppressWarnings("rawtypes")
public class CtcSensor implements Sensor {

  private Logger log = LoggerFactory.getLogger(CtcSensor.class);

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
    java.io.File file = new java.io.File(settings.getString(CtcPlugin.CTC_REPORT_PATH_KEY));
    if (file.canRead()) {
      log.debug("Using report file {}",file.toString());
      CtcReport report = new CtcTextReport(file);
      parseReport(report,module,context);
    } else {
      log.error("Could not read report!");
    }

  }

  private void parseReport(CtcReport report, Project module, SensorContext context) {
    for (CtcMeasure measure : report) {

      Resource resource = module;
      if (measure.SOURCE != null) {
        resource = File.fromIOFile(measure.SOURCE, module);
      }

      log.debug("Saving measures to: {}",resource.toString());
      for (Measure rawMeasure : measure.MEASURES) {
        context.saveMeasure(resource, rawMeasure);
      }


    }
  }

  @SuppressWarnings("unused")
  private void saveToCore(Resource resource, SensorContext context, Map<Metric, Metric> coreMap) {
    for (Resource child : context.getChildren(resource)) {
      saveToCore(resource,context,coreMap);
    }
    for (Entry<Metric, Metric> entry : coreMap.entrySet()) {
      @SuppressWarnings("unchecked")
      Measure measure = context.getMeasure(resource, entry.getKey());
      if (measure != null) {
        switch (entry.getKey().getType()) {
          case INT:
            context.saveMeasure(resource, entry.getValue(), measure.getValue());
            break;
          case DATA:
            Measure m2 = new Measure(entry.getValue(), measure.getData());
            context.saveMeasure(resource,m2);
            break;
          case FLOAT:
          case PERCENT:
            context.saveMeasure(resource, entry.getValue(), measure.getValue());
            break;
          default:
            log.debug("Unknown Datatype! {}",measure);
            break;

        }
      }
    }
  }
}
