appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%c{0} [%-5level] - %msg%n"
  }
}

root(ERROR,["CONSOLE"]);

logger("org.sonar.plugins.ctc.api.parser.CtcTextParser",ERROR)
logger("org.sonar.plugins.ctc.api.parser.CtcTextParserTest",INFO)
