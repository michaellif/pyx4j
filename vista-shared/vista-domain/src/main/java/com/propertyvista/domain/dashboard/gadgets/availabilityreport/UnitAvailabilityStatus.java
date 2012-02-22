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
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;

// TODO probably also must include Tenant Name/Contact Information
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

    // REFERENCED DATA
    IPrimitive<String> propertyCode();

    IPrimitive<String> buildingName();

    IPrimitive<String> address();

    IPrimitive<String> region();

    IPrimitive<String> complexName();

    /** {@link AptUnit#info()} -> {@link AptUnitInfo#number()} */
    IPrimitive<String> unitName();

    IPrimitive<String> floorplanName();

    IPrimitive<String> floorplanMarketingName();

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

    /** {@link AptUnit#availableForRent()} - 1 */
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> moveOutDay();

    /**
     * Applicable only for rented
     * 
     * @deprecated use {@link #rentedFromDate()} instead
     */
    @Deprecated
    @Format("MM/dd/yyyy")
    @Caption(name = "Move In Date")
    IPrimitive<LogicalDate> moveInDay();

    /** Applicable only for rented; maybe different than move out date */
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> rentedFromDate();

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
