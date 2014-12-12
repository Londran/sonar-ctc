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
package org.sonar.plugins.ctc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.ctc.api.measures.CtcMetrics;

public class CtcHtmlDecorator implements Decorator {

  private static final int FILE_EXT_GROUP = 2;

  private static final String[] INDIZES = {"CTCHTML/indexF.html", "CTCHTML/indexC.html"};

  private static final Pattern FILE_PATTERN = Pattern.compile("\\./?([^\\.]*(\\..+)?)");

  private Map<String, String> hrefMap;
  private static final Logger LOG = LoggerFactory
    .getLogger(CtcHtmlDecorator.class);

  public CtcHtmlDecorator() {
    hrefMap = new HashMap<String, String>();

    try {
      for (String s : INDIZES) {
        parseSource(new File(s));
      }

      if (LOG.isDebugEnabled()) {
        LOG.trace("Map");
        for (Entry<String, String> entry : hrefMap.entrySet()) {
          LOG.debug("Map - Entry: '{}':'{}'", entry.getKey(), entry.getValue());
        }
      }
    } catch (IOException e) {
      hrefMap = null;
      LOG.debug("HTML-Report not found", e);
    }

  }

  private void parseSource(File file) throws IOException {
    if (file.exists()) {
      Matcher matcher = FILE_PATTERN.matcher("");
      Source source = new Source(file);
      for (Element element : source.getAllElements("a")) {
        matcher.reset(element.getTextExtractor().toString());
        if (matcher.matches()) {
          addPath(matcher, element);
        }
      }
    } else {
      LOG.debug("{} not found", file.getPath());
    }
  }
  
  private void addPath(Matcher matcher, Element element) {
    String path;
    if (matcher.group(FILE_EXT_GROUP) != null) {
      path = matcher.group(1);
    } else {
      path = matcher.group(1) + "/";
    }
    LOG.trace("Adding '{}' --> '{}'", path, element.getAttributeValue("href"));
    hrefMap.put(path, element.getAttributeValue("href"));
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return hrefMap != null;
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {

    String href = hrefMap.get(resource.getPath());
    if (href == null) {
      href = "index.html";
    }
    LOG.trace("Mapping orig report {} --> {}", resource, href);
    @SuppressWarnings("rawtypes")
    Measure path = new Measure(CtcMetrics.CTC_ORIG_REPORT_NAME);
    path.setData(href);
    context.saveMeasure(path);

  }

}
