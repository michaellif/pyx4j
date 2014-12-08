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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferfile;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.FundsTransferFileDTO;
import com.propertyvista.operations.rpc.services.PadFileCrudService;

public class FundsTransferFileLister extends SiteDataTablePanel<FundsTransferFileDTO> {

    public FundsTransferFileLister() {
        super(FundsTransferFileDTO.class, GWT.<AbstractCrudService<FundsTransferFileDTO>> create(PadFileCrudService.class), false, false);

        setColumnDescriptors( //
                new MemberColumnDescriptor.Builder(proto().fileCreationNumber()).build(), //
                new MemberColumnDescriptor.Builder(proto().fileName()).build(), //
                new MemberColumnDescriptor.Builder(proto().companyId()).build(), //
                new MemberColumnDescriptor.Builder(proto().status()).build(), //
                new MemberColumnDescriptor.Builder(proto().fundsTransferType()).build(), //
                new MemberColumnDescriptor.Builder(proto().sent()).build(), //
                new MemberColumnDescriptor.Builder(proto().created()).build(), //
                new MemberColumnDescriptor.Builder(proto().updated()).build(), //
                new MemberColumnDescriptor.Builder(proto().acknowledged()).build(), //
                new MemberColumnDescriptor.Builder(proto().recordsCount()).build(), //
                new MemberColumnDescriptor.Builder(proto().fileAmount()).build(), //
                new MemberColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build(), //
                new MemberColumnDescriptor.Builder(proto().acknowledgmentRejectReasonMessage()).build(), //
                new MemberColumnDescriptor.Builder(proto().acknowledgmentFileName(), false).build(), //
                new MemberColumnDescriptor.Builder(proto().acknowledgmentRemoteFileDate(), false).build(), //
                new MemberColumnDescriptor.Builder(proto().acknowledgmentStatus()).build() //
        );

        setDataTableModel(new DataTableModel<FundsTransferFileDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().created(), true));
    }
}
