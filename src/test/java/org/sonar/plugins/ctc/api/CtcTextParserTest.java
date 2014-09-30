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
package org.sonar.plugins.ctc.api;

import org.junit.Test;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.fest.assertions.Assertions.*;
import static org.fest.assertions.MapAssert.*;
import static org.sonar.plugins.ctc.api.CtcReportDetailKey.*;

public class CtcTextParserTest {

  private CtcTextParser testee;

  private static File report_small;
  private static File report_big;

  private final static Logger LOG = LoggerFactory.getLogger(CtcTextParserTest.class);

  @BeforeClass
  public static void getFiles() {
    LOG.info("Setting up sample files...");

    report_small = new File(CtcTextParserTest.class.getResource("report_small.txt").getFile());
    LOG.debug("File {} is readable: {}",report_small.getAbsolutePath(),report_small.canRead());
    report_big = new File(CtcTextParserTest.class.getResource("report_big.txt").getFile());
    LOG.debug("File {} is readable: {}",report_big.getAbsolutePath(),report_big.canRead());
  }

  @Test
  public void testGetProjectDetails() throws FileNotFoundException {
    LOG.info("Starting small parsing");
    testee = new CtcTextParser(report_small);
    LOG.info("Ended Small parsing");

    Map<CtcReportDetailKey,String> result = testee.getReportDetails();

    assertThat(result).includes(
      entry(MON_SYM,"MON.sym (Mon Aug 11 12:04:57 2014)"),
      entry(MON_DAT,"MON.dat (Mon Aug 11 12:05:14 2014)"),
      entry(LIST_DATE, "Mon Aug 11 12:06:01 2014"),
      entry(COV_VIEW, "As instrumented"),
      entry(SRC_FILES, "3"),
      entry(SRC_LINES, "82"),
      entry(MEASUREMENT_POINTS, "31"),
      entry(TER_CONDITION,"82 % (27/33) multicondition"),
      entry(TER_STATEMENT, "89 % (25/28) statement")
      );

    LOG.info("Starting big parsing");
    testee = new CtcTextParser(report_big);
    LOG.info("Ended big parsing");

    result = testee.getReportDetails();

    assertThat(result).includes(
      entry(MON_SYM,"MON.sym (Wed Sep 10 07:40:30 2014)"),
      entry(MON_DAT,"MON.dat (Wed Sep 10 07:29:06 2014)"),
      entry(LIST_DATE, "Wed Sep 10 09:40:14 2014"),
      entry(COV_VIEW, "As instrumented"),
      entry(SRC_FILES, "312"),
      entry(SRC_LINES, "165040"),
      entry(MEASUREMENT_POINTS, "69547"),
      entry(TER_CONDITION,"72 % (55100/76542) multicondition"),
      entry(TER_STATEMENT, "80 % (82371/103297) statement")
      );

  }

  @Test
  public void testIterator() throws FileNotFoundException {
    testee = new CtcTextParser(report_big);
    int i = 0;
    while(testee.hasNext()) {
      testee.next();
      i++;
    }

    assertThat(testee.hasNext()).isFalse();
  }

  @Test
  public void testIteratorProfiler() throws FileNotFoundException {
    Runtime rt = Runtime.getRuntime();
    for (int i = 0; i < 10; i++) {
      testIterator();

      LOG.debug("Memory usage after parsing: {}",humanReadableByteCount(rt.totalMemory() - rt.freeMemory(),true));
    }
  }

  private static String humanReadableByteCount(long bytes, boolean si) {
    int unit = si ? 1000 : 1024;
    if (bytes < unit) return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
}

}
