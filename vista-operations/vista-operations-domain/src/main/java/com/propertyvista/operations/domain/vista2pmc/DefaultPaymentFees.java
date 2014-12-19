/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 */
package com.propertyvista.operations.domain.vista2pmc;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;

@Table(prefix = "fee", namespace = VistaNamespace.operationsNamespace)
public interface DefaultPaymentFees extends AbstractPaymentFees {

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    @Caption(name = "Accepted eCheck (ACH)")
    IPrimitive<Boolean> acceptedEcheck();

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> acceptedDirectBanking();

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> acceptedVisa();

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> acceptedVisaConvenienceFee();

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> acceptedVisaDebit();

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> acceptedVisaDebitConvenienceFee();

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> acceptedMasterCard();

    @Override
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> acceptedMasterCardConvenienceFee();
}
