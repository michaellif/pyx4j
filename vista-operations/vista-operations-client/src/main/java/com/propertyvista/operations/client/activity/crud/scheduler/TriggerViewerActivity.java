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
package com.propertyvista.operations.client.activity.crud.scheduler;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.crud.AdminViewerActivity;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerViewerView;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.TriggerDTO;
import com.propertyvista.operations.rpc.services.scheduler.RunCrudService;
import com.propertyvista.operations.rpc.services.scheduler.TriggerCrudService;

public class TriggerViewerActivity extends AdminViewerActivity<TriggerDTO> implements TriggerViewerView.Presenter {

    private final Presenter<Run> runLister;

    public TriggerViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(TriggerViewerView.class), GWT.<TriggerCrudService> create(TriggerCrudService.class));

        runLister = new ListerController<Run>(((TriggerViewerView) getView()).getRunListerView(), GWT.<RunCrudService> create(RunCrudService.class), Run.class);
    }

    @Override
    protected void onPopulateSuccess(TriggerDTO result) {
        super.onPopulateSuccess(result);

        runLister.setParent(result.getPrimaryKey());
        runLister.populate();
    }

    @Override
    public void runImmediately() {
        GWT.<TriggerCrudService> create(TriggerCrudService.class).runImmediately(new DefaultAsyncCallback<Run>() {
            @Override
            public void onSuccess(Run result) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.TriggerRun().formViewerPlace(result.getPrimaryKey()));
            }
        }, EntityFactory.createIdentityStub(TriggerDTO.class, getEntityId()));
    }

    @Override
    public void runForDate(ScheduleDataDTO date) {
        GWT.<TriggerCrudService> create(TriggerCrudService.class).runForDate(new DefaultAsyncCallback<Run>() {
            @Override
            public void onSuccess(Run result) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.TriggerRun().formViewerPlace(result.getPrimaryKey()));
            }
        }, EntityFactory.createIdentityStub(TriggerDTO.class, getEntityId()), date.date().getValue());
    }
}
