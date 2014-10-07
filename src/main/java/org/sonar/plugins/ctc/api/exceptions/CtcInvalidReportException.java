package org.sonar.plugins.ctc.api.exceptions;

public class CtcInvalidReportException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public CtcInvalidReportException() {
    this("");
  }

  public CtcInvalidReportException(String msg) {
    super(msg);
  }

}
