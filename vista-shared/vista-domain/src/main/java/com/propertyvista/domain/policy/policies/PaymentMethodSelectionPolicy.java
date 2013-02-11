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

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.framework.Policy;

/**
 * This 3x6 matrix represented as column to simplify DB maintenance.
 * 
 */
@DiscriminatorValue("PaymentMethodSelectionPolicy")
public interface PaymentMethodSelectionPolicy extends Policy {

    //=====  Accepted In CRM and in residentPortal

    IPrimitive<Boolean> acceptedCash();

    IPrimitive<Boolean> acceptedCheck();

    IPrimitive<Boolean> acceptedEcheck();

    IPrimitive<Boolean> acceptedEFT();

    IPrimitive<Boolean> acceptedCreditCard();

    IPrimitive<Boolean> acceptedInterac();

    //=====  Accepted In residentPortal

    IPrimitive<Boolean> residentPortalEcheck();

    IPrimitive<Boolean> residentPortalEFT();

    IPrimitive<Boolean> residentPortalCreditCard();

    IPrimitive<Boolean> residentPortalInterac();

    //===== Accepted when cashEquivalent flag on BillingAccount.paymentAccepted is set to CashEquivalent

    IPrimitive<Boolean> cashEquivalentCash();

    IPrimitive<Boolean> cashEquivalentCheck();

    IPrimitive<Boolean> cashEquivalentEcheck();

    // There is no way to Disabled this, We can just not advertise it portal
    IPrimitive<Boolean> cashEquivalentEFT();

    IPrimitive<Boolean> cashEquivalentCreditCard();

    IPrimitive<Boolean> cashEquivalentInterac();

}
