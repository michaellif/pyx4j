/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferLister extends AbstractLister<AggregatedTransfer> {

    private static final I18n i18n = I18n.get(AggregatedTransferLister.class);

    public AggregatedTransferLister() {
        super(AggregatedTransfer.class, false);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().paymentDate()).build(), 
                new MemberColumnDescriptor.Builder(proto().status()).build(),
                
                new MemberColumnDescriptor.Builder(proto().merchantAccount().accountNumber()).searchableOnly().columnTitle(i18n.tr("Merchant Account Number")).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount()).searchable(false).build(),
                
                new MemberColumnDescriptor.Builder(proto().grossPaymentCount()).build(),
                new MemberColumnDescriptor.Builder(proto().grossPaymentAmount()).build(),
                
                new MemberColumnDescriptor.Builder(proto().rejectItemsAmount()).build(),
                new MemberColumnDescriptor.Builder(proto().rejectItemsFee()).build(),
                new MemberColumnDescriptor.Builder(proto().rejectItemsCount()).build(),
                
                new MemberColumnDescriptor.Builder(proto().returnItemsAmount()).build(),
                new MemberColumnDescriptor.Builder(proto().returnItemsFee()).build(),
                new MemberColumnDescriptor.Builder(proto().returnItemsCount()).build(),
                
                new MemberColumnDescriptor.Builder(proto().payments().$().id()).searchableOnly().columnTitle(i18n.tr("Payment Id")).build(),
                new MemberColumnDescriptor.Builder(proto().returnedPayments().$().id()).searchableOnly().columnTitle(i18n.tr("Returned Payment Id")).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().paymentDate(), false), new Sort(proto().status(), false));
    }
}
