/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.Money;

/**
 * General required information for all Income types.
 */
public interface IIncomeInfo {

    @Caption(name = "Description")
    IPrimitive<String> name();

    @NotNull
    Money monthlyAmount();

    /**
     * Start of income period. For employment that would be employment start date.
     */
    @Caption(name = "Start on")
    IPrimitive<LogicalDate> starts();

    @Caption(name = "Stop on")
    IPrimitive<LogicalDate> ends();
}
