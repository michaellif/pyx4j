/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-07-17
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.tenantsure;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.operations.rpc.services.TenantSureTransactionListerService;

public class TenantSureTransactionLister extends SiteDataTablePanel<TenantSureTransaction> {

    public TenantSureTransactionLister() {
        super(TenantSureTransaction.class, GWT.<TenantSureTransactionListerService> create(TenantSureTransactionListerService.class));

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().amount()).build(), //
                new ColumnDescriptor.Builder(proto().paymentDue()).build(), //
                new ColumnDescriptor.Builder(proto().paymentMethod()).build(), //
                new ColumnDescriptor.Builder(proto().status()).build(), //
                new ColumnDescriptor.Builder(proto().transactionAuthorizationNumber()).build(), //
                new ColumnDescriptor.Builder(proto().transactionErrorMessage()).build(), //
                new ColumnDescriptor.Builder(proto().transactionDate()).build());

        setDataTableModel(new DataTableModel<TenantSureTransaction>());

        setItemZoomInCommand(null);
    }

}
