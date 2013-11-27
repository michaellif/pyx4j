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

import java.text.DecimalFormat;

import com.kijiji.pint.rs.ILSUnit;
import com.kijiji.pint.rs.ILSUnit.BathroomsEnum;
import com.kijiji.pint.rs.ILSUnit.BedroomsEnum;
import com.kijiji.pint.rs.ILSUnit.IsFurnished;
import com.kijiji.pint.rs.ILSUnit.IsPetsAllowed;
import com.kijiji.pint.rs.ILSUnit.OfferedByEnum;

import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.ils.kijiji.mapper.dto.ILSFloorplanDTO;

public class KijijiUnitMapper {

    public void convert(ILSFloorplanDTO from, ILSUnit to) {
        to.setClientUnitId((int) from.floorplan().getPrimaryKey().asLong());
        to.setRentOrSale("rent");
        to.setOfferedBy(OfferedByEnum.OWNER);

        String title = from.profile().listingTitle().getValue();
        if (title == null) {
            title = from.floorplan().marketingName().isNull() ? from.floorplan().name().getValue() : from.floorplan().marketingName().getValue();
        }
        to.setTitle(title);
        to.setDescription(from.profile().description().isNull() ? from.floorplan().description().getValue() : from.profile().description().getValue());

        to.setBedrooms(getBedrooms(from.floorplan().bedrooms().getValue()));
        to.setBathrooms(getBathrooms(from.floorplan().bathrooms().getValue(), from.floorplan().halfBath().getValue()));

        to.setPrice(from.minPrice().getValue().toPlainString());
        to.setSquareFootage(DomainUtil.getAreaInSqFeet(from.floorplan().area(), from.floorplan().areaUnits()));

        to.setFurnished(from.isFurnished().isBooleanTrue() ? IsFurnished.YES : IsFurnished.NO);
        to.setPetsAllowed(from.isPetsAllowed().isBooleanTrue() ? IsPetsAllowed.YES : IsPetsAllowed.NO);
    }

    private BedroomsEnum getBedrooms(Integer beds) {
        if (beds != null) {
            if (beds >= 6) {
                return BedroomsEnum.Six_More;
            }
            String v = beds.toString();
            for (BedroomsEnum e : BedroomsEnum.values()) {
                if (v.equals(e.value())) {
                    return e;
                }
            }
        }
        return BedroomsEnum.None;
    }

    private BathroomsEnum getBathrooms(Integer baths, Integer halfBath) {
        if (baths != null) {
            double addBaths = halfBath == null ? 0 : halfBath / 2.0;
            if (baths + addBaths >= 6) {
                return BathroomsEnum.Six_More;
            }
            String v = addBaths > 0 ? new DecimalFormat("#.#").format(baths + addBaths) : baths.toString();
            for (BathroomsEnum e : BathroomsEnum.values()) {
                if (v.equals(e.value())) {
                    return e;
                }
            }
        }
        return BathroomsEnum.None;
    }
}
