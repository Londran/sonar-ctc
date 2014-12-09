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
	
	private static final String[] indizes = {"CTCHTML/indexF.html", "CTCHTML/indexC.html"};
	
	private final static Pattern FILE_PATTERN = Pattern.compile("\\./?([^\\.]*(\\..+)?)");

	private Map<String, String> hrefMap;
	public static final Logger log = LoggerFactory
			.getLogger(CtcHtmlDecorator.class);

	public CtcHtmlDecorator() {
		log.info("CTCHTML_DECORATOR STARTED");
		hrefMap = new HashMap<String, String>();
		try {
			for (String s : indizes) {
				parseSource(new File(s));
			}

			if (log.isDebugEnabled()) {
				log.trace("Map");
				for (Entry<String, String> entry : hrefMap.entrySet()) {
					log.debug("Map - Entry: '{}':'{}'",entry.getKey(),entry.getValue());
				}
			}
		} catch (IOException e) {
			hrefMap = null;
			log.debug("HTML-Report not found");
		}
		

	}
	
	private void parseSource (File file) throws IOException {
		Matcher matcher = FILE_PATTERN.matcher("");
		Source source = new Source(file);
		for (Element element : source.getAllElements("a")) {
			matcher.reset(element.getTextExtractor().toString());
			if (matcher.matches()) {
				String path;
				if (matcher.group(2) != null) {
					path = matcher.group(1);
				} else {
					path = matcher.group(1) + "/";
				}
				log.trace("Adding '{}' --> '{}'",path, element.getAttributeValue("href"));
				hrefMap.put(path, element.getAttributeValue("href"));
			}
		}
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
		log.trace("Mapping orig report {} --> {}", resource, href);
		@SuppressWarnings("rawtypes")
		Measure path = new Measure(CtcMetrics.CTC_ORIG_REPORT_NAME);
		path.setData(href);
		context.saveMeasure(path);
		
	}

}
