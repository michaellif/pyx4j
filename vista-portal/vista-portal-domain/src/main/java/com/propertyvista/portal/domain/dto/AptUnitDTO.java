/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;

import com.propertyvista.portal.domain.ptapp.LeaseTerms;

@Transient
public interface AptUnitDTO extends IEntity {

//    IPrimitive<Integer> floor();

    @Caption(name = "Type")
    IPrimitive<String> unitType();

//
//    /**
//     * Number of the unit
//     */
//    IPrimitive<String> suiteNumber();

    //Building building();

    /**
     * Square ft. size of unit
     */
    @Format("#0.#")
    @Caption(name = "Sq F")
    IPrimitive<Double> area();

    /**
     * Number of bedrooms in unit
     * 
     * TODO Artur: Can we move this to floorplan?
     */
    @Format("#0.#")
    @Caption(name = "Beds")
    IPrimitive<Double> bedrooms();

    /**
     * Number of bathrooms in unit
     * 
     * TODO Artur: Can we move this to floorplan?
     * 
     * TODO Are the units with the same number of b*rooms have the same plan.
     */
    @Format("#0.#")
    @Caption(name = "Baths")
    IPrimitive<Double> bathrooms();

//
//    /**
//     * Used for DB Denormalization
//     */
//    Lease currentLease();
//
    @Owned
    @Format("#0.00")
    IPrimitive<Double> unitRent();

    @Caption(name = "Deposit")
    IPrimitive<Double> requiredDeposit();

//
//    IPrimitive<LogicalDate> moveOut();
//
    @Format("MM/dd/yyyy")
    @Caption(name = "Available")
    IPrimitive<LogicalDate> avalableForRent();

    @Detached
    LeaseTerms newLeaseTerms();

    /**
     * Object used as part of a marketing campaign to demonstrate the design, structure,
     * and appearance of unit.
     */
    @Detached
    FloorplanDTO floorplan();

//    // need a lease-terms object
//    //IPrimitive<String> unitLeaseStatus();
//
//    //IPrimitive<String> unitOccpStatus();
//
//    public static enum Status {
//        available, reserved, leased, notice;
//    }

    //IPrimitive<AptUnitStatusType> status();

    IPrimitiveSet<String> amenities();

//    IList<Amenity> amenities();

    IPrimitive<String> utilities();

    IPrimitive<String> infoDetails();

    IPrimitive<String> concessions();

    IPrimitive<String> addOns();
}
