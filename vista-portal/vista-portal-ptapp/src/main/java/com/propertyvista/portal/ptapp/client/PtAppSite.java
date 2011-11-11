/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.common.client.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.portal.ptapp.client.themes.PtAppTheme;
import com.propertyvista.portal.ptapp.client.ui.PtAppSitePanel;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.services.AuthenticationService;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class PtAppSite extends VistaSite {

    private PtAppWizardManager wizardManager;

    public PtAppSite() {
        super(PtSiteMap.class, new PtAppPlaceDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());

        getHistoryHandler().register(getPlaceController(), getEventBus(), new PtSiteMap.Login());

        RootPanel.get().add(new PtAppSitePanel());

        SessionInactiveDialog.register();

        wizardManager = new PtAppWizardManager();

        SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                com.propertyvista.portal.ptapp.client.ui.LogoViewImpl.temporaryWayToSetTitle(descriptor.siteTitles().prospectPortalTitle().getStringView());
                Window.setTitle(descriptor.siteTitles().prospectPortalTitle().getStringView());
                StyleManger.installTheme(new PtAppTheme(), new VistaPalette(descriptor.palette()));

                //TODO
                ///StyleManger.installTheme(new VistaCrmTheme(), new VistaPalette(descriptor.palette()));
                authenticateAndInit();
            }
        }, ClentNavigUtils.getCurrentLocale());

    }

    private void authenticateAndInit() {
        ClientContext.obtainAuthenticationData((com.pyx4j.security.rpc.AuthenticationService) GWT.create(AuthenticationService.class),
                new DefaultAsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        init();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        init();
                        super.onFailure(caught);
                    }
                });
    }

    private void init() {
        hideLoadingIndicator();
        PtAppSite.getHistoryHandler().handleCurrentHistory();

        AppSite.getEventBus().addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {
            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                if (!ClientContext.isAuthenticated()) {
                    getWizardManager().onLogout();
                }

                getPlaceController().goTo(getPlaceController().getWhere());

            }
        });

    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
        getPlaceController().goTo(new PtSiteMap.GenericMessage());
    }

    public static PtAppWizardManager getWizardManager() {
        return ((PtAppSite) instance()).wizardManager;
    }

}
