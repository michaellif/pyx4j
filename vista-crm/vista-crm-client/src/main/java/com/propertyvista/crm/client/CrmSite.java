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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.GoogleAnalytics;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.security.client.SessionInactiveEvent;
import com.pyx4j.security.client.SessionInactiveHandler;
import com.pyx4j.security.client.SessionMonitor;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.common.client.events.UserMessageEvent;
import com.propertyvista.common.client.events.UserMessageHandler;
import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.site.CrmSiteBrowserRequirments;
import com.propertyvista.common.client.site.Message;
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
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;

public class CrmSite extends VistaSite {

    private static final Logger log = LoggerFactory.getLogger(CrmSite.class);

    private static final I18n i18n = I18n.get(CrmSite.class);

    public CrmSite() {
        super("vista-crm", CrmSiteMap.class, new CrmSiteAppPlaceDispatcher());
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
        getEventBus().addHandler(UserMessageEvent.getType(), new UserMessageHandler() {
            @Override
            public void onUserMessage(UserMessageEvent event) {
                setUserMessage(event.getUserMessage());
                getPlaceController().goToUserMessagePlace();
            }
        });

        if (verifyBrowserCompatibility()) {
            initialize();
        }
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
    }

    private void initialize() {
        initSiteTheme();
        ClientPolicyManager.initialize(GWT.<PolicyRetrieveService> create(CrmPolicyRetrieveService.class));

        AppSite.getEventBus().addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {

            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
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

    private boolean verifyBrowserCompatibility() {
        if (!CrmSiteBrowserRequirments.isBrowserCompatible()) {
            hideLoadingIndicator();
            log.warn("Unsupported Browser UserAgent [{}]", BrowserType.getUserAgent());
            Window.alert(i18n.tr("Unsupported Browser")
                    + "\n"
                    + i18n.tr("Your current Browser Version will restrict the functionality of this application.\n"
                            + "Please use an updated version of Internet Explorer.\n"
                            + "This application will also work with current versions of Mozilla Firefox, Google Chrome or Apple Safari"));
            return false;
        } else if (BrowserType.isIE() && BrowserType.isIENative() && !isIEDocumentModeComatible(9)) {
            hideLoadingIndicator();
            Window.alert(i18n.tr("Unsupported Browser Compatibility Mode")
                    + "\n"
                    + i18n.tr("Your current Browser Compatibility Mode settings will restrict the functionality of this application.\n"
                            + "Please change setting 'Document Mode' to IE9 standards.\n"
                            + "This application will also work with current versions of Mozilla Firefox, Google Chrome or Apple Safari"));
            return false;
        }
        return true;
    }

    /**
     * Used to disable tracking of Vista Employee in customer application
     */
    private boolean hasVistaSupportCookie() {
        return CommonsStringUtils.isStringSet(Cookies.getCookie(DeploymentConsts.vistaEmployeeCookie));
    }
}
