/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-13
 * @author vlads
 */
package com.propertyvista.operations.domain.eft.cards.to;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface FeeCalulationRequest extends IEntity {

    IPrimitive<CreditCardType> cardType();

    IPrimitive<BigDecimal> amount();

    @Length(30)
    IPrimitive<String> referenceNumber();

}
