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
package com.propertyvista.domain.security;

import java.util.EnumSet;

import com.pyx4j.security.shared.Behavior;

public enum VistaCustomerPaymentTypeBehavior implements Behavior {

    EcheckPaymentsAllowed,

    CreditCardPaymentsAllowed,

    InteracPaymentsAllowed,

    DirectBankingPaymentsAllowed;

    public static EnumSet<VistaCustomerPaymentTypeBehavior> forAutoPay() {
        return EnumSet.of(EcheckPaymentsAllowed, CreditCardPaymentsAllowed);
    }

    public static EnumSet<VistaCustomerPaymentTypeBehavior> forPaymentMethodSetup() {
        return EnumSet.of(EcheckPaymentsAllowed, CreditCardPaymentsAllowed);
    }
}
