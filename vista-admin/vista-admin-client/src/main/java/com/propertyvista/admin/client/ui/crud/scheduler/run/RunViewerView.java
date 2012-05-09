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

import com.pyx4j.site.client.ui.crud.form.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunData;
import com.propertyvista.admin.rpc.ExecutionStatusUpdateDTO;

public interface RunViewerView extends IViewerView<Run> {

    interface Presenter extends IViewerView.Presenter {
    }

    IListerView<RunData> getRunDataListerView();

    void populateExecutionState(ExecutionStatusUpdateDTO result);
}
