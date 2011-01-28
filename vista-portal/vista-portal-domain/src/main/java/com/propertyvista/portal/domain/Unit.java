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
     */
    IPrimitive<Float> bedrooms();

    /**
     * Number of bathrooms in unit
     */
    IPrimitive<Float> bathrooms();

    Money marketRent();

    /**
     * Unit used as part of a marketing campaign to demonstrate the design, structure, and
     * appearance of unit.
     */
    Unit modelUnit();

    Picture floorplan();

    IPrimitive<String> unitLeaseStatus();

    IPrimitive<String> unitOccpStatus();
}
