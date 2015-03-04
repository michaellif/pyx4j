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

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;

public class BillingCycleLister extends SiteDataTablePanel<BillingCycleDTO> {

    public BillingCycleLister() {
        super(BillingCycleDTO.class, GWT.<BillingCycleCrudService> create(BillingCycleCrudService.class), false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().building()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().billingType()).build(), //
                new ColumnDescriptor.Builder(proto().billingCycleStartDate()).build(), //
                new ColumnDescriptor.Builder(proto().billingCycleEndDate()).build(), //
                new ColumnDescriptor.Builder(proto().targetBillExecutionDate()).build(), //
                new ColumnDescriptor.Builder(proto().notRun()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().stats().notConfirmed()).build(), //
                new ColumnDescriptor.Builder(proto().stats().failed()).build(), //
                new ColumnDescriptor.Builder(proto().stats().rejected()).build(), //
                new ColumnDescriptor.Builder(proto().stats().confirmed()).build(), //
                new ColumnDescriptor.Builder(proto().total()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().pads()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().targetAutopayExecutionDate()).build(), //
                new ColumnDescriptor.Builder(proto().actualAutopayExecutionDate()).build() //
        );

        setDataTableModel(new DataTableModel<BillingCycleDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().billingCycleStartDate(), true));
    }
}
