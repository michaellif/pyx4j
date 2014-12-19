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
 */
package com.propertyvista.operations.client.ui.crud.simulator.pad.file;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimBatch;
import com.propertyvista.operations.rpc.services.simulator.PadSimBatchCrudService;

public class PadSimBatchLister extends SiteDataTablePanel<PadSimBatch> {

    public PadSimBatchLister() {
        super(PadSimBatch.class, GWT.<AbstractCrudService<PadSimBatch>> create(PadSimBatchCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().batchNumber()).build(), //
                new ColumnDescriptor.Builder(proto().terminalId()).build(), //
                new ColumnDescriptor.Builder(proto().bankId()).build(), //
                new ColumnDescriptor.Builder(proto().branchTransitNumber()).build(), //
                new ColumnDescriptor.Builder(proto().accountNumber()).build(), //
                new ColumnDescriptor.Builder(proto().recordsCount()).build(), //
                new ColumnDescriptor.Builder(proto().batchAmount()).build(), //
                new ColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build());

        setDataTableModel(new DataTableModel<PadSimBatch>());
    }
}
