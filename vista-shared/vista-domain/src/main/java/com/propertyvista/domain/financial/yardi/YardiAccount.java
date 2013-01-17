/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.financial.yardi;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.tenant.lease.Lease;

public interface YardiAccount extends IEntity {

    @NotNull
    @ReadOnly
    @Detached
    Lease lease();

    /**
     * Charges that are currently due based on the latest Yardi report
     */
    IList<YardiChargeDetail> pendingCharges();

    /**
     * Payments that mare made since last Yardi update
     */
    IList<YardiPaymentDetail> payments();
}
