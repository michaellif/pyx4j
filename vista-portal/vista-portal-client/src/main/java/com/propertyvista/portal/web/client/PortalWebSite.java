/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
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
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.portal.web.services.PortalAuthenticationService;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;
import com.propertyvista.portal.web.client.themes.PortalWebPalette;
import com.propertyvista.portal.web.client.themes.PortalWebTheme;
import com.propertyvista.portal.web.client.ui.PortalRootPane;

public class PortalWebSite extends VistaSite {

    private static SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);

    private PortalRootPane portalRootPane;

    public PortalWebSite() {
        super("vista-portal", PortalSiteMap.class, new SingletonViewFactory(), new PortalWebSiteDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();
        AppSite.getEventBus().addHandler(NotificationEvent.getType(), new PortalUserMessageHandler());

        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());

        getHistoryHandler().register(getPlaceController(), getEventBus(), AppPlace.NOWHERE);

        portalRootPane = new PortalRootPane();

        RootLayoutPanel.get().add(portalRootPane);

        hideLoadingIndicator();

        if (verifyBrowserCompatibility()) {
            initialize();
        }
    }

    @Override
    protected boolean isBrowserCompatible() {
        return VistaBrowserRequirments.isBrowserCompatiblePortal();
    }

    private void initialize() {
        initSiteTheme();

        PortalSessionInactiveHandler.register();

        ClientPolicyManager.initialize(GWT.<PolicyRetrieveService> create(PortalPolicyRetrieveService.class));
    }

    private void initSiteTheme() {
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                hideLoadingIndicator();
                StyleManager.installTheme(new PortalWebTheme(descriptor.skin().getValue()), new PortalWebPalette());
                VistaFeaturesCustomizationClient.setVistaFeatures(descriptor.features());
                VistaFeaturesCustomizationClient.setGoogleAnalyticDisableForEmployee(descriptor.isGoogleAnalyticDisableForEmployee().getValue());
                VistaFeaturesCustomizationClient.enviromentTitleVisible = descriptor.enviromentTitleVisible().getValue(Boolean.TRUE);
                obtainAuthenticationData();
            }

            @Override
            public void onFailure(Throwable caught) {
                hideLoadingIndicator();
                super.onFailure(caught);
            }
        }, ClientNavigUtils.getCurrentLocale());

    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(GWT.<AuthenticationService> create(PortalAuthenticationService.class), new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                getHistoryHandler().handleCurrentHistory();
            }

            @Override
            public void onFailure(Throwable caught) {
                getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        }, true, null);
    }

    public static PortalWebSite instance() {
        return (PortalWebSite) AppSite.instance();
    }

    public static void scrollToTop() {
        instance().portalRootPane.asWidget().scrollToTop();
    }

    //  portalRootPane

    private class PortalUserMessageHandler implements NotificationHandler {

        @Override
        public void onNotification(NotificationEvent event) {
            PortalWebSite.getPlaceController().showNotification(event.getNotification());
        }

    }

}
