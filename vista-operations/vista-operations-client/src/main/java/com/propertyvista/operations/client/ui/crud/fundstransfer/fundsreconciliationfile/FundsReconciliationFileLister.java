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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;
import com.propertyvista.operations.rpc.services.PadReconciliationFileCrudService;

public class FundsReconciliationFileLister extends SiteDataTablePanel<FundsReconciliationFileDTO> {

    public FundsReconciliationFileLister() {
        super(FundsReconciliationFileDTO.class, GWT.<AbstractCrudService<FundsReconciliationFileDTO>> create(PadReconciliationFileCrudService.class), false,
                false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().fileName()).build(), //
                new ColumnDescriptor.Builder(proto().fundsTransferType()).build(), //
                new ColumnDescriptor.Builder(proto().created()).build(), //
                new ColumnDescriptor.Builder(proto().remoteFileDate()).build(), //
                new ColumnDescriptor.Builder(proto().fileNameDate()).build());

        setDataTableModel(new DataTableModel<FundsReconciliationFileDTO>());
    }
}
