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
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleBillListService;

public class BillingCycleBillLister extends SiteDataTablePanel<BillDataDTO> {

    public BillingCycleBillLister() {
        super(BillDataDTO.class, GWT.<BillingCycleBillListService> create(BillingCycleBillListService.class), false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().bill().billType()).build(), //

                new ColumnDescriptor.Builder(proto().bill().executionDate()).build(), //
                new ColumnDescriptor.Builder(proto().bill().billingPeriodStartDate()).build(), //
                new ColumnDescriptor.Builder(proto().bill().billingPeriodEndDate()).build(), //
                new ColumnDescriptor.Builder(proto().bill().dueDate()).build(), //

                new ColumnDescriptor.Builder(proto().bill().currentAmount()).build(), //
                new ColumnDescriptor.Builder(proto().bill().taxes()).build(), //
                new ColumnDescriptor.Builder(proto().bill().totalDueAmount()).build(), //

                new ColumnDescriptor.Builder(proto().bill().billStatus()).build(), //

                new ColumnDescriptor.Builder(proto().bill().balanceForwardAmount()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().paymentReceivedAmount()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().depositRefundAmount()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().immediateAccountAdjustments()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().nsfCharges()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().bill().pendingAccountAdjustments()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().depositAmount()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().bill().pastDueAmount()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().serviceCharge()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().recurringFeatureCharges()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().bill().oneTimeFeatureCharges()).visible(false).build());

        DataTableModel<BillDataDTO> dataTableModel = new DataTableModel<BillDataDTO>();
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().bill().executionDate(), false), new Sort(proto().bill().dueDate(), false));
    }
}
