/*
 * Testwell CTC++ Plugin
 * Copyright (C) 2014 Verifysoft Technology GmbH
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.ctc.widgets;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.WidgetProperties;
import org.sonar.api.web.WidgetProperty;
import org.sonar.api.web.WidgetPropertyType;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;

@WidgetProperties({
  @WidgetProperty(key = "chartTitle", type = WidgetPropertyType.STRING),
  @WidgetProperty(key = "sizeMetric", type = WidgetPropertyType.METRIC, defaultValue = CtcMetrics.CTC_STATEMENTS_TO_COVER_KEY, options = {"domain:Tests \\(Testwell CTC\\+\\+\\)"}),
  @WidgetProperty(key = "colorMetric", type = WidgetPropertyType.METRIC, defaultValue = CtcMetrics.CTC_CONDITION_COVERAGE_KEY,
    options = {"domain:Tests \\(Testwell CTC\\+\\+\\)", "type:PERCENT"}),
  @WidgetProperty(key = "heightInPercents", type = WidgetPropertyType.INTEGER, optional = true, defaultValue = "55"),
  @WidgetProperty(key = "maxItems", type = WidgetPropertyType.INTEGER, defaultValue = "100"),
  @WidgetProperty(key = "reportBaseUrl", type = WidgetPropertyType.STRING)
})
public class CtcTreemapWidget extends AbstractRubyTemplate implements
  RubyRailsWidget {

  public CtcTreemapWidget() {

  }

  @Override
  public String getId() {
    return "ctc_treemap_widget";
  }

  @Override
  public String getTitle() {
    return "Testwell CTC++ Treemap of Components";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/ctc/widgets/ctc_treemap.html.erb";
  }

}
