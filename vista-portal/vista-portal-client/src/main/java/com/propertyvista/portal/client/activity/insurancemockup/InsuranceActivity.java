/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.insurancemockup;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.client.ui.residents.insurancemockup.InsuranceView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.dto.insurancemockup.TenantInsuranceDTO;
import com.propertyvista.portal.rpc.portal.services.InsuranceService;

public class InsuranceActivity extends AbstractActivity implements InsuranceView.Presenter {

    private final InsuranceView view;

    private final InsuranceService service;

    public InsuranceActivity() {
        service = GWT.<InsuranceService> create(InsuranceService.class);
        view = PortalViewFactory.instance(InsuranceView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
        populate();

    }

    @Override
    public void populate() {
        service.retrieveInsurance(new DefaultAsyncCallback<TenantInsuranceDTO>() {

            @Override
            public void onSuccess(TenantInsuranceDTO result) {
                view.populate(result);
            }
        });
    }

}
