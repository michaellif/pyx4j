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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;

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

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> economicStatusDescription();

    IPrimitive<Integer> floor();

    @ToString(index = 0)
    @MemberColumn(name = "unitNumber")
    IPrimitive<String> number();

    // ---- Physical: ----------------

    IPrimitive<Double> area();

    IPrimitive<AreaMeasurementUnit> areaUnits();

    // This values are populated from floorplan and should not be editable
    @Caption(name = "Beds")
    IPrimitive<Integer> _bedrooms();

    // This values are populated from floorplan and should not be editable
    @Caption(name = "Baths")
    IPrimitive<Integer> _bathrooms();
}
