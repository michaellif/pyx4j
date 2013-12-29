/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-22
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.scheduler;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.crud.AdminViewerActivity;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunDataViewerView;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.rpc.services.scheduler.RunDataCrudService;

public class RunDataViewerActivity extends AdminViewerActivity<RunData> implements RunDataViewerView.Presenter {

    public RunDataViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(RunDataViewerView.class), GWT.<RunDataCrudService> create(RunDataCrudService.class));
    }

    @Override
    public void cancelDataRun() {
        ((RunDataCrudService) getService()).cancelDataRun(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        }, EntityFactory.createIdentityStub(RunData.class, getEntityId()));
    }
}
