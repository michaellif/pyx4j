/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface ChargeItemAdjustment extends IEntity {

    @Translatable
    enum Type {
        percentage, monetary, free;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    enum ChargeType {
        discount, priceChange;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    enum TermType {
        firstMonth, lastMonth, term;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "concessionType")
    IPrimitive<Type> type();

    @NotNull
    IPrimitive<ChargeType> chargeType();

    @NotNull
    IPrimitive<TermType> termType();

    /*
     * for percentageOff - percentage
     * for monetaryOff - amount
     */
    @NotNull
    @Format("#0.00")
    @ToString(index = 1)
    @MemberColumn(name = "concessionValue")
    IPrimitive<Double> value();

}
