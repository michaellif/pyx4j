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

    @Caption(name = "EFT")
    IPrimitive<Boolean> acceptedEFT();

    @Caption(name = "Credit Card")
    IPrimitive<Boolean> acceptedCreditCard();

    @Caption(name = "Interac")
    IPrimitive<Boolean> acceptedInterac();

    //=====  Accepted In residentPortal

    @Caption(name = "E-Check")
    IPrimitive<Boolean> residentPortalEcheck();

    @Caption(name = "EFT")
    IPrimitive<Boolean> residentPortalEFT();

    @Caption(name = "Credit Card")
    IPrimitive<Boolean> residentPortalCreditCard();

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
    @Caption(name = "EFT")
    IPrimitive<Boolean> cashEquivalentEFT();

    @Caption(name = "Credit Card")
    IPrimitive<Boolean> cashEquivalentCreditCard();

    @Caption(name = "Interac")
    IPrimitive<Boolean> cashEquivalentInterac();
}
