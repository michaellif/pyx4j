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

    IPrimitive<Integer> area();

    IPrimitive<Float> bedrooms();

    IPrimitive<Float> bathrooms();

    Money marketRent();

    Unit modelUnit();

    Picture floorplan();

    IPrimitive<String> unitLeaseStatus();

    IPrimitive<String> unitOccpStatus();
}
