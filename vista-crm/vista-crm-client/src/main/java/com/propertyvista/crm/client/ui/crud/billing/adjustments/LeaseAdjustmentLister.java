/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.adjustments;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentLister extends ListerBase<LeaseAdjustment> {

    public LeaseAdjustmentLister() {
        super(LeaseAdjustment.class, true);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().reason().actionType()).build(),
            new MemberColumnDescriptor.Builder(proto().executionType()).build(),
            new MemberColumnDescriptor.Builder(proto().receivedDate(), false).build(),
            new MemberColumnDescriptor.Builder(proto().targetDate()).build(),
            new MemberColumnDescriptor.Builder(proto().amount()).build(),
            new MemberColumnDescriptor.Builder(proto().tax()).build(),
            new MemberColumnDescriptor.Builder(proto().status()).build(),
            new MemberColumnDescriptor.Builder(proto().description(), false).build(),
            new MemberColumnDescriptor.Builder(proto().updated(), false).build(),
            new MemberColumnDescriptor.Builder(proto().created(), false).build(),
            new MemberColumnDescriptor.Builder(proto().createdBy(), false).build()
        );//@formatter:on
    }
}
