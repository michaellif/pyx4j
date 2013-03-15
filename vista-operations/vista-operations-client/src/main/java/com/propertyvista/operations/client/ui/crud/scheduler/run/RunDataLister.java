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
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.domain.scheduler.RunData;

public class RunDataLister extends AbstractLister<RunData> {

    public RunDataLister() {
        super(RunData.class, false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().pmc().dnsName()).build(),
            new MemberColumnDescriptor.Builder(proto().started()).build(),
            new MemberColumnDescriptor.Builder(proto().status()).build(),
            new MemberColumnDescriptor.Builder(proto().executionReport().total()).build(),
            new MemberColumnDescriptor.Builder(proto().executionReport().processed()).build(),
            new MemberColumnDescriptor.Builder(proto().executionReport().failed()).build(),
            new MemberColumnDescriptor.Builder(proto().executionReport().erred()).build(),
            new MemberColumnDescriptor.Builder(proto().executionReport().totalDuration()).build(),
            new MemberColumnDescriptor.Builder(proto().executionReport().message()).build(),
            new MemberColumnDescriptor.Builder(proto().errorMessage()).build(),
            new MemberColumnDescriptor.Builder(proto().updated()).build()
        );//@formatter:on
    }
}
