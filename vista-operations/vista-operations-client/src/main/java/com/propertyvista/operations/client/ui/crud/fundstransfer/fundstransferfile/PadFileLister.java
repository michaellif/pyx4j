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

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.PadFileDTO;

public class PadFileLister extends AbstractLister<PadFileDTO> {

    public PadFileLister() {
        super(PadFileDTO.class, false, false);
        setAllowZoomIn(true);

        setColumnDescriptors(//@formatter:off                
                    new MemberColumnDescriptor.Builder(proto().fileCreationNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().fileName()).build(),
                    new MemberColumnDescriptor.Builder(proto().companyId()).build(),
                    new MemberColumnDescriptor.Builder(proto().status()).build(),
                    new MemberColumnDescriptor.Builder(proto().fundsTransferType()).build(),
                    new MemberColumnDescriptor.Builder(proto().sent()).build(),
                    new MemberColumnDescriptor.Builder(proto().created()).build(),
                    new MemberColumnDescriptor.Builder(proto().updated()).build(),
                    new MemberColumnDescriptor.Builder(proto().acknowledged()).build(),
                    new MemberColumnDescriptor.Builder(proto().recordsCount()).build(),
                    new MemberColumnDescriptor.Builder(proto().fileAmount()).build(),
                    new MemberColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().acknowledgmentRejectReasonMessage()).build(),
                    new MemberColumnDescriptor.Builder(proto().acknowledgmentStatus()).build()
            );//@formatter:on
    }
}
