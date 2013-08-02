/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.policy.framework.Policy;

/**
 * This 3x6 matrix represented as column to simplify DB maintenance.
 * 
 */
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@DiscriminatorValue("PaymentTypeSelectionPolicy")
public interface PaymentTypeSelectionPolicy extends Policy {

    //=====  Accepted In CRM and in residentPortal

    @Caption(name = "Cash")
    IPrimitive<Boolean> acceptedCash();

    @Caption(name = "Check")
    IPrimitive<Boolean> acceptedCheck();

    @Caption(name = "E-Check")
    IPrimitive<Boolean> acceptedEcheck();

    @Caption(name = "Direct Banking")
    //TODO rename the filed!
    IPrimitive<Boolean> acceptedEFT();

    @Caption(name = "Credit Card")
    @Deprecated
    IPrimitive<Boolean> acceptedCreditCard();

    @Caption(name = "Visa Credit Card")
    IPrimitive<Boolean> acceptedCreditCardVisa();

    @Caption(name = "MasterCard")
    IPrimitive<Boolean> acceptedCreditCardMasterCard();

    @Caption(name = "Visa Debit Card")
    IPrimitive<Boolean> acceptedVisaDebit();

    @Caption(name = "Interac")
    IPrimitive<Boolean> acceptedInterac();

    //=====  Accepted In residentPortal

    @Caption(name = "E-Check")
    IPrimitive<Boolean> residentPortalEcheck();

    @Caption(name = "Direct Banking")
    IPrimitive<Boolean> residentPortalEFT();

    @Caption(name = "Credit Card")
    @Deprecated
    IPrimitive<Boolean> residentPortalCreditCard();

    @Caption(name = "Visa Credit Card")
    IPrimitive<Boolean> residentPortalCreditCardVisa();

    @Caption(name = "MasterCard")
    IPrimitive<Boolean> residentPortalCreditCardMasterCard();

    @Caption(name = "Visa Debit Card")
    IPrimitive<Boolean> residentPortalVisaDebit();

    @Caption(name = "Interac")
    IPrimitive<Boolean> residentPortalInterac();

    //===== Accepted when cashEquivalent flag on BillingAccount.paymentAccepted is set to CashEquivalent

    @Caption(name = "Cash")
    IPrimitive<Boolean> cashEquivalentCash();

    @Caption(name = "Check")
    IPrimitive<Boolean> cashEquivalentCheck();

    @Caption(name = "E-Check")
    IPrimitive<Boolean> cashEquivalentEcheck();

    // There is no way to Disabled this, We can just not advertise it portal
    @Caption(name = "Direct Banking")
    IPrimitive<Boolean> cashEquivalentEFT();

    @Caption(name = "Credit Card")
    @Deprecated
    IPrimitive<Boolean> cashEquivalentCreditCard();

    @Caption(name = "Visa Credit Card")
    IPrimitive<Boolean> cashEquivalentCreditCardVisa();

    @Caption(name = "MasterCard")
    IPrimitive<Boolean> cashEquivalentCreditCardMasterCard();

    @Caption(name = "Visa Debit Card")
    IPrimitive<Boolean> cashEquivalentVisaDebit();

    @Caption(name = "Interac")
    IPrimitive<Boolean> cashEquivalentInterac();
}
