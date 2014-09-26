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

import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import org.sonar.api.batch.Sensor;

public class CtcSensor implements Sensor {

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void analyse(Project module, SensorContext context) {
    // TODO Auto-generated method stub

  }

}
