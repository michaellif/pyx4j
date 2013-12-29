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

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@ToStringFormat("{0}, {1}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AptUnitItem extends IEntity {

    @I18n
    @XmlType(name = "AptUnitItemType")
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
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum FlooringType {

        hardwood,

        tile,

        laminate,

        parquet,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum CounterTopType {

        granite,

        marble,

        laminate,

        quartz,

        solidSurface,

        tile,

        wood,

        metal,

        naturalStone,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum CabinetsType {

        wood,

        woodVeneer,

        melamine,

        laminate,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    //TODO VladS. use this for join
    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    AptUnit aptUnit();

    // ----------- Info --------------------------

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "unitDetailType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> conditionNotes();

    // ----------- Details -----------------------

    IPrimitive<String> wallColor();

    IPrimitive<FlooringType> flooringType();

    IPrimitive<LogicalDate> flooringInstallDate();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> flooringValue();

    IPrimitive<CounterTopType> counterTopType();

    IPrimitive<LogicalDate> counterTopInstallDate();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> counterTopValue();

    IPrimitive<CabinetsType> cabinetsType();

    IPrimitive<LogicalDate> cabinetsInstallDate();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> cabinetsValue();
}
