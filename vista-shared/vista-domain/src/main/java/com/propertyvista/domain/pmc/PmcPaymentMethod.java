/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.pmc;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.AbstractPaymentMethod;

@ToStringFormat("{0}{1,choice,null#|!null# - {1}}")
@Table(prefix = "admin", namespace = VistaNamespace.operationsNamespace)
public interface PmcPaymentMethod extends AbstractPaymentMethod {

    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    @Detached
    Pmc pmc();

    // Billing Address:
    @Override
    @Transient
    IPrimitive<Boolean> sameAsCurrent();

    @Override
    @Transient
    AddressSimple billingAddress();

    @Transient
    @Caption(description = "Use this payment method for Equifax payments")
    IPrimitive<Boolean> selectForEquifaxPayments();
}
