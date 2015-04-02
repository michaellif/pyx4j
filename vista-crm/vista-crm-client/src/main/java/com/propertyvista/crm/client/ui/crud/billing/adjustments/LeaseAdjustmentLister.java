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
package com.propertyvista.crm.client.ui.crud.billing.adjustments;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentLister extends SiteDataTablePanel<LeaseAdjustment> {

    public LeaseAdjustmentLister() {
        super(LeaseAdjustment.class, GWT.<LeaseAdjustmentCrudService> create(LeaseAdjustmentCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().code()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().executionType()).build(), //
                new ColumnDescriptor.Builder(proto().receivedDate()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().targetDate()).build(), //
                new ColumnDescriptor.Builder(proto().amount()).build(), //
                new ColumnDescriptor.Builder(proto().tax()).build(), //
                new ColumnDescriptor.Builder(proto().status()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().description()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().updated()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().created()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().createdBy()).visible(false).build());

        setDataTableModel(new DataTableModel<LeaseAdjustment>());
    }

}
