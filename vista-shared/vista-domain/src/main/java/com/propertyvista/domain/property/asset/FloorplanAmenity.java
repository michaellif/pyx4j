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
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.marketing.yield.Amenity;

@ToStringFormat("{0} {1}")
public interface FloorplanAmenity extends Amenity {

    @I18n
    public enum Type {

        additionalStorage,

        airConditioner,

        alarm,

        balcony,

        cable,

        carport,

        ceilingFan,

        controlledAccess,

        courtyard,

        dishwasher,

        disposal,

        dryer,

        fireplace,

        furnished,

        garage,

        handrails,

        heat,

        individualClimateControl,

        largeClosets,

        microwave,

        patio,

        privateBalcony,

        privatePatio,

        range,

        refrigerator,

        satellite,

        skylight,

        view,

        washer,

        wheelChair,

        wdHookup, //yuriyl what is this?

        windowCoverings,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 0)
    @MemberColumn(name = "floorplanType")
    IPrimitive<Type> type();

    @Owner
    @Detached
    @ReadOnly
    @Indexed
    @JoinColumn
    Floorplan belongsTo();
}
