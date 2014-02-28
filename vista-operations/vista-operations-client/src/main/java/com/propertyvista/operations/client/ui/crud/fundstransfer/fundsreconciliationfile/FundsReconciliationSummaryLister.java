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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationfile;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;

public class FundsReconciliationSummaryLister extends AbstractLister<FundsReconciliationSummaryDTO> {

    public FundsReconciliationSummaryLister() {
        super(FundsReconciliationSummaryDTO.class, false, false);
        setAllowZoomIn(true);

        setColumnDescriptors(//@formatter:off       
                    new MemberColumnDescriptor.Builder(proto().paymentDate()).build(),          
                    new MemberColumnDescriptor.Builder(proto().merchantTerminalId()).build(),   
                    new MemberColumnDescriptor.Builder(proto().merchantAccount()).build(),   
                    new MemberColumnDescriptor.Builder(proto().reconciliationStatus()).build(),   
                    new MemberColumnDescriptor.Builder(proto().processingStatus()).build()
                );//@formatter:on   
    }
}
