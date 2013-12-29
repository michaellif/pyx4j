/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-19
 * @author Amer Sohail
 * @version $Id$
 */
package com.propertyvista.domain.reports;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

@Transient
@Caption(name = "Customer Credit Check Report")
public interface CustomerCreditCheckReportMetadata extends ReportMetadata {

    IPrimitive<LogicalDate> minCreditCheckDate();

    IPrimitive<LogicalDate> maxCreditCheckDate();

    IPrimitive<BigDecimal> minAmountChecked();

    IPrimitive<BigDecimal> maxAmountChecked();

}
