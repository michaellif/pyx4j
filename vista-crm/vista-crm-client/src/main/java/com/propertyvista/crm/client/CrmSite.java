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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SessionInactiveEvent;
import com.pyx4j.security.client.SessionInactiveHandler;
import com.pyx4j.security.client.SessionMonitor;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.common.client.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.common.client.events.UserMessageEvent;
import com.propertyvista.common.client.events.UserMessageHandler;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.CrmPanel;
import com.propertyvista.crm.client.ui.LogoViewImpl;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.policies.CrmPolicyRetrieveService;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;

public class CrmSite extends VistaSite {

    private static final I18n i18n = I18n.get(CrmSite.class);

    public CrmSite() {
        super("vista-crm", CrmSiteMap.class, new CrmSiteAppPlaceDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());

        getHistoryHandler().register(getPlaceController(), getEventBus(), AppPlace.NOWHERE);

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(new CrmPanel());

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

        initSiteTheme();
        ClientPolicyManager.initialize(GWT.<PolicyRetrieveService> create(CrmPolicyRetrieveService.class));
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
    }

    private void initSiteTheme() {
        SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                hideLoadingIndicator();
                LogoViewImpl.temporaryWayToSetTitle(descriptor.siteTitles().crmHeader().getStringView(), descriptor.logoAvalable().isBooleanTrue());
                Window.setTitle(i18n.tr("Property Vista") + " - " + descriptor.siteTitles().crmHeader().getStringView());
                StyleManger.installTheme(new CrmTheme(), new VistaPalette(descriptor.palette()));
                VistaFeaturesCustomizationClient.setVistaFeatures(descriptor.features());
                obtainAuthenticationData();
            }

            @Override
            public void onFailure(Throwable caught) {
                hideLoadingIndicator();
                StyleManger.installTheme(new CrmTheme(), VistaPalette.getServerUnavailablePalette());
                super.onFailure(caught);
            }
        }, ClentNavigUtils.getCurrentLocale());

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
        return new CrmSiteMap.Dashboard().formPlace(new Key(-1));
    }
}
