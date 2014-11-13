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
package com.propertyvista.crm.client.activity.crud.billing.transfer;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.transfer.AggregatedTransferViewerView;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferViewerActivity extends CrmViewerActivity<AggregatedTransfer> implements AggregatedTransferViewerView.Presenter {

    public AggregatedTransferViewerActivity(CrudAppPlace place) {
        super(AggregatedTransfer.class, place, CrmSite.getViewFactory().getView(AggregatedTransferViewerView.class), GWT
                .<AggregatedTransferCrudService> create(AggregatedTransferCrudService.class));
    }

    @Override
    public void cancelAction() {
        ((AggregatedTransferCrudService) getService()).cancelTransactions(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, EntityFactory.createIdentityStub(AggregatedTransfer.class, getEntityId()));
    }
}