/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CardTransactionRecord extends IEntity {

    @Length(8)
    IPrimitive<String> merchantTerminalId();

    @Length(60)
    IPrimitive<String> paymentTransactionId();

    @NotNull
    @ToString(index = 0)
    IPrimitive<CreditCardType> cardType();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> feeAmount();

    IPrimitive<String> saleResponseCode();

    IPrimitive<String> saleResponseText();

    IPrimitive<String> feeResponseCode();

    @Timestamp(Timestamp.Update.Created)
    @Editor(type = EditorType.label)
    IPrimitive<Date> creationDate();
}
