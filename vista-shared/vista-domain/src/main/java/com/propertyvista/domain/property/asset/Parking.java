/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@DiscriminatorValue("Parking")
public interface Parking extends BuildingElement {

    @I18n(context = "Parking Type")
    @XmlType(name = "ParkingType")
    public enum Type {

        surfaceLot,

        garageLot,

        coveredLot,

        street,

        none,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> name();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @NotNull
    @ToString(index = 1)
    @MemberColumn(name = "parkingType")
    IPrimitive<Type> type();

    @Format("#0.#")
    IPrimitive<Double> levels();

    // Read-Only (recalculated) items:     
    @Editor(type = EditorType.label)
    IPrimitive<Integer> totalSpaces();

    @Editor(type = EditorType.label)
    IPrimitive<Integer> regularSpaces();

    @Editor(type = EditorType.label)
    IPrimitive<Integer> disabledSpaces();

    @Editor(type = EditorType.label)
    IPrimitive<Integer> wideSpaces();

    @Editor(type = EditorType.label)
    IPrimitive<Integer> narrowSpaces();

    @Timestamp
    IPrimitive<Date> updated();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<ParkingSpot> _ParkingSpots();
}
