/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Scoping;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface UnitAvailabilityStatusDTO extends IEntity {

    // REFERENCES
    IPrimitive<String> propertyCode();

    IPrimitive<String> externalId();

    IPrimitive<String> buildingName();

    AddressStructured address();

    IPrimitive<String> propertyManager();

    IPrimitive<String> complex();

    IPrimitive<String> unit();

    IPrimitive<String> floorplanName();

    IPrimitive<String> floorplanMarketingName();

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
    @Caption(name = "Market Rent ($)")
    IPrimitive<BigDecimal> marketRent();

    @Caption(name = "Delta ($)")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rentDeltaAbsolute();

    @Caption(name = "Delta (%)")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rentDeltaRelative();

    @Format("MM/dd/yyyy")
    @Caption(name = "Rent End")
    IPrimitive<LogicalDate> rentEndDay();

    @Format("MM/dd/yyyy")
    @Caption(name = "Vacant Since")
    IPrimitive<LogicalDate> vacantSince();

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
    IPrimitive<Integer> daysVacant();

    /** days_vacant * marketRent / 30 */
    @Caption(name = "Revenue Lost ($)")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> revenueLost();

    // USED FOR BUINESS LOGIC
    /** used to implement link from list entry to unit */
    IPrimitive<Key> unitId();

    /** used to attach buildings filter */
    Building buildingsFilterAnchor();
}
