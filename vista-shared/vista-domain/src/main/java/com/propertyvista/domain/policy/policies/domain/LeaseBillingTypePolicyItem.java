/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 27, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public interface LeaseBillingTypePolicyItem extends IEntity {

    IPrimitive<PaymentFrequency> paymentFrequency();

    // TODO moved from LeaseBillingPolicy - if null, use LeaseStardDate
    IPrimitive<Integer> billingCycleStartDay();

    IPrimitive<Integer> offsetPaymentDueDay();

    IPrimitive<Integer> offsetPreauthorizedPaymentDay();

    IPrimitive<Integer> offsetExecutionTargetDay();

}
