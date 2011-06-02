/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.Medium;
import com.propertyvista.domain.Picture;
import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.building.Building;

@ToStringFormat("{0}, {1}")
public interface Floorplan extends IEntity {

    @Translatable
    public enum Type {

// TODO: ask Artur which types goes here:

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    // ----------- Infromation ----------------------

    @ToString(index = 1)
    @MemberColumn(name = "floorplanType")
    IPrimitive<Type> type();

    @ToString(index = 0)
    IPrimitive<String> name();

    IPrimitive<String> description();

    IPrimitive<Integer> unitCount();

    IPrimitive<String> availabilityUrl();

    @Transient
    IPrimitive<Integer> unitsAvailable();

    @Transient
    IPrimitive<Integer> displayedUnitsAvailable();

    IPrimitive<Integer> floorCount();

    IPrimitive<Integer> totalRoomCount();

    RangeGroup squareFeet();

    RangeGroup marketRent();

    RangeGroup effectiveRent();

    IList<Concession> concessions();

    @Detached
    IList<Medium> media();

    @Detached
    @com.pyx4j.entity.annotations.Owner
    Building building();

    // ----------- Old data:-------------------------

    @Owned
    @Deprecated
    //TODO VladS to clean it up
    IList<Picture> pictures();

    /**
     * Min value of square ft. size of unit
     */
    @Deprecated
    IPrimitive<Integer> minArea();

    /**
     * Max value of square ft. size of unit
     */
    @Deprecated
    IPrimitive<Integer> maxArea();
}
