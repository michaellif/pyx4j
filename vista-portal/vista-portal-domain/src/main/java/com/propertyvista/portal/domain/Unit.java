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

import com.pyx4j.entity.shared.IPrimitive;

public interface Unit extends Property {

    IPrimitive<Integer> floor();

    IPrimitive<String> unitType();

    Building building();

    /**
     * Square ft. size of unit
     */
    IPrimitive<Integer> area();

    /**
     * Number of bedrooms in unit
     * 
     * TODO Artur: Can we move this to floorplan?
     */
    IPrimitive<Float> bedrooms();

    /**
     * Number of bathrooms in unit
     * 
     * TODO Artur: Can we move this to floorplan?
     * 
     * TODO Are the units with the same number of b*rooms have the same plan.
     */
    IPrimitive<Float> bathrooms();

    /**
     * Used for DB Denormalization
     */
    Lease currentLease();

    Money marketRent();

    IPrimitive<Date> moveOut();

    IPrimitive<Date> avalableForRent();

    /**
     * Object used as part of a marketing campaign to demonstrate the design, structure,
     * and appearance of unit.
     */
    Floorplan floorplan();

    IPrimitive<String> unitLeaseStatus();

    IPrimitive<String> unitOccpStatus();
}
