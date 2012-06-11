/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;

public class BillingCycleLeaseLister extends ListerBase<BillingCycleLeaseDTO> {

    public BillingCycleLeaseLister() {
        super(BillingCycleLeaseDTO.class, false);
        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().lease()).build(),
            new MemberColumnDescriptor.Builder(proto().nonConfirmedBills()).build(),
            new MemberColumnDescriptor.Builder(proto().nonRunnedBills()).build(),
            new MemberColumnDescriptor.Builder(proto().failedBills()).build()
        );//@formatter:on
    }
}
