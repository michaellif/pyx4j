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
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.marketing.yield.Amenity;

public interface BuildingAmenity extends Amenity {

    @Translatable
    public enum Type {

        availability24Hours,

        basketballCourt,

        businessCenter,

        childCare,

        clubDiscount,

        clubHouse,

        concierge,

        coverPark,

        doorAttendant,

        fitnessCenter,

        elevator,

        freeWeights,

        garage,

        gate,

        groupExcercise,

        guestRoom,

        highSpeed,

        housekeeping,

        houseSitting,

        laundry,

        library,

        mealService,

        nightPatrol,

        onSiteMaintenance,

        onSiteManagement,

        packageReceiving,

        pool,

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
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum SubType {

        attached,

        detached,

        both;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Owner
    @Detached
    Building belongsTo();

    @MemberColumn(name = "buildingAmenityType")
    IPrimitive<Type> type();

    IPrimitive<SubType> subType();
}
