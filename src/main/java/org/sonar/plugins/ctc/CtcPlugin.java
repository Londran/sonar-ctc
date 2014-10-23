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

import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.SonarPlugin;

public class CtcPlugin extends SonarPlugin {

  public static final String CTC_REPORT_PATH_KEY = "sonar.ctc.report.path";
  public static final String CTC_REPORT_TYPE_KEY = "sonar.ctc.report.type";
  public static final String CTC_DISABLE_SENSOR_KEY = "sonar.ctc.sensor.disabled";
  public static final String CTC_DISABLE_DECORATOR_KEY = "sonar.ctc.decorator.disabled";
  public static final String CTC_CORE_METRIC_KEY = "sonar.ctc.sensor.core_metric";

  @SuppressWarnings("rawtypes")
  public static final List EXTENSIONS = Arrays.asList(

    CtcMetrics.class,
    CtcSensor.class, CtcCoverageDecorator.class,
    PropertyDefinition.builder(CTC_REPORT_PATH_KEY)
      .hidden().defaultValue("report.txt").type(PropertyType.STRING).name("CTC_REPORT_PATH").build(),
    PropertyDefinition.builder(CTC_REPORT_TYPE_KEY)
      .hidden().type(PropertyType.SINGLE_SELECT_LIST).options("TXT").name("CTC_REPORT_TYPE").build(),
    PropertyDefinition.builder(CTC_DISABLE_SENSOR_KEY)
      .hidden().type(PropertyType.BOOLEAN).defaultValue("false").name("CTC_DISABLE_SENSOR").build(),
    PropertyDefinition.builder(CTC_DISABLE_DECORATOR_KEY)
      .hidden().type(PropertyType.BOOLEAN).defaultValue("false").name("CTC_DISABLE_DECORATOR").build(),
    PropertyDefinition.builder(CTC_CORE_METRIC_KEY)
      .defaultValue("false").name("Core Metric").description("Should Testwell CTC++ replace the core metrics?").category("2").type(PropertyType.BOOLEAN).build()
    );

  @SuppressWarnings("rawtypes")
  @Override
  public List getExtensions() {
    return EXTENSIONS;
  }



}
