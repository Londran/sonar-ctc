package org.sonar.plugins.ctc;

import org.sonar.api.measures.Measure;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Metric;

@SuppressWarnings("rawtypes")
public class CtcConditionCoverageDecorator extends CtcCoverageDecorator {

  public CtcConditionCoverageDecorator(Settings settings) {
    super(settings);
  }


  @Override
  protected Metric getGeneratedMetric() {
    return CtcMetrics.CTC_CONDITION_COVERAGE;
  }

  @Override
  protected Integer countElements(DecoratorContext context) {
    Measure measure = context.getMeasure(CtcMetrics.CTC_CONDITIONS_TO_COVER);
    if (measure == null) return null;
    else return measure.getIntValue();
  }

  @Override
  protected Integer countUncoveredElements(DecoratorContext context) {
    Measure measure = context.getMeasure(CtcMetrics.CTC_UNCOVERED_CONDITIONS);
    if (measure == null) return null;
    else return measure.getIntValue();
  }

}
