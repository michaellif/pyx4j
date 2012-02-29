/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public interface LeaseAdjustmentPolicyItem extends IEntity {

    @ToString
    LeaseAdjustmentReason leaseAdjustmentReason();

    IList<Tax> taxes();
}