/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 19, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.util;

import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.Floorplan;
import com.propertyvista.portal.domain.pt.ApartmentFloorplan;
import com.propertyvista.portal.domain.pt.ApartmentUnit;

import com.pyx4j.entity.shared.EntityFactory;

public class Converter {

    public static ApartmentFloorplan convert(Floorplan from) {
        ApartmentFloorplan to = EntityFactory.create(ApartmentFloorplan.class);

        to.name().setValue(from.name().getValue());
        to.area().setValue(from.area().getValue());

        return to;
    }

    public static ApartmentUnit convert(ApptUnit from) {
        ApartmentUnit to = EntityFactory.create(ApartmentUnit.class);

        // iEntity
        to.building().set(from.building());
        to.floorplan().set(convert(from.floorplan()));
        to.newLeaseTerms().set(from.newLeaseTerms());
        to.requiredDeposit().set(from.requiredDeposit());
        to.infoDetails().set(from.infoDetails());

        to.amenities().set(from.amenities());
        to.concessions().set(from.concessions());
        to.utilities().set(from.utilities());
        to.addOns().set(from.addOns());

        to.status().set(from.status());

        // primitives
        to.unitType().setValue(from.unitType().getValue());
        to.area().setValue(from.area().getValue());
        to.avalableForRent().setValue(from.avalableForRent().getValue());
        to.bathrooms().setValue(from.bathrooms().getValue());
        to.bedrooms().setValue(from.bedrooms().getValue());
        to.marketRent().setValue(from.marketRent().getValue());

        return to;
    }
}
