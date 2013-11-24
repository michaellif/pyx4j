/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.mvp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.common.client.site.VistaBrowserRequirments;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.ob.client.views.OnboardingViewFactory;
import com.propertyvista.ob.client.views.PmcAccountCreationRequestView;
import com.propertyvista.ob.rpc.OnboardingSiteMap;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;
import com.propertyvista.ob.rpc.services.OnboardingPublicActivationService;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;
import com.propertyvista.shared.i18n.CompiledLocale;

public class PmcAccountCreationRequestActivity extends AbstractActivity implements PmcAccountCreationRequestView.Presenter {

    private static final I18n i18n = I18n.get(PmcAccountCreationCompleteActivity.class);

    private final AppPlace place;

    private final PmcAccountCreationRequestView view;

    private final OnboardingPublicActivationService onboardingPublicActivationService;

    private final PmcRegistrationService pmcRegistrationService;

    public PmcAccountCreationRequestActivity(AppPlace place) {
        this.place = place;
        view = OnboardingViewFactory.instance(PmcAccountCreationRequestView.class);
        onboardingPublicActivationService = GWT.<OnboardingPublicActivationService> create(OnboardingPublicActivationService.class);
        pmcRegistrationService = GWT.<PmcRegistrationService> create(PmcRegistrationService.class);
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        view.setEnabled(VistaBrowserRequirments.isBrowserCompatibleCrm());
        view.setMessage(VistaBrowserRequirments.isBrowserCompatibleCrm() ? "" : "<div>" + i18n.tr("Your Browser is Not Supported!") + "</div>" + "<div>"
                + i18n.tr("Please use the most recent version of Internet&nbsp;Explorer, Google&nbsp;Chrome, Firefox or Apple&nbsp;Safari.") + "</div>");

        PmcAccountCreationRequest req = EntityFactory.create(PmcAccountCreationRequest.class);

        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        if (CompiledLocale.en_GB.equals(currentLocale)) {
            req.countryOfOperation().setValue(CountryOfOperation.UK);
        } else if (CompiledLocale.en_US.equals(currentLocale)) {
            req.countryOfOperation().setValue(CountryOfOperation.US);
        } else {
            req.countryOfOperation().setValue(CountryOfOperation.Canada);
        }
        view.setPmcAccountCreationRequest(req);
        panel.setWidget(view);
    }

    @Override
    public void checkDns(AsyncCallback<Boolean> callback, String dnsName) {
        onboardingPublicActivationService.checkDNSAvailability(callback, dnsName);
    }

    @Override
    public void openTerms() {
        try {
            Window.open(AppPlaceInfo.absoluteUrl(GWT.getHostPageBaseURL(), false, OnboardingSiteMap.PmcAccountTerms.class), "_blank",
                    BrowserType.isIE() ? "status=1,toolbar=1,location=1,resizable=1,scrollbars=1" : null);
        } catch (Throwable popupBlocked) {
            AppSite.getPlaceController().goTo(new OnboardingSiteMap.PmcAccountTerms());
        }
    }

    @Override
    public void createAccount() {
        if (ClientContext.isAuthenticated()) {
            startAccountCreation();
        } else {
            ClientContext.authenticate(GWT.<OnboardingAuthenticationService> create(OnboardingAuthenticationService.class), null,
                    new DefaultAsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            startAccountCreation();
                        }
                    });
        }

    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub
    }

    @Override
    @Deprecated
    public void refresh() {
        // TODO Auto-generated method stub

    }

    private void startAccountCreation() {
        pmcRegistrationService.createAccount(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorellationId) {
                OnboardingUserVisit visit = ClientContext.getUserVisit(OnboardingUserVisit.class);
                visit.accountCreationDeferredCorrelationId = deferredCorellationId;
                goToProgressPlace();
            }
        }, view.getPmcAccountCreationRequest());
    }

    private void goToProgressPlace() {
        AppSite.getPlaceController().goTo(new OnboardingSiteMap.PmcAccountCreationProgress());
    }

}
