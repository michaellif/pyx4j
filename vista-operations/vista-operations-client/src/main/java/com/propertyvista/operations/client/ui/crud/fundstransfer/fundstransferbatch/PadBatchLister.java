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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferbatch;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.PadBatchDTO;

public class PadBatchLister extends AbstractLister<PadBatchDTO> {

    public PadBatchLister() {
        super(PadBatchDTO.class, false, false);
        setAllowZoomIn(true);

        setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().batchNumber()).build(),    
                    
                    new MemberColumnDescriptor.Builder(proto().pmc()).build(),
                    new MemberColumnDescriptor.Builder(proto().pmc().namespace()).visible(false).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().merchantTerminalId()).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().bankId()).build(),
                    new MemberColumnDescriptor.Builder(proto().branchTransitNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().accountNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().batchAmount()).build(),
                    new MemberColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().processingStatus()).build()
            );//@formatter:on
    }
}
