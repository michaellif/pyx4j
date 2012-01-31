/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.tenant.lease.LeaseFinancial;

public interface BillingAccount extends IEntity {

    //Bidirectional reference
    @ReadOnly
    @Detached
    @Owner
    LeaseFinancial leaseFinancial();

    @ReadOnly
    BillingCycle billingCycle();

    /**
     * Assign to BillingRun during billing extract.
     * Set to null when last period bill has been approved or rejected.
     */
    BillingRun currentBillingRun();

}
