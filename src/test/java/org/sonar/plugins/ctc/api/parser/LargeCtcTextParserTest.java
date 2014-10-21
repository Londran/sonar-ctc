package org.sonar.plugins.ctc.api.parser;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LargeCtcTextParserTest extends CtcTextParserTest {


  @Override
  public File getReport() {
    return new File(CtcTextParserTest.class.getResource("report_big.txt").getFile());
  }


}
