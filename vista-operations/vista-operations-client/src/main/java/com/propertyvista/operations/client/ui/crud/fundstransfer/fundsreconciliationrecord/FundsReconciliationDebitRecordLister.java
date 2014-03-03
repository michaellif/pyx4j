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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationrecord;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;

public class FundsReconciliationDebitRecordLister extends AbstractLister<FundsReconciliationRecordRecordDTO> {

    public FundsReconciliationDebitRecordLister() {
        super(FundsReconciliationRecordRecordDTO.class, false, false);
        setAllowZoomIn(true);

        setColumnDescriptors(//@formatter:off       
                    new MemberColumnDescriptor.Builder(proto().reconciliationSummary().id()).columnTitle("Summary Id").searchableOnly().build(),    
                    new MemberColumnDescriptor.Builder(proto().reconciliationSummary().reconciliationFile().id()).columnTitle("File Id").searchableOnly().build(),    
                    new MemberColumnDescriptor.Builder(proto().reconciliationSummary().reconciliationFile().fileName()).build(),
                    new MemberColumnDescriptor.Builder(proto().reconciliationSummary().merchantAccount().pmc()).build(),
                    new MemberColumnDescriptor.Builder(proto().reconciliationSummary().merchantAccount().pmc().namespace()).visible(false).build(),
         
                    new MemberColumnDescriptor.Builder(proto().merchantTerminalId()).build(),   
                    new MemberColumnDescriptor.Builder(proto().paymentDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().clientId()).build(),
                    new MemberColumnDescriptor.Builder(proto().transactionId()).build(),
                    new MemberColumnDescriptor.Builder(proto().amount()).build(),
                    new MemberColumnDescriptor.Builder(proto().reconciliationStatus()).build(),
                    new MemberColumnDescriptor.Builder(proto().reasonCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().reasonText()).build(),
                    new MemberColumnDescriptor.Builder(proto().fee()).build(),
                    new MemberColumnDescriptor.Builder(proto().processingStatus()).build()
                );//@formatter:on   
    }
}
