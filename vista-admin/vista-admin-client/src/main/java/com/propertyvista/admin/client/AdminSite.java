/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.essentials.client.DefaultErrorHandlerDialog;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.admin.client.ui.AdminView;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.services.AuthenticationService;
import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.common.domain.VistaBehavior;

public class AdminSite extends VistaSite {

    public AdminSite() {
        super(AdminSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        DefaultErrorHandlerDialog.register();

        getHistoryHandler().register(getPlaceController(), getEventBus(), new AdminSiteMap.Dashboard());

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(new AdminView());

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
        //TODO getPlaceController().goTo(new AdminSiteMap.GenericMessage());
    }

    private void init() {
        if (ClientSecurityController.checkBehavior(VistaBehavior.PROPERTY_MANAGER)) {
            if (AdminSiteMap.Login.class.equals(AppSite.getPlaceController().getWhere().getClass())) {
                AppSite.getPlaceController().goTo(new AdminSiteMap.Dashboard());
            } else {
                AdminSite.getHistoryHandler().handleCurrentHistory();
            }
        } else {
            AppSite.getPlaceController().goTo(new AdminSiteMap.Login());
        }
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(((AuthenticationService) GWT.create(AuthenticationService.class)), new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                init();
            }

            @Override
            public void onFailure(Throwable caught) {
                //TODO handle it properly
                AdminSite.getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }
}
