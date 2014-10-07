package org.sonar.plugins.ctc.api.parser;

import java.io.File;

public class SmallCtcTextParserTest extends CtcTextParserTest {

  @Override
  public File getReport() {
    return new File(CtcTextParserTest.class.getResource("report_small.txt").getFile());
  }

}
