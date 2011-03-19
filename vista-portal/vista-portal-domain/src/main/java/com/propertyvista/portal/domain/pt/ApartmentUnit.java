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
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.propertyvista.portal.domain.AddOn;
import com.propertyvista.portal.domain.Amenity;
import com.propertyvista.portal.domain.ApptUnit.Status;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Concession;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.UnitInfoItem;
import com.propertyvista.portal.domain.Utility;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface ApartmentUnit extends IEntity {

//    IPrimitive<Integer> floor();

    @Caption(name = "Type")
    IPrimitive<String> unitType();

//
//    /**
//     * Number of the unit
//     */
//    IPrimitive<String> suiteNumber();

    Building building();

    /**
     * Square ft. size of unit
     */
    @Caption(name = "Sq F")
    IPrimitive<Integer> area();

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
    @Caption(name = "Rent")
    IList<MarketRent> marketRent();

//
//    IPrimitive<Date> moveOut();
//
    @Format("MM/dd/yyyy")
    @Caption(name = "Available")
    IPrimitive<Date> avalableForRent();

    @Detached
    LeaseTerms newLeaseTerms();

    /**
     * Object used as part of a marketing campaign to demonstrate the design, structure,
     * and appearance of unit.
     */
    @Detached
    ApartmentFloorplan floorplan();

    /**
     * How much does the user need to put down
     */
    @Caption(name = "Deposit")
    Money requiredDeposit();

//    // need a lease-terms object
//    //IPrimitive<String> unitLeaseStatus();
//
//    //IPrimitive<String> unitOccpStatus();
//
//    public static enum Status {
//        available, reserved, leased, notice;
//    }

    IPrimitive<Status> status();

    IList<Amenity> amenities();

    IList<Utility> utilities();

    IList<UnitInfoItem> infoDetails();

    IList<Concession> concessions();

    IList<AddOn> addOns();
}
