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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.style.StyleManger;

import com.propertyvista.common.client.Message;
import com.propertyvista.common.client.VistaSite;
import com.propertyvista.portal.client.themes.BlueColdTheme;
import com.propertyvista.portal.client.ui.PortalScreen;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.AuthenticationService;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;

public class PortalSite extends VistaSite {

    private static PortalSiteServices srv = GWT.create(PortalSiteServices.class);

    public static final String RESIDENT_INSERTION_ID = "vista.resident";

    public static final String RESIDENT_TEST_INSERTION_ID = "vista.resident.test";

    public PortalSite() {
        super(PortalSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        if (RootPanel.get(RESIDENT_INSERTION_ID) != null) {
            getHistoryHandler().register(getPlaceController(), getEventBus(), new PortalSiteMap.Residents());
            RootPanel.get(RESIDENT_INSERTION_ID).add(new PortalScreen());
        } else if (RootPanel.get(RESIDENT_TEST_INSERTION_ID) != null) {
            getHistoryHandler().register(getPlaceController(), getEventBus(), new PortalSiteMap.Landing());
            RootPanel.get(RESIDENT_TEST_INSERTION_ID).add(new PortalScreen());
            StyleManger.installTheme(new BlueColdTheme());
        } else {
            getHistoryHandler().register(getPlaceController(), getEventBus(), new PortalSiteMap.Landing());
            RootPanel.get().add(new PortalScreen());
            StyleManger.installTheme(new BlueColdTheme());
        }

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        ClientContext.obtainAuthenticationData((com.pyx4j.security.rpc.AuthenticationService) GWT.create(AuthenticationService.class),
                new DefaultAsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        getHistoryHandler().handleCurrentHistory();
                    }

                    //TODO remove this when initial application message is implemented
                    @Override
                    public void onFailure(Throwable caught) {
                        getHistoryHandler().handleCurrentHistory();
                        super.onFailure(caught);
                    }
                });

    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
    }

    public static PortalSite instance() {
        return (PortalSite) AppSite.instance();
    }

    public static PortalSiteServices getPortalSiteServices() {
        return srv;
    }
}
