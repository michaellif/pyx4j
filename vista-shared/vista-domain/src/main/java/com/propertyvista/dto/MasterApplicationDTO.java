/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;

@Transient
public interface MasterApplicationDTO extends MasterApplication {

    Tenant mainApplicant();

    IPrimitive<Integer> numberOfOccupants();

    IPrimitive<Integer> numberOfCoApplicants();

    IPrimitive<Integer> numberOfGuarantors();

    @Format("#0.00")
    IPrimitive<Double> rentPrice();

    @Format("#0.00")
    IPrimitive<Double> parkingPrice();

    @Format("#0.00")
    IPrimitive<Double> otherPrice();

    @Format("#0.00")
    IPrimitive<Double> deposit();

    @Caption(name = "Promotions/Discounts")
    IPrimitive<Boolean> discounts();

    IList<TenantInfoDTO> tenantsWithInfo();

    IList<TenantFinancialDTO> tenantFinancials();
}
