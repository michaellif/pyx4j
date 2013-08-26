/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TermsView;
import com.propertyvista.portal.rpc.portal.services.PortalVistaTermsService;

public class VistaTermsActivity extends AbstractActivity {

    private final TermsView view;

    private final PortalVistaTermsService service;

    public VistaTermsActivity() {
        service = GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class);
        view = PortalSite.getViewFactory().instantiate(TermsView.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        service.getVistaTerms(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                view.populate(result);
                panel.setWidget(view);
            }
        });
        panel.setWidget(view);
    }

}
