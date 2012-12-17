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

    // TODO i don't like this duplication, actually this value is held in parent insurance certificate
    IPrimitive<BigDecimal> liabilityCoverage();

    IPrimitive<BigDecimal> contentsCoverage();

    IPrimitive<BigDecimal> deductible();

    IPrimitive<BigDecimal> grossPremium();

    IPrimitive<BigDecimal> underwriterFee();

    @Owned
    IList<InsuranceTenantSureTax> taxes();

}
