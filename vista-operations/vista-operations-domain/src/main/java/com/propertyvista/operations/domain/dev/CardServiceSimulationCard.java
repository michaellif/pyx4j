/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.dev;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;

@RequireFeature(ApplicationDevelopmentFeature.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
public interface CardServiceSimulationCard extends IEntity {

    @Owner
    @JoinColumn
    CardServiceSimulationMerchantAccount merchant();

    @ToString
    IPrimitive<CreditCardType> cardType();

    @ToString
    IPrimitive<String> number();

    @Owned
    IList<CardServiceSimulationToken> tokens();

    @NotNull
    @ToString(index = 2)
    @Format("MM/yyyy")
    @Caption(name = "Expiry Date")
    @Editor(type = EditorType.monthyearpicker)
    IPrimitive<LogicalDate> expiryDate();

    @Editor(type = EditorType.money)
    @Format("#0.00")
    IPrimitive<BigDecimal> balance();

    @Editor(type = EditorType.money)
    @Format("#0.00")
    IPrimitive<BigDecimal> reserved();

    @Caption(description = "Force rejection code on all transactions to this Account")
    IPrimitive<String> responseCode();

    @Timestamp(Update.Created)
    @Format("MM/dd/yyyy HH:mm")
    @Editor(type = EditorType.label)
    IPrimitive<Date> created();

    @Timestamp(Update.Updated)
    @Format("MM/dd/yyyy HH:mm")
    @Editor(type = EditorType.label)
    IPrimitive<Date> updated();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<CardServiceSimulationTransaction> transactions();

}
