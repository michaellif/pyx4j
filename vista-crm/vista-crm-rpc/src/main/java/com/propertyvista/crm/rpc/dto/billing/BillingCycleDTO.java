/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-08
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.billing;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.billing.BillingCycle;

@Transient
@ExtendsBO
public interface BillingCycleDTO extends BillingCycle {

    // Statistics:

    @Caption(name = "Total Leases")
    IPrimitive<Long> total();

    @Caption(name = "Leases With Non Run Bills")
    IPrimitive<Long> notRun();

    @Caption(name = "PADs")
    IPrimitive<Long> pads();
}
