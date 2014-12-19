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
 */
package com.propertyvista.operations.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.essentials.client.DefaultErrorHandlerDialog;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.SingletonViewFactory;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;

import com.propertyvista.common.client.site.VistaBrowserRequirments;
import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.operations.client.themes.OperationsPalette;
import com.propertyvista.operations.client.themes.OperationsTheme;
import com.propertyvista.operations.client.ui.OperationsRootPane;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.services.OperationsAuthenticationService;

public class OperationsSite extends VistaSite {

    public OperationsSite() {
        super("vista-operations", OperationsSiteMap.class, new SingletonViewFactory(), new OperationsSiteAppPlaceDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();
        OperationsEntityMapper.init();
        DefaultErrorHandlerDialog.register();

        ClientContext.setAuthenticationService(GWT.<AuthenticationService> create(OperationsAuthenticationService.class));
        getHistoryHandler().register(getPlaceController(), getEventBus());
        StyleManager.installTheme(new OperationsTheme(), new OperationsPalette());

        if (verifyBrowserCompatibility()) {
            setRootPane(new OperationsRootPane());

            hideLoadingIndicator();
            SessionInactiveDialog.register();
            obtainAuthenticationData();
        }
    }

    @Override
    protected boolean isBrowserCompatible() {
        return VistaBrowserRequirments.isBrowserCompatibleOperations();
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                setOperationsTitle();
                OperationsSite.getHistoryHandler().handleCurrentHistory();
            }

            @Override
            public void onFailure(Throwable caught) {
                setOperationsTitle();
                //TODO handle it properly
                OperationsSite.getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }

    private void setOperationsTitle() {
        String envName = ClientContext.getEnviromentName();
        if (envName != null) {
            Window.setTitle(envName + " - Vista Operations");
        }
    }

    @Override
    public NotificationAppPlace getNotificationPlace(Notification notification) {
        return null;
    }
}
