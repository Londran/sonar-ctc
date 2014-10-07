package org.sonar.plugins.ctc.api.measures;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;
import static org.fest.assertions.Assertions.*;

public class CtcMetricsTest {

  private CtcMetrics testee;

  public static Logger LOG = LoggerFactory.getLogger(CtcMetricsTest.class);

  @Before
  public void setUp() throws Exception {
    testee = new CtcMetrics();
  }

  @Test
  public void testGetMetrics() {
    LOG.info("Starting Metrics test.");
    LOG.debug("Metrics: {}",testee.getMetrics());
    assertThat(testee.getMetrics()).doesNotHaveDuplicates()
    .hasSize(9);
  }

}
