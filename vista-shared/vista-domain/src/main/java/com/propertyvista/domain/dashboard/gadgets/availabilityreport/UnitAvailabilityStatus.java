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
package com.propertyvista.domain.dashboard.gadgets.availabilityreport;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public interface UnitAvailabilityStatus extends IEntity {

    public enum Vacancy {
        Vacant,

        Notice;

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

    public enum RentReadiness {
        RentReady, RenoInProgress, NeedsRepairs;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

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
    IPrimitive<LogicalDate> statusDate();

    @Caption(name = "Vacant/Notice")
    IPrimitive<Vacancy> vacancyStatus();

    @Caption(name = "Rented/Unrented/OffMarket")
    IPrimitive<RentedStatus> rentedStatus();

    IPrimitive<Scoping> scoping();

    @Caption(name = "Physical Condition")
    IPrimitive<RentReadiness> rentReadinessStatus();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> unitRent();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> marketRent();

    /** <code>{@link #unitRent()} - {@link #unitMarketRent()} </code> */
    @Caption(name = "Delta, in $")
    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rentDeltaAbsolute();

    /** <code>({@link #unitRent()} - {@link #unitMarketRent()})/{@link #unitMarketRent()}</code> */
    @Caption(name = "Delta, in %")
    @Format("#0.00")
    IPrimitive<BigDecimal> rentDeltaRelative();

    /**
     * Applicable for 'Notice'
     */
    @Format("MM/dd/yyyy")
    @Caption(name = "Rent End")
    IPrimitive<LogicalDate> rentEndDay();

    /**
     * Applicable for 'Vacant', must be the same as {@link AptUnit#_availableForRent()} but stored here for efficiency. used to calculate days vacant
     */
    @Format("MM/dd/yyyy")
    @Caption(name = "Vacant Since")
    IPrimitive<LogicalDate> vacantSince();

    /** Applicable only for 'Rented': denotes lease start of the following tenant, maybe different than move in date */
    @Format("MM/dd/yyyy")
    @Caption(name = "Rented From")
    IPrimitive<LogicalDate> rentedFromDay();

    /**
     * Applicable only for 'Rented': denotes move in day of the following tenant
     */
    @Format("MM/dd/yyyy")
    @Caption(name = "Move In")
    IPrimitive<LogicalDate> moveInDay();

    @Caption(name = "Days Vacant")
    /** For Vacant units numberOfDays between today and availableForRent date */
    @Transient
    IPrimitive<Integer> daysVacant();

    /** days_vacant * marketRent / 30 */
    @Caption(name = "Revenue Lost ($)")
    @Format("#0.00")
    @Editor(type = EditorType.money)
    @Transient
    IPrimitive<BigDecimal> revenueLost();
}
