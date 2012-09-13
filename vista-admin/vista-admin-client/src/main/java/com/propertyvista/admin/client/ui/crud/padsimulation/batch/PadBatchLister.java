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
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.padsimulation.batch;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;

public class PadBatchLister extends ListerBase<PadSimBatch> {

    public PadBatchLister() {
        super(PadSimBatch.class, true);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().batchNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().terminalId()).build(),
                new MemberColumnDescriptor.Builder(proto().bankId()).build(),
                new MemberColumnDescriptor.Builder(proto().branchTransitNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().accountNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().recordsCount()).build(),
                new MemberColumnDescriptor.Builder(proto().batchAmount()).build(),
                new MemberColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build()
        );//@formatter:on
    }
}
