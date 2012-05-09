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
package com.propertyvista.admin.client.ui.crud.scheduler.run;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.admin.domain.scheduler.Run;

public class RunLister extends ListerBase<Run> {

    public RunLister() {
        super(Run.class, false, false);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().status()).build(),
                new MemberColumnDescriptor.Builder(proto().stats().total()).build(),
                new MemberColumnDescriptor.Builder(proto().stats().processed()).build(),
                new MemberColumnDescriptor.Builder(proto().stats().failed()).build(),
                new MemberColumnDescriptor.Builder(proto().stats().averageDuration()).build(),
                new MemberColumnDescriptor.Builder(proto().stats().totalDuration()).build(),
                new MemberColumnDescriptor.Builder(proto().created()).build(),
                new MemberColumnDescriptor.Builder(proto().updated()).build()
        );//@formatter:on
    }
}
