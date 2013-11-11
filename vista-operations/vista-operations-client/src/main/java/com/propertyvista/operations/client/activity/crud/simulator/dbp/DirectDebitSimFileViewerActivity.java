/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulator.dbp;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimFileViewerView;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile.DirectDebitSimFileStatus;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimFileCrudService;

public class DirectDebitSimFileViewerActivity extends AbstractViewerActivity<DirectDebitSimFile> implements DirectDebitSimFileViewerView.Presenter {

    private DirectDebitSimFile simFile;

    public DirectDebitSimFileViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(DirectDebitSimFileViewerView.class), GWT
                .<DirectDebitSimFileCrudService> create(DirectDebitSimFileCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return simFile != null && simFile.status().getValue() != DirectDebitSimFileStatus.Sent;
    }

    @Override
    public void send() {
        ((DirectDebitSimFileCrudService) getService()).send(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    getDirectDebitSimFileViewerView().reportSendResult(true, caught.getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, EntityFactory.createIdentityStub(DirectDebitSimFile.class, getEntityId()));
    }

    @Override
    protected void onPopulateSuccess(DirectDebitSimFile simFile) {
        this.simFile = simFile;
        super.onPopulateSuccess(simFile);
        getDirectDebitSimFileViewerView().setEnableSendAction(simFile != null && simFile.status().getValue() != DirectDebitSimFileStatus.Sent);
    }

    private DirectDebitSimFileViewerView getDirectDebitSimFileViewerView() {
        return ((DirectDebitSimFileViewerView) getView());
    }

}
