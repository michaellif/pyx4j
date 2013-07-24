/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.crud.AdminViewerActivity;
import com.propertyvista.operations.client.ui.crud.pmc.EquifaxApprovalView;
import com.propertyvista.operations.rpc.EquifaxSetupRequestDTO;
import com.propertyvista.operations.rpc.services.EquifaxApprovalCrudService;

public class EquifaxApprovalViewActivity extends AdminViewerActivity<EquifaxSetupRequestDTO> implements EquifaxApprovalView.Presenter {

    public EquifaxApprovalViewActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().instantiate(EquifaxApprovalView.class), GWT
                .<AbstractCrudService<EquifaxSetupRequestDTO>> create(EquifaxApprovalCrudService.class));
    }

    @Override
    public void approveAndSendToEquifax() {
        ((EquifaxApprovalCrudService) getService()).applyAndSendToEquifax(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                ((EquifaxApprovalView) getView()).reportResult("Approved Successfully");
            }
        });
    }

    @Override
    public void reject() {
        ((EquifaxApprovalCrudService) getService()).reject(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                ((EquifaxApprovalView) getView()).reportResult("Rejected Successfully");
            }
        });

    }

    @Override
    protected void onPopulateSuccess(EquifaxSetupRequestDTO result) {
        ((EquifaxApprovalView) getView()).setEnableApprovalControls(result.status().getValue() == PmcEquifaxStatus.PendingVistaApproval);
        super.onPopulateSuccess(result);
    }

    @Override
    public void confirmSuccess() {
        ((EquifaxApprovalView) getView()).setEnableApprovalControls(false);
    }

}
