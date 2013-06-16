/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.operations.business;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.rpc.report.ReportColumn;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CaledonReportModel extends IEntity {

    @Caption(name = "PMC Name")
    IPrimitive<String> pmcName();

    IPrimitive<String> MID();

    IPrimitive<Integer> buildings();

    IPrimitive<Integer> units();

    IPrimitive<BigDecimal> averageRent();

    IPrimitive<BigDecimal> maxLeaseCharges();

    @ReportColumn(ignore = true)
    IPrimitive<Integer> leaseCount();

    IPrimitive<BigDecimal> averageEFT();

    @ReportColumn(ignore = true)
    IPrimitive<Integer> eftCount();

}
