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
package org.sonar.plugins.ctc.api.parser;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.NoSuchElementException;

import static org.fest.assertions.Fail.fail;

public abstract class CtcTextParserTest {

  private CtcTextParser testee;
  private static Logger log = LoggerFactory.getLogger(CtcTextParserTest.class);

  @Before
  public void setUp() throws Exception {
    testee = new CtcTextParser(getReport());
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testHasNext() {
    try {
      while (testee.next() != null)
        ;
      fail("No Such Element not thrown!");
    } catch (NoSuchElementException e) {
      Assertions.assertThat(testee.hasNext()).isEqualTo(false);
    }

  }

  @Test
  public void testNext() {
    while (testee.hasNext()) {
      testee.next();
    }

  }

  @Test
  public void testRemove() {
    try {
      testee.remove();
      fail("Remove shall not be supported");
    } catch (UnsupportedOperationException e) {

    }
  }

  @Test
  public void testGetReportDetails() {
    while (testee.hasNext()) {
      log.info("FOUND ELEMENT: {}", testee.next());
    }
  }

  public abstract File getReport();

}
