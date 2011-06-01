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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.property.asset.building.Building;

public interface Parking extends IEntity {

    @Translatable
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

    IPrimitive<String> name();

    IPrimitive<String> description();

    @MemberColumn(name = "parkingType")
    IPrimitive<Type> type();

    @Format("#0.#")
    IPrimitive<Double> levels();

    IPrimitive<Integer> totalSpaces();

    IPrimitive<Integer> disabledSpaces();

    IPrimitive<Integer> regularSpaces();

    IPrimitive<Integer> doubleSpaces();

    IPrimitive<Integer> narrowSpaces();

    // ----- Financials -----------------

    @Format("#0.00")
    IPrimitive<Double> disableRent();

    @Format("#0.00")
    IPrimitive<Double> regularRent();

    @Format("#0.00")
    IPrimitive<Double> doubleRent();

    @Format("#0.00")
    IPrimitive<Double> narrowRent();

    @Format("#0.00")
    IPrimitive<Double> deposit();
}