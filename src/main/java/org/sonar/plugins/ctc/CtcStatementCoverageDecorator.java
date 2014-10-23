package org.sonar.plugins.ctc;

import org.sonar.api.measures.Measure;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Metric;

@SuppressWarnings("rawtypes")
public class CtcStatementCoverageDecorator extends CtcCoverageDecorator {

  public CtcStatementCoverageDecorator(Settings settings) {
    super(settings);
  }

  @Override
  protected Metric getGeneratedMetric() {
    return CtcMetrics.CTC_STATEMENT_COVERAGE;
  }

  @Override
  protected Integer countElements(DecoratorContext context) {
    Measure measure = context.getMeasure(CtcMetrics.CTC_STATEMENTS_TO_COVER);
    if (measure == null) return null;
    else return measure.getIntValue();
  }

  @Override
  protected Integer countUncoveredElements(DecoratorContext context) {
    Measure measure = context.getMeasure(CtcMetrics.CTC_UNCOVERED_STATEMENTS);
    if (measure == null) return null;
    else return measure.getIntValue();
  }
}
