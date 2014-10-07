appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%c{0} [%-5level] - %msg%n"
  }
}

root(DEBUG,["CONSOLE"]);

logger("org.sonar.plugins.ctc.api.parser.CtcTextParser",TRACE)
