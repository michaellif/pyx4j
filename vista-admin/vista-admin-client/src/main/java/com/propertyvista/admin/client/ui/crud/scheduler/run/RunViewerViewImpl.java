/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.scheduler.run;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunData;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.ExecutionStatusUpdateDTO;

public class RunViewerViewImpl extends AdminViewerViewImplBase<Run> implements RunViewerView {

    private final IListerView<RunData> runDataLister;

    public RunViewerViewImpl() {
        super(AdminSiteMap.Management.Run.class, true);

        runDataLister = new ListerInternalViewImplBase<RunData>(new RunDataLister());

        setForm(new RunForm(true));
    }

    @Override
    public IListerView<RunData> getRunDataListerView() {
        return runDataLister;
    }

    @Override
    public void populateExecutionState(ExecutionStatusUpdateDTO result) {
        getForm().get(getForm().proto().status()).setValue(result.status().getValue());
        getForm().get(getForm().proto().stats().total()).setValue(result.stats().total().getValue());
        getForm().get(getForm().proto().stats().processed()).setValue(result.stats().processed().getValue());
        getForm().get(getForm().proto().stats().failed()).setValue(result.stats().failed().getValue());
        getForm().get(getForm().proto().stats().averageDuration()).setValue(result.stats().averageDuration().getValue());
        getForm().get(getForm().proto().stats().totalDuration()).setValue(result.stats().totalDuration().getValue());
    }
}