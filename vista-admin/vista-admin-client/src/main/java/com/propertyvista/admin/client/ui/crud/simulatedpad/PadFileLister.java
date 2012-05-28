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
package com.propertyvista.admin.client.ui.crud.simulatedpad;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;

public class PadFileLister extends ListerBase<PadSimFile> {

    public PadFileLister() {
        super(PadSimFile.class, false, false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().fileName()).build(),
            new MemberColumnDescriptor.Builder(proto().fileCreationNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().status()).build(),
            new MemberColumnDescriptor.Builder(proto().received()).build(),
            new MemberColumnDescriptor.Builder(proto().recordsCount()).build(),
            new MemberColumnDescriptor.Builder(proto().fileAmount()).build(),
            new MemberColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build()
        );//@formatter:on
    }
}
