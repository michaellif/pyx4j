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

import com.pyx4j.commons.Key;
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
@Deprecated
public interface AptUnitDTO extends IEntity {

    IPrimitive<Key> selectedUnitID();

    @Caption(name = "Type")
    IPrimitive<String> unitType();

    /**
     * Square ft. size of unit
     */
    @Format("#0.#")
    @Caption(name = "Sq. Feet")
    IPrimitive<Double> area();

    /**
     * Number of bedrooms in unit
     * 
     * TODO Artur: Can we move this to floorplan?
     */
    @Caption(name = "Beds")
    IPrimitive<Integer> bedrooms();

    /**
     * Number of bathrooms in unit
     * 
     * TODO Artur: Can we move this to floorplan?
     * 
     * TODO Are the units with the same number of b*rooms have the same plan.
     */
    @Caption(name = "Baths")
    IPrimitive<Integer> bathrooms();

    @Owned
    @Format("#0.00")
    IPrimitive<Double> unitRent();

    @Caption(name = "Deposit")
    IPrimitive<Double> requiredDeposit();

    @Format("MM/dd/yyyy")
    @Caption(name = "Available")
    IPrimitive<LogicalDate> availableForRent();

    @Detached
    LeaseTerms newLeaseTerms();

    /**
     * Object used as part of a marketing campaign to demonstrate the design, structure,
     * and appearance of unit.
     */
    @Detached
    FloorplanDTO floorplan();

    IPrimitiveSet<String> amenities();

    IPrimitive<String> informationDetails();

    IPrimitive<String> concessions();

}
