appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%c{0} [%-5level] - %msg%n"
  }
}

root(INFO,["CONSOLE"]);

logger("org.sonar.plugins.ctc.api.CtcTextParserTest",TRACE)
