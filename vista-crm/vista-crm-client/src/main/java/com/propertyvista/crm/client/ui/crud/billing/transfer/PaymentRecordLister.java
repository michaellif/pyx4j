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
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentRecordLister extends AbstractLister<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentRecordLister.class);

    public PaymentRecordLister() {
        super(PaymentRecordDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().id()).build(),
            new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().building().propertyCode()).columnTitle(i18n.tr("Property Code")).visible(true).build(),
            new MemberColumnDescriptor.Builder(proto().padBillingCycle().billingCycleStartDate()).columnTitle(i18n.tr("Pre-Authorized Payment Cycle")).build(),
            new MemberColumnDescriptor.Builder(proto().padBillingCycle().id(), false).columnTitle(i18n.tr("Pre-Authorized Payment Cycle Id")).build(),
            new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().info().number()).columnTitle(i18n.tr("Unit")).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().billingAccount().lease().leaseId()).columnTitle(i18n.tr("Lease")).visible(true).build(),
            new MemberColumnDescriptor.Builder(proto().billingAccount().accountNumber()).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().paymentMethod().customer()).visible(false).build(), 
            new MemberColumnDescriptor.Builder(proto().amount()).build(),
            new MemberColumnDescriptor.Builder(proto().paymentMethod().type()).build(),
            new MemberColumnDescriptor.Builder(proto().createdDate()).build(),
            new MemberColumnDescriptor.Builder(proto().receivedDate()).build(),
            new MemberColumnDescriptor.Builder(proto().lastStatusChangeDate()).build(),
            new MemberColumnDescriptor.Builder(proto().targetDate()).build(),
            new MemberColumnDescriptor.Builder(proto().paymentStatus()).build(),
            new MemberColumnDescriptor.Builder(proto().rejectedWithNSF()).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().transactionErrorMessage()).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().paymentMethod().type()).build(),
            new MemberColumnDescriptor.Builder(proto().notice()).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().createdBy()).visible(false).searchable(false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().id(), false));
    }
}
