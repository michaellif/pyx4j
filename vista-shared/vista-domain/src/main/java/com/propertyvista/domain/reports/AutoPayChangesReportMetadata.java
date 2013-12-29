/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-12
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

import com.propertyvista.domain.property.asset.building.Building;

@Transient
@Caption(name = "AutoPay Changes Report")
public interface AutoPayChangesReportMetadata extends ReportMetadata, ExportableReport {

    IPrimitive<Boolean> leasesOnNoticeOnly();

    IPrimitive<Boolean> filterByBuildings();

    IList<Building> buildings();

    IPrimitive<Boolean> filterByExpectedMoveOut();

    @NotNull
    IPrimitive<LogicalDate> minimum();

    @NotNull
    IPrimitive<LogicalDate> maximum();
}
