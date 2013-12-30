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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferrecord;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;

public class PadDebitRecordLister extends AbstractLister<FundsTransferRecordDTO> {

    public PadDebitRecordLister() {
        super(FundsTransferRecordDTO.class, false, false);
        setAllowZoomIn(true);

        setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().padBatch().padFile().id()).columnTitle("File Id").searchableOnly().build(),    
                    new MemberColumnDescriptor.Builder(proto().padBatch().padFile().fileName(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().padBatch().padFile().status(), false).columnTitle("File status").build(),
                    new MemberColumnDescriptor.Builder(proto().padBatch().padFile().fundsTransferType()).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().padBatch().pmc()).build(),
                    new MemberColumnDescriptor.Builder(proto().padBatch().pmc().namespace()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().padBatch().merchantTerminalId()).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().clientId()).build(),
                    new MemberColumnDescriptor.Builder(proto().amount()).build(),
                    new MemberColumnDescriptor.Builder(proto().bankId()).build(),
                    new MemberColumnDescriptor.Builder(proto().branchTransitNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().accountNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().transactionId()).build(),
                    new MemberColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().processed()).build(),
                    new MemberColumnDescriptor.Builder(proto().processingStatus()).build()
            );//@formatter:on
    }
}
