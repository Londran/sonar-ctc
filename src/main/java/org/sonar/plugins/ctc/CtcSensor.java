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
import org.sonar.api.measures.Measure;
import org.sonar.plugins.ctc.api.measures.CtcMeasure;
import org.sonar.plugins.ctc.api.measures.CtcReport;
import org.sonar.plugins.ctc.api.measures.CtcTextReport;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.Metric;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;

@SuppressWarnings("rawtypes")
public class CtcSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(CtcSensor.class);

  private Settings settings;

  private static final Map<String, Metric> MAPPING_CTC_CORE_METRICS = createMap();

  private static Map<String, Metric> createMap() {
    Map<String, Metric> result = new HashMap<String, Metric>();

    result.put(CtcMetrics.CTC_LINES_TO_COVER.key(), CoreMetrics.LINES_TO_COVER);
    result.put(CtcMetrics.CTC_UNCOVERED_LINES.key(), CoreMetrics.UNCOVERED_LINES);
    result.put(CtcMetrics.CTC_LINE_COVERAGE.key(), CoreMetrics.LINE_COVERAGE);

    result.put(CtcMetrics.CTC_CONDITIONS_TO_COVER.key(), CoreMetrics.CONDITIONS_TO_COVER);
    result.put(CtcMetrics.CTC_UNCOVERED_CONDITIONS.key(), CoreMetrics.UNCOVERED_CONDITIONS);

    result.put(CtcMetrics.CTC_CONDITIONS_BY_LINE.key(), CoreMetrics.CONDITIONS_BY_LINE);
    result.put(CtcMetrics.CTC_COVERED_CONDITIONS_BY_LINE.key(), CoreMetrics.COVERED_CONDITIONS_BY_LINE);

    return Collections.unmodifiableMap(result);
  }

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

    Metric ctcMetric;
    Metric coreMetric;
    boolean setCoreMetrics;

    setCoreMetrics = this.settings.getBoolean(CtcPlugin.CTC_CORE_METRIC_KEY);

    java.io.File myFile = ctcMeasures.getSOURCE();
    if (myFile != null) {
      String componentPath = myFile.toString();
      org.sonar.api.batch.fs.FilePredicate fp = sensorContext.fileSystem().predicates().hasPath(componentPath);
      InputFile coveredFile = sensorContext.fileSystem().inputFile(fp);
      if (coveredFile != null) {
        LOG.debug("File mapped to {}", componentPath);
        for (Measure rawMeasure : ctcMeasures.getMEASURES()) {
          ctcMetric = rawMeasure.getMetric();
          coreMetric = setCoreMetrics == true ? MAPPING_CTC_CORE_METRICS.get(ctcMetric.getKey()) : null;
          LOG.debug("ctcMetric {} {}", ctcMetric.key(), rawMeasure.toString());
          switch (ctcMetric.getType()) {
            case DATA:
              sensorContext.<String>newMeasure()
                .forMetric(ctcMetric)
                .on(coveredFile)
                .withValue(rawMeasure.getData())
                .save();
              if (null != coreMetric) {
                sensorContext.<String>newMeasure()
                  .forMetric(coreMetric)
                  .on(coveredFile)
                  .withValue(rawMeasure.getData())
                  .save();
              }
              break;
            case FLOAT:
              sensorContext.<Float>newMeasure()
                .forMetric(ctcMetric)
                .on(coveredFile)
                .withValue(rawMeasure.getValue())
                .save();
              if (null != coreMetric) {
                sensorContext.<Float>newMeasure()
                  .forMetric(coreMetric)
                  .on(coveredFile)
                  .withValue(rawMeasure.getValue())
                  .save();
              }
              break;
            case INT:
              sensorContext.<Integer>newMeasure()
                .forMetric(ctcMetric)
                .on(coveredFile)
                .withValue(rawMeasure.getIntValue())
                .save();
              if (null != coreMetric) {
                sensorContext.<Integer>newMeasure()
                  .forMetric(coreMetric)
                  .on(coveredFile)
                  .withValue(rawMeasure.getIntValue())
                  .save();
              }
              break;
            case MILLISEC:
              break;
            case PERCENT:
              sensorContext.<Double>newMeasure()
                .forMetric(ctcMetric)
                .on(coveredFile)
                .withValue(rawMeasure.toString())
                .save();
              if (null != coreMetric) {
                sensorContext.<Double>newMeasure()
                  .forMetric(coreMetric)
                  .on(coveredFile)
                  .withValue(rawMeasure.toString())
                  .save();
              }
              break;
            default:
              LOG.error("Illegal Measure Type!");
              break;
          }
        }
      } else {
        LOG.error("File not mapped to resource! ({})", componentPath);
      }
    }
  }

  private void parseReport(CtcReport report, SensorContext sensorContext) {
    /*
      FileSystem fileSystem = sensorContext.fileSystem();
      FilePredicate mainFilePredicate = fileSystem.predicates().and(
            fileSystem.predicates().hasType(InputFile.Type.MAIN),
            fileSystem.predicates().hasLanguage("c++"));

      for (InputFile inputFile : sensorContext.fileSystem().inputFiles(mainFilePredicate)){
            LOG.debug("inputFile {}", inputFile.toString());
      }
     */

    for (CtcMeasure measure : report) {
      processMeasures(measure, sensorContext);
    }
  }
}
