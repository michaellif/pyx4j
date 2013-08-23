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
package com.propertyvista.portal.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.Dialog.Type;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.common.client.events.NotificationEvent;
import com.propertyvista.common.client.events.NotificationHandler;
import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.site.Notification;
import com.propertyvista.common.client.site.Notification.NotificationType;
import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.portal.client.themes.PortalTheme;
import com.propertyvista.portal.client.ui.PortalRootPane;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.portal.rpc.portal.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;

public class PortalSite extends VistaSite {

    private static PortalSiteServices portalSiteServices = GWT.create(PortalSiteServices.class);

    private static SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);

    public static final String RESIDENT_INSERTION_ID = "residentInsert";

    public static final String TOP_RIGHT_INSERTION_ID = "siteAuthInsert";

    public PortalSite() {
        super("vista-portal", PortalSiteMap.class, new PortalSiteDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();
        AppSite.getEventBus().addHandler(NotificationEvent.getType(), new PortalUserMessageHandlerByPlace());

        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());

        getHistoryHandler().register(getPlaceController(), getEventBus(), AppPlace.NOWHERE);

        if (RootPanel.get(RESIDENT_INSERTION_ID) != null) {
            RootPanel.get(RESIDENT_INSERTION_ID).add(new PortalRootPane());
        } else {
            RootPanel.get().add(new PortalRootPane());
        }

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        initialize();
    }

    public final native String getAuthenticationToken() /*-{
		return $wnd.gwtToken();
    }-*/;

    @Override
    public void showMessageDialog(String message, String title) {
        setNotification(new Notification(message, NotificationType.ERROR, title));
    }

    public void showInfoDialog(String message, String title) {
        setNotification(new Notification(message, NotificationType.INFO, title));
    }

    private void initialize() {
        initSiteTheme();
        ClientPolicyManager.initialize(GWT.<PolicyRetrieveService> create(PortalPolicyRetrieveService.class));
    }

    private void initSiteTheme() {
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                hideLoadingIndicator();
                if (!descriptor.useCustomHtml().isBooleanTrue()) {
                    StyleManager.installTheme(new PortalTheme(descriptor.skin().getValue()), new VistaPalette(descriptor.palette()));
                }
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
        }, true, getAuthenticationToken());
    }

    public static PortalSite instance() {
        return (PortalSite) AppSite.instance();
    }

    public static PortalSiteServices getPortalSiteServices() {
        return portalSiteServices;
    }

    private static class PortalUserMessageHandlerByDialog implements NotificationHandler {

        @Override
        public void onUserMessage(NotificationEvent event) {
            Dialog.Type dialogType = null;
            switch (event.getMessageType()) {
            case ERROR:
                dialogType = Type.Error;
                break;
            case FAILURE:
                dialogType = Type.Error;
                break;
            case INFO:
                dialogType = Type.Info;
                break;
            case WARN:
                dialogType = Type.Warning;
                break;
            default:
                dialogType = Type.Warning;
                break;
            }
            new MessageDialog("", event.getMessage() + (ApplicationMode.isDevelopment() ? "\nDebug Info: " + event.getDebugMessage() : ""), dialogType,
                    new OkOption() {
                        @Override
                        public boolean onClickOk() {
                            return true;
                        }
                    });

        }
    }

    private class PortalUserMessageHandlerByPlace implements NotificationHandler {

        @Override
        public void onUserMessage(NotificationEvent event) {
            setNotification(event.getNotification());
            PortalSite.getPlaceController().goToUserMessagePlace();
        }

    }

}
