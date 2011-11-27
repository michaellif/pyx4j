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
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.essentials.client.DefaultErrorHandlerDialog;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.CrmPanel;
import com.propertyvista.crm.client.ui.LogoViewImpl;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.CrmAuthenticationService;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;

public class CrmSite extends VistaSite {

    private static I18n i18n = I18n.get(CrmSite.class);

    public CrmSite() {
        super(CrmSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        DefaultErrorHandlerDialog.register();

        getHistoryHandler().register(getPlaceController(), getEventBus(), getSystemFashboardPlace());

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(new CrmPanel());

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        AppSite.getEventBus().addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {

            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                init();
            }

        });

        obtainAuthenticationData();
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
        //TODO getPlaceController().goTo(new CrmSiteMap.GenericMessage());
    }

    private void init() {
        SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                LogoViewImpl.temporaryWayToSetTitle(descriptor.siteTitles().crmHeader().getStringView(), descriptor.logoAvalable().isBooleanTrue());

                Window.setTitle(i18n.tr("Vista CRM") + " - " + descriptor.siteTitles().crmHeader().getStringView());

                StyleManger.installTheme(new VistaCrmTheme(), new VistaPalette(descriptor.palette()));
                if (ClientSecurityController.checkBehavior(VistaBehavior.PROPERTY_MANAGER)) {
                    if (CrmSiteMap.Login.class.equals(AppSite.getPlaceController().getWhere().getClass())) {
                        AppSite.getPlaceController().goTo(getSystemFashboardPlace());
                    } else {
                        CrmSite.getHistoryHandler().handleCurrentHistory();
                    }
                } else {
                    AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
                }
            }
        }, ClentNavigUtils.getCurrentLocale());

    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(((CrmAuthenticationService) GWT.create(CrmAuthenticationService.class)), new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                init();
            }

            @Override
            public void onFailure(Throwable caught) {
                //TODO handle it properly
                CrmSite.getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }

    static public Place getSystemFashboardPlace() {
        return new CrmSiteMap.Dashboard().formDashboardPlace(new Key(-1));
    }
}
