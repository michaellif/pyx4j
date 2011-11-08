/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 5, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.vacancyreport;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.unit.AptUnit;

public interface UnitAvailabilityStatus extends IEntity {

    // TODO ask Vlad about @Translatable and @XMLType
    public enum VacancyStatus {
        Vacant, Notice;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum RentedStatus {
        Rented, Unrented, OffMarket;
        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum RentReadinessStatus {
        RentReady, RenoInProgress, NeedsRepairs;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<LogicalDate> date();

    @Owner
    @ReadOnly
    @Detached
    AptUnit belongsTo();

    IPrimitive<VacancyStatus> vacancyStatus();

    IPrimitive<RentedStatus> rentedStatus();

    IPrimitive<Boolean> isScoped();

    IPrimitive<RentReadinessStatus> rentReadinessStatus();

    IPrimitive<Double> rentDeltaAbsolute();

    IPrimitive<Double> rentDeltaRelative();

    /** Applicable only for rented */
    IPrimitive<LogicalDate> moveInDay();

    /** {@link AptUnit#availableForRent()} - 1 */
    IPrimitive<LogicalDate> moveOutDay();

    /** Applicable only for rented; maybe different than move out date */
    IPrimitive<LogicalDate> rentedFromDate();
}
