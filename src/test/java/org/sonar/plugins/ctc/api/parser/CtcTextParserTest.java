package org.sonar.plugins.ctc.api.parser;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.NoSuchElementException;

import static org.fest.assertions.Fail.fail;

public abstract class CtcTextParserTest {

  private CtcTextParser testee;
  private static Logger log = LoggerFactory.getLogger(CtcTextParserTest.class);


  @Before
  public void setUp() throws Exception {
    testee = new CtcTextParser(getReport());
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testHasNext() {
    try {
      while (testee.next() != null);
      fail("No Such Element not thrown!");
    } catch (NoSuchElementException e) {
      Assertions.assertThat(testee.hasNext()).isEqualTo(false);
    }

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
    while (testee.hasNext()) {
      log.info("FOUND ELEMENT: {}",testee.next());
    }
  }

  public abstract File getReport();

}
