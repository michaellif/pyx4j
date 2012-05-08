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
package com.propertyvista.admin.client.activity.crud.scheduler;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.scheduler.run.RunViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunData;
import com.propertyvista.admin.rpc.services.scheduler.RunCrudService;
import com.propertyvista.admin.rpc.services.scheduler.RunDataCrudService;

public class RunViewerActivity extends AdminViewerActivity<Run> implements RunViewerView.Presenter {

    private final Presenter<RunData> runDataLister;

    @SuppressWarnings("unchecked")
    public RunViewerActivity(Place place) {
        super(place, ManagementVeiwFactory.instance(RunViewerView.class), (AbstractCrudService<Run>) GWT.create(RunCrudService.class));

        runDataLister = new ListerActivityBase<RunData>(place, ((RunViewerView) view).getRunDataListerView(),
                (AbstractCrudService<RunData>) GWT.create(RunDataCrudService.class), RunData.class);
    }

    @Override
    protected void onPopulateSuccess(Run result) {
        super.onPopulateSuccess(result);

        runDataLister.setParent(result.getPrimaryKey());
        runDataLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) runDataLister).onStop();
        super.onStop();
    }
}
