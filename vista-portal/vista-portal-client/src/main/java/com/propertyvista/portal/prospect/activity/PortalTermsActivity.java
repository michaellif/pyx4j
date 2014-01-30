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
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap.ProspectPortalTerms;
import com.propertyvista.portal.rpc.portal.shared.services.PortalVistaTermsService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.TermsView;
import com.propertyvista.shared.rpc.LegalTermsTO;

public class PortalTermsActivity extends AbstractActivity {

    private final TermsView view;

    private final Place place;

    public PortalTermsActivity(Place place) {
        view = PortalSite.getViewFactory().getView(TermsView.class);
        this.place = place;
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        DefaultAsyncCallback<LegalTermsTO> callback = new DefaultAsyncCallback<LegalTermsTO>() {
            @Override
            public void onSuccess(LegalTermsTO result) {
                view.populate(result.caption().getValue(), result.content().getValue());
                panel.setWidget(view);
            }
        };

        if (place instanceof PortalSiteMap.PortalTerms.PortalTermsAndConditions) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPVProspectPortalTermsAndConditions(callback);
        } else if (place instanceof PortalSiteMap.PortalTerms.PortalPrivacyPolicy) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPVProspectPortalPrivacyPolicy(callback);
        } else if (place instanceof PortalSiteMap.PortalTerms.PMCTermsAndConditions) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPMCProspectPortalTermsAndConditions(callback);
        } else if (place instanceof PortalSiteMap.PortalTerms.PMCPrivacyPolicy) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPMCProspectPortalPrivacyPolicy(callback);

        } else if (place instanceof PortalSiteMap.PortalTerms.BillingTerms) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getTenantBillingTerms(callback);
        } else if (place instanceof ProspectPortalTerms.ApplicantTermsAndConditions) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPVProspectPortalTermsAndConditions(callback);
        } else if (place instanceof ProspectPortalTerms.RentalCriteriaGuidelines) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getPMCProspectPortalTermsAndConditions(callback);
        }

        panel.setWidget(view);
    }
}
