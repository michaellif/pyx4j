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

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.property.asset.building.Building;

public interface Parking extends IEntity {

    @Translatable
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
            return I18nEnum.tr(this);
        }
    }

    @Owner
    @Detached
    Building belongsTo();

    @ToString(index = 0)
    IPrimitive<String> name();

    IPrimitive<String> description();

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
}