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
package com.propertyvista.admin.domain.dev;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.adminNamespace)
public interface CardServiceSimulation extends IEntity {

    IPrimitive<CreditCardType> cardType();

    IPrimitive<String> number();

    IPrimitive<String> token();

    @NotNull
    @ToString(index = 2)
    @Format("MM/yyyy")
    @Caption(name = "Expiry Date")
    @Editor(type = EditorType.monthyearpicker)
    IPrimitive<LogicalDate> expiryDate();

    @Format("#0.00")
    IPrimitive<BigDecimal> balance();

    IPrimitive<String> responseCode();

    @Owned
    IList<CardServiceSimulationTransaction> transactions();

}
