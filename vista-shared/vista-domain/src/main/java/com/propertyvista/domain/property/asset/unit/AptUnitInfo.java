/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-19
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit;

import com.pyx4j.entity.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;

@EmbeddedEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AptUnitInfo extends IEntity {

    @I18n
    public enum EconomicStatus {

        residential,

        commercial,

        offMarket,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<EconomicStatus> economicStatus();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> economicStatusDescription();

    IPrimitive<Integer> floor();

    @ToString(index = 0)
    @MemberColumn(name = "unitNumber", sortAdapter = AlphanumIndexAdapter.class)
    @Indexed(group = "BuildingUnitNumber,11", uniqueConstraint = true, ignoreCase = true)
    @NotNull
    @Length(20)
    IPrimitive<String> number();

    // ---- Physical: ----------------

    @Format("0.##")
    IPrimitive<Double> area();

    IPrimitive<AreaMeasurementUnit> areaUnits();

    // This values are populated from floorplan and should not be editable
    @Caption(name = "Beds")
    @Editor(type = Editor.EditorType.label)
    IPrimitive<Integer> _bedrooms();

    // This values are populated from floorplan and should not be editable
    @Caption(name = "Baths")
    @Editor(type = Editor.EditorType.label)
    IPrimitive<Integer> _bathrooms();
}
