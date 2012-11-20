/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.insurance;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.InsurancePaymentMethod;

public interface InsuranceTenantSureTransaction extends IEntity {

    enum TransactionStatus {

        Draft,

        Authorized,

        Processing,

        Rejected,

        Cleared,
    }

    @Owner
    @JoinColumn
    @MemberColumn(notNull = true)
    InsuranceTenantSure insurance();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    InsurancePaymentMethod paymentMethod();

    IPrimitive<TransactionStatus> status();

    IPrimitive<String> transactionAuthorizationNumber();

    IPrimitive<Date> transactionDate();
}
