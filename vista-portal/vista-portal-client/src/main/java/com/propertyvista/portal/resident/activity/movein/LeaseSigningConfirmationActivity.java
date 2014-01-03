/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.movein;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.movein.LeaseSigningConfirmationView;
import com.propertyvista.portal.resident.ui.movein.LeaseSigningConfirmationView.LeaseSigningConfirmationPresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementConfirmationDTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseSigningCrudService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class LeaseSigningConfirmationActivity extends SecurityAwareActivity implements LeaseSigningConfirmationPresenter {

    private final LeaseSigningConfirmationView view;

    public LeaseSigningConfirmationActivity(AppPlace place) {
        this.view = ResidentPortalSite.getViewFactory().getView(LeaseSigningConfirmationView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        GWT.<LeaseSigningCrudService> create(LeaseSigningCrudService.class).retrieveLeaseAgreementDocument(
                new DefaultAsyncCallback<LeaseTermAgreementDocument>() {
                    @Override
                    public void onSuccess(LeaseTermAgreementDocument result) {
                        LeaseAgreementConfirmationDTO confirmatioin = EntityFactory.create(LeaseAgreementConfirmationDTO.class);
                        confirmatioin.agreementDocument().set(result);
                        view.populate(confirmatioin);
                    }
                });
    }

    @Override
    public void back() {
        // TODO Auto-generated method stub
    }

}
