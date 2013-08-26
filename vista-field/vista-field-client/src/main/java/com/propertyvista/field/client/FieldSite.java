/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.field.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.SessionInactiveEvent;
import com.pyx4j.security.client.SessionInactiveHandler;
import com.pyx4j.security.client.SessionMonitor;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.field.client.theme.FieldPalette;
import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.FieldRootPane;
import com.propertyvista.field.rpc.FieldSiteMap;
import com.propertyvista.field.rpc.services.FieldAuthenticationService;

public class FieldSite extends VistaSite {

    public FieldSite() {
        super("vista-field", FieldSiteMap.class, null, new FieldSiteAppPlaceDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();
        hideLoadingIndicator();

        getHistoryHandler().register(getPlaceController(), getEventBus(), AppPlace.NOWHERE);

        RootLayoutPanel.get().add(new FieldRootPane());

        FieldEntityMapper.init();

        SessionInactiveDialog.register();
        SessionMonitor.addSessionInactiveHandler(new SessionInactiveHandler() {
            @Override
            public void onSessionInactive(SessionInactiveEvent event) {
                ClientContext.logout((AuthenticationService) GWT.create(FieldAuthenticationService.class), null);
            }
        });

        getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                if (!ClientContext.isAuthenticated()) {
                    //TODO do logout
                }
            }
        });

        StyleManager.installTheme(new FieldTheme(), new FieldPalette());

        obtainAuthenticationData();
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(((AuthenticationService) GWT.create(FieldAuthenticationService.class)), new DefaultAsyncCallback<Boolean>() {

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

    @Override
    public void showMessageDialog(String message, String title) {
    }

}
