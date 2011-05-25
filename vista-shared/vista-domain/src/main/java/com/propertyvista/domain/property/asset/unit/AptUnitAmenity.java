/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-15
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.marketing.yield.Amenity;

@ToStringFormat("{0} {1}")
public interface AptUnitAmenity extends Amenity {

    @Translatable
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

        dishWasher,

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

        wdHookup,

        windowCoverings,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum SubType {

        central,

        window,

        wall,

        gas,

        gasOrElectric,

        luxury,

        wood,

        electric,

        radiant,

        decorative,

        unspecified;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 0)
    @MemberColumn(name = "unitAmenityType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    IPrimitive<SubType> subType();
}
