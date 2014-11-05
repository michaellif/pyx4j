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
package com.propertyvista.operations.client.activity.crud.simulator.pad;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.activity.ListerController;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeLister.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.crud.AdminViewerActivity;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadSimFileViewerView;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimBatch;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.services.simulator.PadSimBatchCrudService;
import com.propertyvista.operations.rpc.services.simulator.PadSimFileCrudService;

public class PadSimFileViewerActivity extends AdminViewerActivity<PadSimFile> implements PadSimFileViewerView.Presenter {

    private final Presenter<PadSimBatch> batchLister;

    public PadSimFileViewerActivity(CrudAppPlace place) {
        super(PadSimFile.class, place, OperationsSite.getViewFactory().getView(PadSimFileViewerView.class), GWT
                .<AbstractCrudService<PadSimFile>> create(PadSimFileCrudService.class));

        batchLister = new ListerController<PadSimBatch>(PadSimBatch.class, ((PadSimFileViewerView) getView()).getBatchListerView(),
                GWT.<AbstractCrudService<PadSimBatch>> create(PadSimBatchCrudService.class));
    }

    @Override
    protected void onPopulateSuccess(PadSimFile result) {
        super.onPopulateSuccess(result);

        batchLister.setParent(result.getPrimaryKey());
        batchLister.populate();
    }

    @Override
    public void replyAcknowledgment() {
        ((PadSimFileCrudService) getService()).replyAcknowledgment(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, EntityFactory.createIdentityStub(PadSimFile.class, getEntityId()));
    }

    @Override
    public void replyReconciliation() {
        ((PadSimFileCrudService) getService()).replyReconciliation(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, EntityFactory.createIdentityStub(PadSimFile.class, getEntityId()));
    }

    @Override
    public void replyReturns() {
        ((PadSimFileCrudService) getService()).replyReturns(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, EntityFactory.createIdentityStub(PadSimFile.class, getEntityId()));
    }

    @Override
    public void createReturnReconciliation() {
        ((PadSimFileCrudService) getService()).createReturnReconciliation(new DefaultAsyncCallback<PadSimFile>() {
            @Override
            public void onSuccess(PadSimFile result) {
                CrudAppPlace place = new OperationsSiteMap.Simulator.PadSimulation.PadSimFile();
                place.formViewerPlace(result.getPrimaryKey());
                AppSite.getPlaceController().goTo(place);
            }
        }, EntityFactory.createIdentityStub(PadSimFile.class, getEntityId()));

    }
}
