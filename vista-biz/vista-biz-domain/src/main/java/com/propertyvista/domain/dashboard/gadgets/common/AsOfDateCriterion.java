/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.common;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.criterion.Criterion;

public class AsOfDateCriterion implements Criterion {

    private static final long serialVersionUID = -8767795929725488006L;

    LogicalDate asOfDate;

    public AsOfDateCriterion() {
        this(new LogicalDate());
    }

    public AsOfDateCriterion(LogicalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    public LogicalDate getAsOfDate() {
        return new LogicalDate(asOfDate);
    }
}
