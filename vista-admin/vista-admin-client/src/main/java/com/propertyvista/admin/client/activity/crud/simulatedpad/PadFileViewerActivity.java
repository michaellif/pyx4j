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
package com.propertyvista.admin.client.activity.crud.simulatedpad;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.simulatedpad.PadFileViewerView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.admin.rpc.services.sim.PadSimBatchCrudService;
import com.propertyvista.admin.rpc.services.sim.PadSimFileCrudService;

public class PadFileViewerActivity extends AdminViewerActivity<PadSimFile> implements PadFileViewerView.Presenter {

    private final Presenter<PadSimBatch> batchLister;

    @SuppressWarnings("unchecked")
    public PadFileViewerActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(PadFileViewerView.class), (AbstractCrudService<PadSimFile>) GWT.create(PadSimFileCrudService.class));

        batchLister = new ListerActivityBase<PadSimBatch>(place, ((PadFileViewerView) getView()).getBatchListerView(),
                (AbstractCrudService<PadSimBatch>) GWT.create(PadSimBatchCrudService.class), PadSimBatch.class);
    }

    @Override
    protected void onPopulateSuccess(PadSimFile result) {
        super.onPopulateSuccess(result);

        batchLister.setParent(result.getPrimaryKey());
        batchLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) batchLister).onStop();
        super.onStop();
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
}
