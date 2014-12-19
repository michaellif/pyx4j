/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2013
 * @author vlads
 */
package com.propertyvista.crm.rpc.dto.reports;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface EftVarianceReportRecordLeaseTotalsDTO extends IEntity {

    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalEft();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> charges();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> difference();
}
