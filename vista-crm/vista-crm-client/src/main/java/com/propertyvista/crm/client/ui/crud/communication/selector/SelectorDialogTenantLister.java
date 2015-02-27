/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author arminea
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.Collection;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;

import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.shared.config.VistaFeatures;

public class SelectorDialogTenantLister extends EntityLister<Tenant> {

    public SelectorDialogTenantLister(SelectRecipientsDialogForm parent, Collection<Tenant> alreadySelected) {
        super(Tenant.class, GWT.<SelectTenantListService> create(SelectTenantListService.class), parent, alreadySelected);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().participantId()).filterAlwaysShown(VistaFeatures.instance().yardiIntegration()).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().name()).searchable(false).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().name().firstName()).searchableOnly().filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().name().lastName()).searchableOnly().filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().sex()).visible(false).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().birthDate(), false).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().email(), false).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().homePhone()).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().mobilePhone()).build(),//
                new ColumnDescriptor.Builder(proto().customer().person().workPhone()).build(),//
                new ColumnDescriptor.Builder(proto().lease()).searchable(false).build(),//
                new ColumnDescriptor.Builder(proto().lease().leaseId()).searchableOnly().build());

        DataTableModel<Tenant> dataTableModel = new DataTableModel<Tenant>();
        dataTableModel.setPageSize(DataTablePanel.PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

}
