/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 26, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.kijiji.mapper;

import com.kijiji.pint.rs.ILSUnit;
import com.kijiji.pint.rs.ILSUnit.BathroomsEnum;
import com.kijiji.pint.rs.ILSUnit.BedroomsEnum;
import com.kijiji.pint.rs.ILSUnit.IsFurnished;
import com.kijiji.pint.rs.ILSUnit.IsPetsAllowed;
import com.kijiji.pint.rs.ILSUnit.OfferedByEnum;

import com.propertyvista.domain.property.asset.Floorplan;

public class KijijiUnitMapper {

    public void convert(Floorplan from, ILSUnit to) {
        to.setClientUnitId((int) from.getPrimaryKey().asLong());
        to.setRentOrSale("rent");
        to.setOfferedBy(OfferedByEnum.OWNER);
        to.setTitle("2 Bedroom condo by the lake");
        to.setBedrooms(BedroomsEnum.None);
        to.setBathrooms(BathroomsEnum.Six_More);
        to.setPrice("1134.00");
        to.setSquareFootage(864);
        to.setFurnished(IsFurnished.YES);
        to.setPetsAllowed(IsPetsAllowed.NO);
    }
}
