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
package com.propertyvista.crm.client.activity.crud.lease.application;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseViewerActivityBase;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO.Action;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationCrudService;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;

public class LeaseApplicationViewerActivity extends LeaseViewerActivityBase<LeaseApplicationDTO> implements LeaseApplicationViewerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseApplicationViewerActivity.class);

    @SuppressWarnings("unchecked")
    public LeaseApplicationViewerActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseApplicationViewerView.class), (AbstractCrudService<LeaseApplicationDTO>) GWT
                .create(LeaseApplicationCrudService.class));
    }

    // Actions:

    @Override
    public void startOnlineApplication() {
        ((LeaseApplicationCrudService) service).startOnlineApplication(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                ((LeaseApplicationViewerView) view).reportStartOnlineApplicationSuccess();
                populate();

            }
        }, entityId);
    }

    @Override
    public void inviteUsers(List<LeaseParticipant> users) {
        ((LeaseApplicationCrudService) service).inviteUsers(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String message) {
                populate();
                ((LeaseApplicationViewerView) view).reportInviteUsersActionResult(message);
            }
        }, entityId, new Vector<LeaseParticipant>(users));
    }

    @Override
    public void applicationAction(final LeaseApplicationActionDTO action) {
        ((LeaseApplicationCrudService) service).applicationAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                if (action.action().getValue() == Action.Approve) {
                    entityId = entityId.asCurrentKey();
                }
                populate();
            }
        }, action);
    }
}
