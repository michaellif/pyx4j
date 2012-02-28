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
package com.propertyvista.crm.client.ui.crud.tenant.lease.payment;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.billing.Payment;

public class PaymentLister extends ListerBase<Payment> {

    public PaymentLister() {
        super(Payment.class, CrmSiteMap.Tenants.Payment.class, false, true);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().number()).build(),
            new MemberColumnDescriptor.Builder(proto().depositDate()).build(),
            new MemberColumnDescriptor.Builder(proto().amount()).build(),
            new MemberColumnDescriptor.Builder(proto().type()).build()
        );//@formatter:on
    }
}
