/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.common;

import java.io.Serializable;

import com.pyx4j.commons.LogicalDate;

public final class TimeInterval implements Serializable {

    private static final long serialVersionUID = 4181389736311010609L;

    private final LogicalDate from;

    private final LogicalDate to;

    public TimeInterval(LogicalDate from, LogicalDate to) {
        this.from = from;
        this.to = to;
    }

    public LogicalDate getFrom() {
        return new LogicalDate(from);
    }

    public LogicalDate getTo() {
        return new LogicalDate(to);
    }

}
