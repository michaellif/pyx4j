/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.reports;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.reports.HasAdvancedSettings;
import com.pyx4j.entity.shared.reports.ReportMetadata;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;

@Transient
public interface AvailabilityReportMetadata extends ReportMetadata, HasAdvancedSettings {

    /** is null when current */
    @NotNull
    IPrimitive<LogicalDate> asOf();

    IPrimitiveSet<UnitAvailabilityStatus.Vacancy> vacancyStatus();

    IPrimitiveSet<UnitAvailabilityStatus.RentedStatus> rentedStatus();

    IPrimitiveSet<UnitAvailabilityStatus.RentReadiness> rentReadinessStatus();

    IPrimitiveSet<UnitAvailabilityStatus.Scoping> scopingStatus();

}
