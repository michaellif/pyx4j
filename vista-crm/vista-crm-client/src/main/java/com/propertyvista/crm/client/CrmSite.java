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
 */
package com.propertyvista.crm.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.entity.security.DataModelPermission;
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
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.SingletonViewFactory;
import com.pyx4j.site.client.events.NotificationEvent;
import com.pyx4j.site.client.events.NotificationHandler;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.common.client.WalkMe;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.site.VistaBrowserRequirments;
import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.crm.client.activity.CrmClientCommunicationManager;
import com.propertyvista.crm.client.themes.CrmNoServerPalette;
import com.propertyvista.crm.client.themes.CrmPalette;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.CrmRootPane;
import com.propertyvista.crm.client.ui.HeaderViewImpl;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.ContentManagement;
import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.crm.rpc.services.admin.ac.CrmContentManagementAccess;
import com.propertyvista.crm.rpc.services.policies.CrmPolicyRetrieveService;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.shared.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;

public class CrmSite extends VistaSite {

    private static final I18n i18n = I18n.get(CrmSite.class);

    public CrmSite() {
        super("vista-crm", CrmSiteMap.class, new SingletonViewFactory(), new CrmSiteAppPlaceDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());

        getHistoryHandler().register(getPlaceController(), getEventBus());

        HTML feedbackWidgetContainer = new HTML();
        feedbackWidgetContainer.getElement().setAttribute("id", "feedback_widget_container"); //getSatisfaction button container
        RootLayoutPanel.get().add(feedbackWidgetContainer); //must be done before add(contentPanel) else the container blocks all interaction with site

        setRootPane(new CrmRootPane());

        CrmEntityMapper.init();

        ClientContext.setAuthenticationService(GWT.<AuthenticationService> create(CrmAuthenticationService.class));

        SessionInactiveDialog.register();
        SessionMonitor.addSessionInactiveHandler(new SessionInactiveHandler() {
            @Override
            public void onSessionInactive(SessionInactiveEvent event) {
                ClientContext.logout(null);
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
                if (SecurityController.check(VistaAccessGrantedBehavior.CRM)) {
                    if (VistaFeaturesCustomizationClient.isGoogleAnalyticDisableForEmployee() && hasVistaSupportCookie()) {
                        // Do not track Vista Support Employee
                    } else {
                        GoogleAnalytics.setGoogleAnalyticsTracker(ClientContext.getGoogleAnalyticsKey());
                        GoogleAnalytics.track("login");
                    }
                }
            }

        });

        CrmClientCommunicationManager.instance();
    }

    private void initSiteTheme() {
        Cookies.setCookie("locale", ClientLocaleUtils.getCurrentLocale().name());
        SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                hideLoadingIndicator();
                HeaderViewImpl.temporaryWayToSetTitle(descriptor.siteTitles().crmHeader().getStringView(), descriptor.logoAvalable().getValue(false));
                Window.setTitle(i18n.tr("Property Vista") + " - " + descriptor.siteTitles().crmHeader().getStringView());
                StyleManager.installTheme(new CrmTheme(), new CrmPalette(descriptor));
                VistaFeaturesCustomizationClient.setVistaFeatures(descriptor.features());
                VistaFeaturesCustomizationClient.setGoogleAnalyticDisableForEmployee(descriptor.isGoogleAnalyticDisableForEmployee().getValue());
                VistaFeaturesCustomizationClient.enviromentTitleVisible = descriptor.enviromentTitleVisible().getValue(Boolean.TRUE);
                ClientLocaleUtils.setCountryOfOperationLocale();
                obtainAuthenticationData();
                if (descriptor.walkMeEnabled().getValue(false)) {
                    WalkMe.enable(descriptor.walkMeJsAPIUrl().getValue());
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                hideLoadingIndicator();
                StyleManager.installTheme(new CrmTheme(), new CrmNoServerPalette());
                super.onFailure(caught);
            }
        }, VistaApplication.crm, ClientLocaleUtils.getCurrentLocale());

    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

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

    static public AppPlace getDefaultPlace() {
        if (SecurityController.check(DataModelPermission.permissionRead(DashboardMetadata.class))) {
            return getSystemDashboardPlace();
        }
        return new CrmSiteMap.Welcome();
    }

    static public AppPlace getSystemDashboardPlace() {
        return new CrmSiteMap.Dashboard.View().formPlace(new Key(-1));
    }

    static public AppPlace getAvalableAdministrationPlace() {
        if (SecurityController.check(DataModelPermission.permissionRead(PmcCompanyInfoDTO.class))) {
            return new CrmSiteMap.Administration.Profile.CompanyInfo().formViewerPlace(new Key(-1));
        } else if (SecurityController.check(DataModelPermission.permissionRead(ARCode.class))) {
            return new CrmSiteMap.Administration.Financial.ARCode();
        } else if (SecurityController.check(new ActionPermission(CrmContentManagementAccess.class))) {
            return new ContentManagement.General();
        } else {
            return AppPlace.NOWHERE;
        }
    }

    /**
     * Used to disable tracking of Vista Employee in customer application
     */
    private boolean hasVistaSupportCookie() {
        return CommonsStringUtils.isStringSet(Cookies.getCookie(DeploymentConsts.vistaEmployeeCookie));
    }

    @Override
    public NotificationAppPlace getNotificationPlace(Notification notification) {
        NotificationAppPlace place = new CrmSiteMap.RuntimeError();
        place.setNotification(notification);
        return place;
    }
}
