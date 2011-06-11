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
package com.propertyvista.portal.admin.client.site;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.shared.meta.NavigUtils;

import com.propertyvista.portal.admin.client.app.VistaAdminAppSiteMap;

public class SignOutWidget extends HTML implements InlineWidget {

    SignOutWidget() {
        super("<br/><center><h2>Signing out ...</h2></center><br/>");
    }

    @Override
    public void populate(Map<String, String> args) {
        AbstractSiteDispatcher.instance().getSitePanels().remove(NavigUtils.getSiteId(VistaAdminAppSiteMap.App.class));
        ClientContext.logout(new AsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                AbstractSiteDispatcher.show(VistaAdminPublicSiteMap.Pub.Home.Landing.class);
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                AbstractSiteDispatcher.show(VistaAdminPublicSiteMap.Pub.Home.Landing.class);
            }
        });
    }
}
