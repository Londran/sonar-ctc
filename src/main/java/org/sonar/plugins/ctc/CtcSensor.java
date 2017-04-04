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
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.plugins.ctc.api.measures.CtcMeasure;
import org.sonar.plugins.ctc.api.measures.CtcReport;
import org.sonar.plugins.ctc.api.measures.CtcTextReport;
import org.sonar.api.batch.fs.InputFile;
import java.util.Map;
import java.util.SortedMap;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;
import org.sonar.api.batch.sensor.coverage.CoverageType;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.measures.CoreMetrics;

@SuppressWarnings("rawtypes")
public class CtcSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(CtcSensor.class);

  private Settings settings;

  public CtcSensor(Settings settings) {
    this.settings = settings;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .onlyOnLanguage("c++")
      .name(getClass().getName());
    LOG.debug("ctc describe");
  }

  @Override
  public void execute(SensorContext context) {
    // LOG.trace("Module: '{}' Module.getParent(): {} getBranch(): '{}' getModules(): '{}' getRoot(): '{}'", module, module.getParent(), module.getBranch(), module.getModules(), module.getRoot());
    java.io.File file = new java.io.File(settings.getString(CtcPlugin.CTC_REPORT_PATH_KEY));
    if (file.canRead()) {
      LOG.debug("Using report file {}", file.toString());
      LOG.debug("Root module imports test metrics: Module Key = '{}'", context.module());
      CtcReport report = new CtcTextReport(file);
      parseReport(report, context);
    } else {
      LOG.error("Could not read report!");
    }

  }

  private void processMeasures(CtcMeasure ctcMeasures, SensorContext sensorContext) {

    SortedMap<Long, Long> hitsByLine;
    SortedMap<Long, Long> conditionsByLine;
    SortedMap<Long, Long> coveredConditionsByLine;

    java.io.File myFile = ctcMeasures.getSource();
    if (myFile != null) {
      String filePath = myFile.toString();
      InputFile coveredFile = sensorContext.fileSystem().inputFile(sensorContext.fileSystem().predicates().hasPath(filePath));
      if (coveredFile != null) {
        
        // core metrics
        
        NewCoverage newCoverage = sensorContext.newCoverage().onFile(coveredFile).ofType(CoverageType.UNIT);
        
        hitsByLine = ctcMeasures.getHitsByLine();
        for (Map.Entry<Long, Long> entry : hitsByLine.entrySet()) {
          newCoverage.lineHits(entry.getKey().intValue(), entry.getValue().intValue());
        }
        
        conditionsByLine = ctcMeasures.getConditionsByLine();
        coveredConditionsByLine = ctcMeasures.getCoveredConditionsByLine();
        for (Map.Entry<Long, Long> entry : conditionsByLine.entrySet()) {
          newCoverage.conditions(entry.getKey().intValue(), entry.getValue().intValue(), coveredConditionsByLine.get(entry.getKey()).intValue());
        }
        newCoverage.save();
        
        if (ctcMeasures.getStatements() > 0) {
          sensorContext.<Integer>newMeasure()
            .forMetric(CtcMetrics.CTC_STATEMENTS_TO_COVER)
            .on(coveredFile)
            .withValue((int)(ctcMeasures.getStatements()))
            .save();

          sensorContext.<Integer>newMeasure()
            .forMetric(CtcMetrics.CTC_UNCOVERED_STATEMENTS)
            .on(coveredFile)
            .withValue((int)(ctcMeasures.getStatements() - ctcMeasures.getCoveredStatements()))
            .save();
        }
        
        
        // CTC++ metrics
        
        if (ctcMeasures.getStatements() > 0) {
          sensorContext.<Integer>newMeasure()
            .forMetric(CtcMetrics.CTC_CONDITIONS_TO_COVER)
            .on(coveredFile)
            .withValue((int)(ctcMeasures.getConditions()))
            .save();

          sensorContext.<Integer>newMeasure()
            .forMetric(CtcMetrics.CTC_UNCOVERED_CONDITIONS)
            .on(coveredFile)
            .withValue((int)(ctcMeasures.getConditions() - ctcMeasures.getCoveredConditions()))
            .save();
        }
        
      } else {
        LOG.error("File not mapped to resource! ({})", filePath);
      }
    } else {
      
      // CTC++ project measures
      sensorContext.<Integer>newMeasure()
        .forMetric(CtcMetrics.CTC_MEASURE_POINTS)
        .on(sensorContext.module())
        .withValue((int)ctcMeasures.getMeasurePoints())
        .save();
    }
  }


  private void parseReport(CtcReport report, SensorContext sensorContext) {
    for (CtcMeasure measure : report) {
      processMeasures(measure, sensorContext);
    }
  }
}
