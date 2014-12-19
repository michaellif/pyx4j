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
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

@Transient
@ExtendsBO
public interface TenantInLeaseDTO extends LeaseTermTenant {

    public static enum ChangeStatus {
        New, Updated;
    }

    // ------------------------------------

    IPrimitive<IncomeSource> incomeSource();

    IPrimitive<ChangeStatus> changeStatus();
}
