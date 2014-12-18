/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-30
 * @author ArtyomB
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationfile;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;
import com.propertyvista.operations.rpc.services.FundsReconciliationSummaryCrudService;

public class FundsReconciliationSummaryLister extends SiteDataTablePanel<FundsReconciliationSummaryDTO> {

    public FundsReconciliationSummaryLister() {
        super(FundsReconciliationSummaryDTO.class, GWT.<FundsReconciliationSummaryCrudService> create(FundsReconciliationSummaryCrudService.class), false,
                false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().id()).columnTitle("Summary Id").searchableOnly().build(), //
                new ColumnDescriptor.Builder(proto().merchantAccount().pmc()).build(), // 
                new ColumnDescriptor.Builder(proto().merchantAccount().pmc().namespace()).visible(false).build(), // 
                new ColumnDescriptor.Builder(proto().paymentDate()).build(), // 
                new ColumnDescriptor.Builder(proto().merchantTerminalId()).build(), //   
                new ColumnDescriptor.Builder(proto().merchantAccount()).build(), // 
                new ColumnDescriptor.Builder(proto().reconciliationStatus()).build(), //   
                new ColumnDescriptor.Builder(proto().processingStatus()).build(), // 
                new ColumnDescriptor.Builder(proto().grossPaymentAmount()).build(), // 
                new ColumnDescriptor.Builder(proto().grossPaymentCount()).build(), // 
                new ColumnDescriptor.Builder(proto().rejectItemsAmount()).build(), // 
                new ColumnDescriptor.Builder(proto().rejectItemsCount()).build());

        setDataTableModel(new DataTableModel<FundsReconciliationSummaryDTO>());
    }
}
