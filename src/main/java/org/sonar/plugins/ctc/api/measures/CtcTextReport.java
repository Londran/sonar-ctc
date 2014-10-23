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
package org.sonar.plugins.ctc.api.measures;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.sonar.plugins.ctc.api.parser.CtcResult;
import org.sonar.plugins.ctc.api.parser.CtcTextParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class CtcTextReport implements CtcReport {

  private final File file;
  private final Map<CtcResult,String> projectDetails;

  private final static Logger log = LoggerFactory.getLogger(CtcTextReport.class);

  public CtcTextReport(File file) {
    this.file = file;
    this.projectDetails = new EnumMap<CtcResult, String>(CtcResult.class);
  }

  @Override
  public Iterator<CtcMeasure> iterator() {

    try {
      return new CtcTextParser(file);
    } catch (FileNotFoundException e) {
      log.error("Report not found.",e);
      return new EmptyIterator();
    }
  }

  @Override
  public Map<CtcResult, String> getReportDetails() {
    return projectDetails;
  }

  private static class EmptyIterator implements Iterator<CtcMeasure> {
    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public CtcMeasure next() {
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
