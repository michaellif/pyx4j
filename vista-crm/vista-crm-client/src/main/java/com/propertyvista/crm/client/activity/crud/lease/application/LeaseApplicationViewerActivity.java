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

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.HandledErrorAsyncCallback;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseViewerActivityBase;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO.Action;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;

public class LeaseApplicationViewerActivity extends LeaseViewerActivityBase<LeaseApplicationDTO> implements LeaseApplicationViewerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseApplicationViewerActivity.class);

    private BigDecimal creditCheckAmount;

    private LeaseApplicationDTO currentValue;

    @SuppressWarnings("unchecked")
    public LeaseApplicationViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(LeaseApplicationViewerView.class), (AbstractCrudService<LeaseApplicationDTO>) GWT
                .create(LeaseApplicationViewerCrudService.class));
    }

    @Override
    protected void populateView(LeaseApplicationDTO result) {
        super.populateView(result);

        currentValue = result;

        creditCheckAmount = result.leaseApproval().rentAmount().getValue();
    }

    // Views:

    @Override
    public void viewLease() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Lease().formViewerPlace(currentValue.getPrimaryKey()));
    }

    // Actions:

    @Override
    public void startOnlineApplication() {
        ((LeaseApplicationViewerCrudService) getService()).startOnlineApplication(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                ((LeaseApplicationViewerView) getView()).reportStartOnlineApplicationSuccess();
                populate();

            }
        }, getEntityId());
    }

    @Override
    public void inviteUsers(List<LeaseTermParticipant<?>> users) {
        ((LeaseApplicationViewerCrudService) getService()).inviteUsers(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String message) {
                populate();
                ((LeaseApplicationViewerView) getView()).reportInviteUsersActionResult(message);
            }
        }, getEntityId(), new Vector<LeaseTermParticipant<?>>(users));
    }

    @Override
    public void creditCheck(List<LeaseTermParticipant<?>> users) {
        ((LeaseApplicationViewerCrudService) getService()).creditCheck(new HandledErrorAsyncCallback<String>() {
            @Override
            public void onSuccess(String message) {
                populate();
                ((LeaseApplicationViewerView) getView()).reportCreditCheckActionResult(message);
            }
        }, getEntityId(), creditCheckAmount, new Vector<LeaseTermParticipant<?>>(users));
    }

    @Override
    public void applicationAction(final LeaseApplicationActionDTO action) {
        ((LeaseApplicationViewerCrudService) getService()).applicationAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                if (action.action().getValue() == Action.Approve) {
                    setEntityIdAsCurrentKey();
                    ((LeaseApplicationViewerView) getView()).reportApplicationApprovalSuccess();
                }
                populate();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (action.action().getValue() == Action.Approve & (caught instanceof UserRuntimeException)) {
                    ((LeaseApplicationViewerView) getView()).reportApplicationApprovalFailure((UserRuntimeException) caught);
                } else {
                    super.onFailure(caught);
                }
            }
        }, action);
    }

    @Override
    public void getCreditCheckServiceStatus(final AsyncCallback<PmcEquifaxStatus> callback) {
        ((LeaseApplicationViewerCrudService) getService()).getCreditCheckServiceStatus(new DefaultAsyncCallback<PmcEquifaxStatus>() {
            @Override
            public void onSuccess(PmcEquifaxStatus result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void isCreditCheckViewAllowed(final AsyncCallback<VoidSerializable> callback) {
        ((LeaseApplicationViewerCrudService) getService()).isCreditCheckViewAllowed(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                callback.onSuccess(result);
            }
        });
    }
}
