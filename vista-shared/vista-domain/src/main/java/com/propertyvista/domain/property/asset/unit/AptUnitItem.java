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

import java.sql.Date;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

@ToStringFormat("{0} {1}")
public interface AptUnitItem extends IEntity {

    @Translatable
    public enum Type {

        bedroom,

        bathroom,

        diningRoom,

        kitchen,

        sunroom,

        den,

        livingRoom,

        familyRoom,

        library,

        office,

        balcony,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum FlooringType {

        hardwood,

        tile,

        laminate,

        parcket,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum CounterTopType {

        granite,

        marble,

        laminate,

        euartz,

        solidSurface,

        tile,

        wood,

        metal,

        naturalStone,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum CabinetsType {

        wood,

        woodVeneer,

        melamine,

        laminate,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    //TODO VladS. use this for join
    @Owner
    AptUnit parent();

    // ----------- Info --------------------------

    @ToString(index = 0)
    @MemberColumn(name = "unitDetailType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    IPrimitive<String> description();

    IPrimitive<String> conditionNotes();

    // ----------- Details -----------------------

    IPrimitive<String> wallColour();

    IPrimitive<FlooringType> flooringType();

    IPrimitive<Date> flooringInstallDate();

    @Format("#0.00")
    IPrimitive<Double> flooringValue();

    IPrimitive<CounterTopType> counterTopType();

    IPrimitive<Date> counterTopInstallDate();

    @Format("#0.00")
    IPrimitive<Double> counterTopValue();

    IPrimitive<CabinetsType> cabinetsType();

    IPrimitive<Date> cabinetsInstallDate();

    @Format("#0.00")
    IPrimitive<Double> cabinetsValue();
}
