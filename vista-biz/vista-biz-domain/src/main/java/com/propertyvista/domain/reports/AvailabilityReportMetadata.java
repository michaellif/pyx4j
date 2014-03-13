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
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.site.shared.domain.reports.HasAdvancedSettings;
import com.pyx4j.site.shared.domain.reports.PropertyCriterionEntity;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;

@Transient
@Caption(name = "Availability Report")
public interface AvailabilityReportMetadata extends ReportMetadata, HasAdvancedSettings {

    @I18n
    public enum RentedStatusPreset {

        All, Rented, Unrented, OffMarket;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    @I18n
    public enum RentReadinessStatusPreset {

        All,

        RentReady,

        @Translate("Renovation in Progress")
        RenovationInProgress,

        NeedsRepairs;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    /** is null when current */
    @NotNull
    IPrimitive<LogicalDate> asOf();

    IPrimitiveSet<UnitAvailabilityStatus.Vacancy> vacancyStatus();

    @NotNull
    IPrimitive<RentedStatusPreset> rentedStatus();

    @NotNull
    IPrimitive<RentReadinessStatusPreset> rentReadinessStatus();

    IList<PropertyCriterionEntity> availbilityTableCriteria();
}
