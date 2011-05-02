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
package com.propertyvista.portal.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.portal.domain.pt.LeaseTerms;

public interface AptUnit extends IEntity {

    IPrimitive<String> name();

    IPrimitive<String> marketingName();

    @Caption(name = "Type")
    IPrimitive<String> unitType();

    IPrimitive<AptUnitEcomomicStatus> unitEcomomicStatus();

    /**
     * @see AptUnitEcomomicStatus#other
     */
    IPrimitive<String> unitEcomomicStatusDescr();

    IPrimitive<Integer> floor();

    /**
     * Number of the unit
     */
    IPrimitive<String> suiteNumber();

    Building building();

    /**
     * Square ft. size of unit
     */
    @Caption(name = "Sq F")
    IPrimitive<Integer> area();

    IPrimitive<AreaMeasurementType> areaMeasurementType();

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

    /**
     * Keeps current and future occupancy data
     * Used for DB Denormalization
     */
    IList<AptUnitOccupancy> currentOccupancies();

    @Format("MM/dd/yyyy")
    @Caption(name = "Available")
    @Indexed
    /**
     * Denormalizied field used for search, derived from @see AptUnitOccupancy
     */
    IPrimitive<Date> avalableForRent();

    @Owned
    @Caption(name = "Rent")
    IList<MarketRent> marketRent();

    @Detached
    LeaseTerms newLeaseTerms();

    /**
     * Object used as part of a marketing campaign to demonstrate the design, structure,
     * and appearance of unit.
     */
    @Detached
    Floorplan floorplan();

    /**
     * How much does the user need to put down
     */
    @Caption(name = "Deposit")
    Money requiredDeposit();

    IList<Amenity> amenities();

    IList<Utility> utilities();

    IList<UnitInfoItem> infoDetails();

    IList<Concession> concessions();

    IList<AddOn> addOns();

    @Detached
    @Deprecated
    //TODO VladS to clean it up
    ISet<Picture> pictures();

}
