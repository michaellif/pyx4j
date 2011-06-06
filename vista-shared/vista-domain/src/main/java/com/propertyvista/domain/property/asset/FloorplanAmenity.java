/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity.SubType;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity.Type;

@ToStringFormat("{0} {1}")
public interface FloorplanAmenity extends Amenity {

    @ToString(index = 0)
    @MemberColumn(name = "atype")
    IPrimitive<Type> type();

    @ToString(index = 1)
    IPrimitive<SubType> subType();

    @Owner
    @Detached
    Floorplan belongsTo();
}
