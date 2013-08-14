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
package com.propertyvista.crm.client.ui.crud.billing.bill;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;

public class BillLister extends AbstractLister<BillDataDTO> {

    private static final I18n i18n = I18n.get(BillLister.class);

    public BillLister() {
        super(BillDataDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().bill().billType()).build(),

            new MemberColumnDescriptor.Builder(proto().bill().executionDate()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().billingPeriodStartDate()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().billingPeriodEndDate()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().dueDate()).build(),
         
            new MemberColumnDescriptor.Builder(proto().bill().pastDueAmount()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().currentAmount()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().taxes()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().totalDueAmount()).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().billStatus()).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().balanceForwardAmount(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().paymentReceivedAmount(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().depositRefundAmount(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().immediateAccountAdjustments(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().nsfCharges(),false).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().pendingAccountAdjustments(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().depositAmount(),false).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().serviceCharge(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().recurringFeatureCharges(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().oneTimeFeatureCharges(),false).build()
        );//@formatter:on

        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);
        addActionItem(new Button(i18n.tr("Confirm Checked"), new Command() {
            @Override
            public void execute() {
                if (!getLister().getDataTablePanel().getDataTable().getCheckedItems().isEmpty()) {
                    MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to Confirm checked bills?"), new ConfirmDecline() {
                        @Override
                        public void onConfirmed() {
                            ((BillListerPresenter) getPresenter()).confirm(getDataTablePanel().getDataTable().getCheckedItems());
                        }

                        @Override
                        public void onDeclined() {
                        }
                    });
                }
            }
        }));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().bill().executionDate(), false), new Sort(proto().bill().dueDate(), false));
    }
}
