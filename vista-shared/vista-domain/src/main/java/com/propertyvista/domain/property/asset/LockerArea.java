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

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.property.asset.building.Building;

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

    Building belongsTo();

    // ----------- Infromation --------------------------------------------------------------------------

    IPrimitive<String> name();

    IPrimitive<String> description();

    IPrimitive<Boolean> isPrivate();

    @MemberColumn(name = "lockerSize")
    IPrimitive<Size> size();

    @Format("#0.#")
    IPrimitive<Double> levels();

    IPrimitive<Integer> totalLockers();

    IPrimitive<Integer> regularLockers();

    IPrimitive<Integer> largeLockers();

    IPrimitive<Integer> smallLockers();

    // ------------------ Financials ------------------------------------------------------------------

    @Format("#0.00")
    IPrimitive<Double> regularRent();

    @Format("#0.00")
    IPrimitive<Double> largeRent();

    @Format("#0.00")
    IPrimitive<Double> smallRent();

    @Format("#0.00")
    IPrimitive<Double> deposit();
}
