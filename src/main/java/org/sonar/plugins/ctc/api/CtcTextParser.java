package org.sonar.plugins.ctc.api;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.collections.map.HashedMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.AbstractIterator;

public class CtcTextParser extends AbstractIterator<CtcFileMeasure> implements CtcParser {

  private final Scanner scanner;
  private final Map<CtcReportDetailKey, String> projectDetails;

  private final static Logger log = LoggerFactory.getLogger(CtcTextParser.class);
  private final static Pattern BLANK = Pattern.compile("^\\s+", Pattern.MULTILINE);

  public CtcTextParser(File report) throws FileNotFoundException {
    log.debug("Instantiating...");
    Readable readable = null;
    try {
      log.debug("Trying to open file...");
      readable = new FileReader(report);
    } catch (FileNotFoundException e) {
      log.error("File not found!",e);
      throw e;
    }
    if (readable != null) {
      scanner = new Scanner(readable);
      log.debug("Scanner is ready...");
    } else {
      scanner = null;
    }

    projectDetails = new EnumMap<CtcReportDetailKey, String>(CtcReportDetailKey.class);
    for (CtcReportDetailKey key : CtcReportDetailKey.values()) {
      log.debug("Checking for key {} with pattern /{}/",key.toString(),key.getPattern().pattern());
      if (scanner.findWithinHorizon(key.getPattern(), 0) != null) {
        projectDetails.put(key, scanner.nextLine());
        log.debug("Added {} : {}",key,projectDetails.get(key));
      }
      if (key == CtcReportDetailKey.MON_DAT || key == CtcReportDetailKey.MON_SYM) {
        while(scanner.findInLine(BLANK) != null) {
          projectDetails.put(key, projectDetails.get(key)+","+scanner.nextLine());
        }
      }
    }

    if (projectDetails.size() < CtcReportDetailKey.values().length) {
      log.error("INVALID REPORT!");
      throw new RuntimeException("Invalid Report");
    }

  }

  @Override
  protected CtcFileMeasure computeNext() {

    if (scanner == null) {
      return endOfData();
    }

    return null;
  }

  @Override
  public Map<CtcReportDetailKey, String> getReportDetails() {

    return projectDetails;
  }

}
