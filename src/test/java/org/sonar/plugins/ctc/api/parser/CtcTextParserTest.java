package org.sonar.plugins.ctc.api.parser;

import org.sonar.plugins.ctc.api.parser.CtcTextParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.fest.assertions.Fail.fail;

public abstract class CtcTextParserTest {

  private CtcTextParser testee;

  @Before
  public void setUp() throws Exception {
    testee = new CtcTextParser(getReport());
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testHasNext() {
    fail("Not yet implemented");
  }

  @Test
  public void testNext() {
    while (testee.hasNext()) {
      testee.next();
    }
  }

  @Test
  public void testRemove() {
    try {
      testee.remove();
      fail("Remove shall not be supported");
    } catch (UnsupportedOperationException e) {

    }
  }

  @Test
  public void testGetReportDetails() {
    fail("Not yet implemented");
  }

  public abstract File getReport();

}
