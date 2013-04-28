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
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.rpc.ExecutionStatusUpdateDTO;

public class RunViewerViewImpl extends OperationsViewerViewImplBase<Run> implements RunViewerView {

    private final ILister<RunData> runDataLister;

    public RunViewerViewImpl() {
        super(true);

        runDataLister = new ListerInternalViewImplBase<RunData>(new RunDataLister(true));

        setForm(new RunForm(this));
    }

    @Override
    public ILister<RunData> getRunDataListerView() {
        return runDataLister;
    }

    @Override
    public void populateExecutionState(ExecutionStatusUpdateDTO result) {
        getForm().get(getForm().proto().status()).setValue(result.status().getValue());
        getForm().get(getForm().proto().executionReport().total()).setValue(result.stats().total().getValue());
        getForm().get(getForm().proto().executionReport().processed()).setValue(result.stats().processed().getValue());
        getForm().get(getForm().proto().executionReport().failed()).setValue(result.stats().failed().getValue());
        getForm().get(getForm().proto().executionReport().erred()).setValue(result.stats().erred().getValue());
        getForm().get(getForm().proto().executionReport().averageDuration()).setValue(result.stats().averageDuration().getValue());
        getForm().get(getForm().proto().executionReport().totalDuration()).setValue(result.stats().totalDuration().getValue());
    }
}