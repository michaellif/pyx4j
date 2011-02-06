/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.admin.client.app.VistaAdminAppSiteMap;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.shared.meta.NavigUtils;

public class SignInCommand implements Command {

    @Override
    public void execute() {
        ClientContext.obtainAuthenticationData(new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean authenticated) {
                if (authenticated) {
                    AbstractSiteDispatcher.show(getLogedInHistoryToken());
                } else {
                    AsyncEntryPoints.showLogInPanel(false);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

        }, false, false);

    }

    public static String getLogedInHistoryToken() {
        return NavigUtils.getPageUri(VistaAdminAppSiteMap.App.Dashboard.class);
    }
}
