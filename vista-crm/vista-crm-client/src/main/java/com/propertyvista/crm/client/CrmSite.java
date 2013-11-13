/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.gwt.commons.GoogleAnalytics;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SessionInactiveEvent;
import com.pyx4j.security.client.SessionInactiveHandler;
import com.pyx4j.security.client.SessionMonitor;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.SingletonViewFactory;
import com.pyx4j.site.client.events.NotificationEvent;
import com.pyx4j.site.client.events.NotificationHandler;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.site.VistaBrowserRequirments;
import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.CrmRootPane;
import com.propertyvista.crm.client.ui.HeaderViewImpl;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.policies.CrmPolicyRetrieveService;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.shared.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;

public class CrmSite extends VistaSite {

    private static final Logger log = LoggerFactory.getLogger(CrmSite.class);

    private static final I18n i18n = I18n.get(CrmSite.class);

    public CrmSite() {
        super("vista-crm", CrmSiteMap.class, new SingletonViewFactory(), new CrmSiteAppPlaceDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());

        getHistoryHandler().register(getPlaceController(), getEventBus(), AppPlace.NOWHERE);

        HTML feedbackWidgetContainer = new HTML();
        feedbackWidgetContainer.getElement().setAttribute("id", "feedback_widget_container"); //getSatisfaction button container
        RootLayoutPanel.get().add(feedbackWidgetContainer); //must be done before add(contentPanel) else the container blocks all interaction with site

        setRootPane(new CrmRootPane());

        CrmEntityMapper.init();

        SessionInactiveDialog.register();
        SessionMonitor.addSessionInactiveHandler(new SessionInactiveHandler() {
            @Override
            public void onSessionInactive(SessionInactiveEvent event) {
                ClientContext.logout((AuthenticationService) GWT.create(CrmAuthenticationService.class), null);
            }
        });

        // subscribe to UserMessageEvent fired from VistaUnrecoverableErrorHandler
        getEventBus().addHandler(NotificationEvent.getType(), new NotificationHandler() {
            @Override
            public void onNotification(NotificationEvent event) {
                getPlaceController().showNotification(event.getNotification());
            }
        });

        if (verifyBrowserCompatibility()) {
            initialize();
        }
    }

    @Override
    protected boolean isBrowserCompatible() {
        return VistaBrowserRequirments.isBrowserCompatibleCrm();
    }

    private void initialize() {
        initSiteTheme();
        ClientPolicyManager.initialize(GWT.<PolicyRetrieveService> create(CrmPolicyRetrieveService.class));

        AppSite.getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {

            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                if (SecurityController.checkBehavior(VistaBasicBehavior.CRM)) {
                    if (VistaFeaturesCustomizationClient.isGoogleAnalyticDisableForEmployee() && hasVistaSupportCookie()) {
                        // Do not track Vista Support Employee
                    } else {
                        GoogleAnalytics.setGoogleAnalyticsTracker(ClientContext.getGoogleAnalyticsKey());
                        GoogleAnalytics.track("login");
                    }
                }
            }

        });
    }

    private void initSiteTheme() {
        Cookies.setCookie("locale", ClientNavigUtils.getCurrentLocale().name());
        SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                hideLoadingIndicator();
                HeaderViewImpl.temporaryWayToSetTitle(descriptor.siteTitles().crmHeader().getStringView(), descriptor.logoAvalable().isBooleanTrue());
                Window.setTitle(i18n.tr("Property Vista") + " - " + descriptor.siteTitles().crmHeader().getStringView());
                StyleManager.installTheme(new CrmTheme(), new VistaPalette(descriptor.palette()));
                VistaFeaturesCustomizationClient.setVistaFeatures(descriptor.features());
                VistaFeaturesCustomizationClient.setGoogleAnalyticDisableForEmployee(descriptor.isGoogleAnalyticDisableForEmployee().getValue());
                VistaFeaturesCustomizationClient.enviromentTitleVisible = descriptor.enviromentTitleVisible().getValue(Boolean.TRUE);
                ClientNavigUtils.setCountryOfOperationLocale();
                obtainAuthenticationData();
            }

            @Override
            public void onFailure(Throwable caught) {
                hideLoadingIndicator();
                StyleManager.installTheme(new CrmTheme(), VistaPalette.getServerUnavailablePalette());
                super.onFailure(caught);
            }
        }, ClientNavigUtils.getCurrentLocale());

    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(((CrmAuthenticationService) GWT.create(CrmAuthenticationService.class)), new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                AppSite.getHistoryHandler().handleCurrentHistory();
            }

            @Override
            public void onFailure(Throwable caught) {
                AppSite.getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }

    static public AppPlace getSystemDashboardPlace() {
        return new CrmSiteMap.Dashboard.View().formPlace(new Key(-1));
    }

    /**
     * Used to disable tracking of Vista Employee in customer application
     */
    private boolean hasVistaSupportCookie() {
        return CommonsStringUtils.isStringSet(Cookies.getCookie(DeploymentConsts.vistaEmployeeCookie));
    }
}
