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

import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface InsuranceTenantSureDetails extends IEntity {

    @Owner
    @JoinColumn
    @MemberColumn(notNull = true)
    InsuranceTenantSure insurance();

    IPrimitive<BigDecimal> contentsCoverage();

    IPrimitive<BigDecimal> deductible();

    /** gross premium for the whole policy period */
    IPrimitive<BigDecimal> grossPremium();

    // TODO rename to grossPremiumTaxes
    @Owned
    @OrderBy(InsuranceTenantSureTax.OrderInOwner.class)
    IList<InsuranceTenantSureTaxGrossPremium> taxes();

    IPrimitive<BigDecimal> underwriterFee();

    @Owned
    @OrderBy(InsuranceTenantSureTax.OrderInOwner.class)
    IList<InsuranceTenantSureTaxGrossPremium> underwriterFeeTaxes();

    // TODO although right now this value is computed on our side it would be nicer to get it via CFC-API
    IPrimitive<BigDecimal> totalPayable();

    /** gross premium for the whole policy period */
    IPrimitive<BigDecimal> monthlyPremium();

    @Owned
    @OrderBy(InsuranceTenantSureTax.OrderInOwner.class)
    IList<InsuranceTenantSureTaxMonthlyPremium> monthlyPremiumTaxes();

}
