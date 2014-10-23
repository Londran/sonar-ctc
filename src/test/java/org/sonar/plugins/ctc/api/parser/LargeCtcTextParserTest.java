package org.sonar.plugins.ctc.api.parser;

import java.io.File;

public class LargeCtcTextParserTest extends CtcTextParserTest {


  @Override
  public File getReport() {
    return new File(CtcTextParserTest.class.getResource("report_big.txt").getFile());
  }


}
