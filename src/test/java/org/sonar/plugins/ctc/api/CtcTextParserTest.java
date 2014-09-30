package org.sonar.plugins.ctc.api;

import org.jfree.util.Log;

import org.fest.util.Arrays;
import com.google.common.collect.Sets;
import org.sonar.plugins.ctc.CtcDecorator;
import org.fest.assertions.MapAssert;
import org.junit.Test;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.fest.assertions.Fail.*;
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

}
