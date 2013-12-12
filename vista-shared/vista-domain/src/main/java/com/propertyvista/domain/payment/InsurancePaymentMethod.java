/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;

import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.tenant.lease.Tenant;

@DiscriminatorValue("InsurancePaymentMethod")
public interface InsurancePaymentMethod extends PaymentMethod {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    Tenant tenant();

    @NotNull
    @Caption(name = "I agree to the Terms")
    @MemberColumn(name = "signature")
    CustomerSignature preAuthorizedAgreementSignature();
}
