/*
 * Testwell CTC++ Plugin
 * Copyright (C) 2017 Hella Gutmann Solutions GmbH
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

public class CtcCondition {
  private long lineId;
  private long conditionTrue;
  private long conditionFalse;
  private boolean isCondition;
  
  public CtcCondition(long lineId, long condTrue, long condFalse, boolean isCondition) {
    this.lineId = lineId;
    this.conditionTrue =  condTrue;
    this.conditionFalse = condFalse;
    this.isCondition = isCondition;
  }
  
  public long getLineId() {
    return this.lineId;
  }
  
  public long getConditionTrue() {
    return this.conditionTrue;
  }
  
  public long getConditionFalse() {
    return this.conditionFalse;
  }
  
  public long getLineHits()
  {
    return this.conditionTrue + this.conditionFalse;
  }
  
  public boolean isCondition() {
    return this.isCondition;
  }
}
