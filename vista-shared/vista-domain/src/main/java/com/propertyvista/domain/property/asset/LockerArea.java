/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.property.asset.building.Building;

@ToStringFormat("{0} {1}")
public interface LockerArea extends IEntity {

    @Translatable
    public enum Size {

        large,

        small,

        regular;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Owner
    @Detached
    Building belongsTo();

    // ----------- Infromation:

    @ToString(index = 0)
    IPrimitive<String> name();

    IPrimitive<String> description();

    IPrimitive<Boolean> isPrivate();

    @ToString(index = 1)
    @Caption(name = "Size")
    IPrimitive<Size> lockerSize();

    @Format("#0.#")
    IPrimitive<Double> levels();

    // Read-Only info:     
    @Editor(type = EditorType.label)
    IPrimitive<Integer> totalLockers();

    @Editor(type = EditorType.label)
    IPrimitive<Integer> largeLockers();

    @Editor(type = EditorType.label)
    IPrimitive<Integer> regularLockers();

    @Editor(type = EditorType.label)
    IPrimitive<Integer> smallLockers();
}
