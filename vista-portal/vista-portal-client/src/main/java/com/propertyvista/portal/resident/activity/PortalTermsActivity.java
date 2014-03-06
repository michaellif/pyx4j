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
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.resources.VistaResources;
import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.ResidentPortalTerms;
import com.propertyvista.portal.rpc.portal.shared.services.PortalTermsAndPoliciesService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.TermsView;
import com.propertyvista.shared.rpc.LegalTermTO;

public class PortalTermsActivity extends AbstractActivity {

    private final TermsView view;

    private final Place place;

    public PortalTermsActivity(Place place) {
        view = PortalSite.getViewFactory().getView(TermsView.class);
        this.place = place;
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        DefaultAsyncCallback<LegalTermTO> callback = new DefaultAsyncCallback<LegalTermTO>() {
            @Override
            public void onSuccess(LegalTermTO result) {
                view.populate(result.caption().getValue(), result.content().getValue());
                panel.setWidget(view);
            }
        };

        if (place instanceof PortalSiteMap.PortalTerms.VistaTermsAndConditions) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.PVResidentPortalTermsAndConditions);
        } else if (place instanceof PortalSiteMap.PortalTerms.VistaPrivacyPolicy) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.PVResidentPortalPrivacyPolicy);
        } else if (place instanceof PortalSiteMap.PortalTerms.PmcTermsAndConditions) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.PMCResidentPortalTermsAndConditions);
        } else if (place instanceof PortalSiteMap.PortalTerms.PmcPrivacyPolicy) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.PMCResidentPortalPrivacyPolicy);

        } else if (place instanceof PortalSiteMap.PortalTerms.BillingTerms) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback, TermsAndPoliciesType.TenantBillingTerms);
        } else if (place instanceof PortalSiteMap.PortalTerms.WebPaymentFeeTerms) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.ResidentPortalWebPaymentFeeTerms);

        } else if (place instanceof ResidentPortalTerms.PreauthorizedPaymentTerms) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.TenantPreauthorizedPaymentECheckTerms);
        } else if (place instanceof ResidentPortalTerms.CreditCardPolicy) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.TenantPreauthorizedPaymentCardTerms);
        } else if (place instanceof ResidentPortalTerms.TenantSurePreAuthorizedPaymentTerms) {
            GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTerm(callback,
                    TermsAndPoliciesType.TenantSurePreAuthorizedPaymentsAgreement);

        } else if (place instanceof PortalSiteMap.PortalTerms.DirectBankingInstruction) {
            LegalTermTO result = EntityFactory.create(LegalTermTO.class);
            result.caption().setValue("Direct Banking Instruction");
            result.content().setValue(VistaResources.INSTANCE.directBankingInstruction().getText());
            callback.onSuccess(result);
        }

        panel.setWidget(view);
    }
}
