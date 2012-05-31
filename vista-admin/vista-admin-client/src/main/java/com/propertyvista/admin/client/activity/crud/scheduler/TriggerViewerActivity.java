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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.scheduler.trigger.TriggerViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.services.scheduler.RunCrudService;
import com.propertyvista.admin.rpc.services.scheduler.TriggerCrudService;

public class TriggerViewerActivity extends AdminViewerActivity<Trigger> implements TriggerViewerView.Presenter {

    private final Presenter<Run> runLister;

    @SuppressWarnings("unchecked")
    public TriggerViewerActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(TriggerViewerView.class), (AbstractCrudService<Trigger>) GWT.create(TriggerCrudService.class));

        runLister = new ListerActivityBase<Run>(place, ((TriggerViewerView) getView()).getRunListerView(),
                (AbstractCrudService<Run>) GWT.create(RunCrudService.class), Run.class);
    }

    @Override
    protected void onPopulateSuccess(Trigger result) {
        super.onPopulateSuccess(result);

        runLister.setParent(result.getPrimaryKey());
        runLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) runLister).onStop();
        super.onStop();
    }

    @Override
    public void runImmediately() {
        GWT.<TriggerCrudService> create(TriggerCrudService.class).runImmediately(new DefaultAsyncCallback<Run>() {
            @Override
            public void onSuccess(Run result) {
                AppSite.getPlaceController().goTo(
                        AppSite.getHistoryMapper().createPlace(AdminSiteMap.Management.Run.class).formViewerPlace(result.getPrimaryKey()));
            }
        }, EntityFactory.createIdentityStub(Trigger.class, getEntityId()));
    }

    @Override
    public void runForDate(LogicalDate date) {
        GWT.<TriggerCrudService> create(TriggerCrudService.class).runForDate(new DefaultAsyncCallback<Run>() {
            @Override
            public void onSuccess(Run result) {
                AppSite.getPlaceController().goTo(
                        AppSite.getHistoryMapper().createPlace(AdminSiteMap.Management.Run.class).formViewerPlace(result.getPrimaryKey()));
            }
        }, EntityFactory.createIdentityStub(Trigger.class, getEntityId()), date);
    }
}
