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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferLister extends SiteDataTablePanel<AggregatedTransfer> {

    private static final I18n i18n = I18n.get(AggregatedTransferLister.class);

    public AggregatedTransferLister() {
        super(AggregatedTransfer.class, GWT.<AggregatedTransferCrudService> create(AggregatedTransferCrudService.class), false);

        setColumnDescriptors( //
                new MemberColumnDescriptor.Builder(proto().paymentDate()).build(), //
                new MemberColumnDescriptor.Builder(proto().status()).build(), //

                new MemberColumnDescriptor.Builder(proto().merchantAccount().accountNumber()).searchableOnly().columnTitle(i18n.tr("Merchant Account Number"))
                        .build(), //
                new MemberColumnDescriptor.Builder(proto().merchantAccount()).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().fundsTransferType()).build(), //

                new MemberColumnDescriptor.Builder(proto().netAmount()).build(), //
                new MemberColumnDescriptor.Builder(proto().grossPaymentAmount()).build(), //
                new MemberColumnDescriptor.Builder(proto().grossPaymentFee()).build(), //
                new MemberColumnDescriptor.Builder(proto().grossPaymentCount()).build(), //

                new MemberColumnDescriptor.Builder(proto().adjustments(), false).sortable(false).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().adjustments().$().adjustment()).searchableOnly().build(), //

                new MemberColumnDescriptor.Builder(proto().chargebacks(), false).sortable(false).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().chargebacks().$().chargeback()).searchableOnly().build(), //

                new MemberColumnDescriptor.Builder(proto().payments().$().id()).searchableOnly().columnTitle(i18n.tr("Payment Id")).build(), //
                new MemberColumnDescriptor.Builder(proto().returnedPayments().$().id()).searchableOnly().columnTitle(i18n.tr("Returned Payment Id")).build());

        setDataTableModel(new DataTableModel<AggregatedTransfer>());

        addUpperActionItem(new Button(i18n.tr("Export"), new Command() {
            @Override
            public void execute() {
                onAggregateTransferDownload();
            }
        }));

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().paymentDate(), true), new Sort(proto().status(), false));
    }

    public void onAggregateTransferDownload() {

    }
}
