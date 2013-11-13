/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity;

import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.LeaseContextSelectionView;
import com.propertyvista.portal.rpc.portal.resident.services.LeaseContextSelectionService;
import com.propertyvista.portal.rpc.portal.shared.dto.LeaseContextChoiceDTO;

public class LeaseContextSelectionActivity extends AbstractActivity implements LeaseContextSelectionView.Presenter {

    private static final I18n i18n = I18n.get(LeaseContextSelectionActivity.class);

    private final LeaseContextSelectionView view;

    private final LeaseContextSelectionService service;

    public LeaseContextSelectionActivity() {
        this.service = GWT.<LeaseContextSelectionService> create(LeaseContextSelectionService.class);
        this.view = ResidentPortalSite.getViewFactory().getView(LeaseContextSelectionView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        service.getLeaseContextChoices(new DefaultAsyncCallback<Vector<LeaseContextChoiceDTO>>() {
            @Override
            public void onSuccess(Vector<LeaseContextChoiceDTO> result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void setLeaseContext() {
        Lease leaseIdStub = view.getSelectedLeaseIdStub();

        if (leaseIdStub != null) {
            service.setLeaseContext(new DefaultAsyncCallback<AuthenticationResponse>() {
                @Override
                public void onSuccess(AuthenticationResponse result) {
                    ClientContext.authenticated(result);
                    AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
                }

            }, leaseIdStub);
        } else {
            view.showMessage(i18n.tr("Please choose a lease"));
        }
    }

}
