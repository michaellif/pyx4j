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
package com.propertyvista.portal.resident.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.TermsView;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.PortalVistaTermsService;

public class VistaTermsActivity extends AbstractActivity {

    private final TermsView view;

    private final Place place;

    public VistaTermsActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().instantiate(TermsView.class);
        this.place = place;
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        if (place instanceof ResidentPortalSiteMap.TermsAndConditions) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPortalTerms(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    view.populate(result);
                    panel.setWidget(view);
                }
            });
        } else if (place instanceof ResidentPortalSiteMap.BillingPolicy) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPortalBillingPolicy(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    view.populate(result);
                    panel.setWidget(view);
                }
            });
        } else if (place instanceof ResidentPortalSiteMap.CreditCardPolicy) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPortalCcPolicy(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    view.populate(result);
                    panel.setWidget(view);
                }
            });
        } else if (place instanceof ResidentPortalSiteMap.PadPolicy) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPortalPadPolicy(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    view.populate(result);
                    panel.setWidget(view);
                }
            });
        }

        panel.setWidget(view);
    }
}
