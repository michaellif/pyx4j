/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.reports;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.site.shared.domain.reports.ExportableReport;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;
import com.pyx4j.site.shared.domain.reports.ReportOrderColumnMetadata;

import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
@Caption(name = "EFT Report")
public interface EftReportMetadata extends ReportMetadata, ExportableReport {

    IPrimitive<Boolean> leasesOnNoticeOnly();

    @Caption(name = "Issues/Alerts")
    IPrimitive<Boolean> onlyWithNotice();

    @Caption(name = "Filter by Billing Cycle")
    IPrimitive<Boolean> filterByBillingCycle();

    @NotNull
    IPrimitive<BillingPeriod> billingPeriod();

    @NotNull
    IPrimitive<LogicalDate> billingCycleStartDate();

    @Caption(name = "Filter by Portfolio")
    IPrimitive<Boolean> filterByPortfolio();

    IList<Portfolio> selectedPortfolios();

    @Caption(name = "Filter by Complex")
    IPrimitive<Boolean> filterByComplex();

    IList<Portfolio> selectedComplexes();

    @Caption(name = "Filter by Buildings")
    IPrimitive<Boolean> filterByBuildings();

    IList<Building> selectedBuildings();

    IPrimitive<PaymentRecord.PaymentStatus> paymentStatus();

    @Caption(name = "Upcoming/Future EFTs")
    IPrimitive<Boolean> forthcomingEft();

    @Caption(name = "Filter by Expected Move Out")
    IPrimitive<Boolean> filterByExpectedMoveOut();

    /** minimum of expected move out */
    @NotNull
    IPrimitive<LogicalDate> minimum();

    /** maximum of expected move out */
    @NotNull
    IPrimitive<LogicalDate> maximum();

    ReportOrderColumnMetadata orderBy();
}
