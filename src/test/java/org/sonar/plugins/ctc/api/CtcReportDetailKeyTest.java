package org.sonar.plugins.ctc.api;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

public class CtcReportDetailKeyTest {

  private static Logger LOG = LoggerFactory.getLogger(CtcReportDetailKeyTest.class);

  @Test
  public void testGetPattern() {
    for (CtcReportDetailKey key : CtcReportDetailKey.values()) {
      LOG.debug("Key {} Pattern {}", key, key.getPattern());
    }
  }

}
