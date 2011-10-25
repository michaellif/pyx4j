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
package com.propertyvista.domain.property.asset.building;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.marketing.yield.Amenity;

@ToStringFormat("{0} {1}")
public interface BuildingAmenity extends Amenity {

    @I18n
    public enum Type {

        garage,

        fitnessCenter,

        fitness,

        elevator,

        parking,

        pool,

        availability24Hours,

        basketballCourt,

        businessCenter,

        childCare,

        clubDiscount,

        clubHouse,

        concierge,

        coverPark,

        doorAttendant,

        freeWeights,

        gate,

        groupExercise,

        guestRoom,

        highSpeed,

        housekeeping,

        houseSitting,

        laundry,

        library,

        mealService,

        nightPatrol,

        @Translate("On-Site Maintenance")
        onSiteMaintenance,

        @Translate("On-Site Management")
        onSiteManagement,

        packageReceiving,

        playGround,

        racquetball,

        recRoom,

        sauna,

        shortTermLease,

        spa,

        storageSpace,

        sundeck,

        tennisCourt,

        transportation,

        tvLounge,

        vintage,

        volleyballCourt,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @Detached
    @ReadOnly
    Building belongsTo();

    @ToString(index = 0)
    @MemberColumn(name = "buildingAmenityType")
    IPrimitive<Type> type();
}
