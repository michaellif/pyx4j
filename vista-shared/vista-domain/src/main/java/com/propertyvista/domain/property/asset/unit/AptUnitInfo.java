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
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;

public interface AptUnitInfo extends IEntity {

    @Translatable
    public enum EcomomicStatus {

        residential,

        commercial,

        offMarket,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    IPrimitive<String> name();

    @MemberColumn(name = "unitType")
    IPrimitive<AptUnitType> type();

    IPrimitive<String> typeDescription();

    IPrimitive<EcomomicStatus> economicStatus();

    IPrimitive<String> economicStatusDescription();

    IPrimitive<Integer> floor();

    @MemberColumn(name = "unitNumber")
    IPrimitive<String> number();

    @Detached
    Building building();

    // ---- Physical: ----------------

    IPrimitive<Double> area();

    IPrimitive<AreaMeasurementUnit> areaUnits();

    @Format("#0.#")
    @Caption(name = "Beds")
    IPrimitive<Double> bedrooms();

    @Format("#0.#")
    @Caption(name = "Baths")
    IPrimitive<Double> bathrooms();

    IList<Utility> utilities();

    IList<AptUnitItem> details();
}
