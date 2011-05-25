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

import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.common.domain.VistaBehavior;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class CrmSite extends VistaSite {

    private CrmGinjector ginjector;

    public CrmSite() {
        super(CrmSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        DefaultErrorHandlerDialog.register();

        ginjector = GWT.create(CrmGinjector.class);
        getHistoryHandler().register(getPlaceController(), getEventBus(), new CrmSiteMap.Login());

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(ginjector.getSiteView());

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        AppSite.getEventBus().addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {

            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                loadCrm();
            }

        });

        obtainAuthenticationData();
    }

    private void loadCrm() {

        if (ClientSecurityController.checkBehavior(VistaBehavior.PROPERTY_MANAGER)) {
            AppSite.getPlaceController().goTo(new CrmSiteMap.Dashboard());
        } else {
            AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
        }
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
        //TODO getPlaceController().goTo(new CrmSiteMap.GenericMessage());
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                CrmSite.getHistoryHandler().handleCurrentHistory();
            }

            @Override
            public void onFailure(Throwable caught) {
                CrmSite.getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }
}
