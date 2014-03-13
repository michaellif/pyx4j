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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.payment.InsurancePaymentMethod;

public interface TenantSureTransaction extends IEntity {

    /**
     * Status flow:
     * 
     * New policy
     * Draft -> AuthorizationRejected
     * Draft -> Authorized -> AuthorizationReversal; cfcApiClient.bindQuote failed
     * Draft -> Authorized -> AuthorizedPaymentRejectedRetry
     * Draft -> Authorized -> Cleared
     * 
     * 
     * Monthly process:
     * Draft -> Cleared
     * Draft -> PaymentError; Caledon connection error -> create a new InsuranceTenantSureTransaction next time.
     * Draft -> PaymentRejected; make insurance "PendingCancellation" and send Email
     * 
     */
    public enum TransactionStatus {

        Draft,

        Authorized,

        AuthorizationRejected,

        AuthorizationReversal,

        AuthorizedPaymentRejectedRetry, // If next retry will fail  move status to "PaymentRejected" and  make insurance "PendingCancellation"

        PaymentError, // Caledon connection error ->  create a new  InsuranceTenantSureTransaction

        PaymentRejected, // make insurance "PendingCancellation"

        Cleared,
    }

    @Owner
    @JoinColumn
    @MemberColumn(notNull = true)
    TenantSureInsurancePolicy insurance();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    /**
     * Store the day when the payment should pave been changed if payment was done later.
     */
    IPrimitive<LogicalDate> paymentDue();

    InsurancePaymentMethod paymentMethod();

    IPrimitive<TransactionStatus> status();

    IPrimitive<String> transactionAuthorizationNumber();

    IPrimitive<Date> transactionDate();
}
