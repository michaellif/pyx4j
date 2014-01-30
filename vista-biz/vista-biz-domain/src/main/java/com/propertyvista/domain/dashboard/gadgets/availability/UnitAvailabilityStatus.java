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
package com.propertyvista.domain.dashboard.gadgets.availability;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public interface UnitAvailabilityStatus extends IEntity {

    @I18n
    public enum Vacancy {

        Vacant,

        Notice;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum RentedStatus {

        Rented, Unrented, OffMarket;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum RentReadiness {

        RentReady,

        @Translate("Renovation in Progress")
        RenoInProgress,

        NeedsRepairs;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum Scoping {

        Scoped, Unscoped;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ReadOnly
    AptUnit unit();

    @ReadOnly
    Building building();

    @ReadOnly
    Floorplan floorplan();

    @ReadOnly
    Complex complex();

    // STATUS DATA
    IPrimitive<LogicalDate> statusFrom();

    IPrimitive<LogicalDate> statusUntil();

    @Caption(name = "Vacant/Notice")
    IPrimitive<Vacancy> vacancyStatus();

    @Caption(name = "Rented/Unrented/OffMarket")
    IPrimitive<RentedStatus> rentedStatus();

    IPrimitive<Scoping> scoping();

    @Caption(name = "Physical Condition")
    IPrimitive<RentReadiness> rentReadinessStatus();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Unit Rent ($)")
    IPrimitive<BigDecimal> unitRent();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Money Rent ($)")
    IPrimitive<BigDecimal> marketRent();

    /** <code>unitRent - marketRent</code> */
    @Caption(name = "Delta ($)")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rentDeltaAbsolute();

    /** <code>(unitRent - unitMarketRent)/marketRent</code> */
    @Caption(name = "Delta (%)")
    @Format("#,##0.00")
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> rentDeltaRelative();

    /**
     * Applicable for 'Notice'
     */
    @Caption(name = "Rent End")
    IPrimitive<LogicalDate> rentEndDay();

    /**
     * Applicable for 'Vacant', stores the first day when the unit has become vacant, used to calculate {@link #daysVacant()}
     */
    @Caption(name = "Vacant Since")
    IPrimitive<LogicalDate> vacantSince();

    /** Applicable only for 'Rented': denotes lease start of the following tenant, maybe different than move in date */
    @Caption(name = "Rented From")
    IPrimitive<LogicalDate> rentedFromDay();

    /**
     * Applicable only for 'Rented': denotes move in day of the following tenant
     */
    @Caption(name = "Move In")
    IPrimitive<LogicalDate> moveInDay();

}
