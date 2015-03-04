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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferrecord;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;
import com.propertyvista.operations.rpc.services.PadDebitRecordCrudService;

public class FundsTransferDebitRecordLister extends SiteDataTablePanel<FundsTransferRecordDTO> {

    public FundsTransferDebitRecordLister() {
        super(FundsTransferRecordDTO.class, GWT.<AbstractCrudService<FundsTransferRecordDTO>> create(PadDebitRecordCrudService.class), false, false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().padBatch().padFile().id()).columnTitle("File Id").searchableOnly().build(), //
                new ColumnDescriptor.Builder(proto().padBatch().padFile().fileName()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().padBatch().padFile().status()).visible(false).columnTitle("File status").build(), //
                new ColumnDescriptor.Builder(proto().padBatch().padFile().sent()).build(), //
                new ColumnDescriptor.Builder(proto().padBatch().padFile().fundsTransferType()).build(), //

                new ColumnDescriptor.Builder(proto().padBatch().pmc()).build(), //
                new ColumnDescriptor.Builder(proto().padBatch().pmc().namespace()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().padBatch().merchantTerminalId()).build(), //

                new ColumnDescriptor.Builder(proto().clientId()).build(), //
                new ColumnDescriptor.Builder(proto().amount()).build(), //
                new ColumnDescriptor.Builder(proto().bankId()).build(), //
                new ColumnDescriptor.Builder(proto().branchTransitNumber()).build(), //
                new ColumnDescriptor.Builder(proto().accountNumber()).build(), //
                new ColumnDescriptor.Builder(proto().transactionId()).build(), //
                new ColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build(), //
                new ColumnDescriptor.Builder(proto().processed()).build(), //
                new ColumnDescriptor.Builder(proto().processingStatus()).build(), //
                new ColumnDescriptor.Builder(proto().statusChangeDate()).build());

        setDataTableModel(new DataTableModel<FundsTransferRecordDTO>());
    }
}
