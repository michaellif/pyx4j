/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-13
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

public interface PreauthorizedPayment extends IEntity {

    public enum AmountType {
        Percent, Value;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<AmountType> amountType();

    @ToString(index = 1)
    @Format("#,##0.00")
    IPrimitive<BigDecimal> amount();

    @ToString(index = 2)
    LeasePaymentMethod paymentMethod();

    @Length(40)
    IPrimitive<String> comments();
}
