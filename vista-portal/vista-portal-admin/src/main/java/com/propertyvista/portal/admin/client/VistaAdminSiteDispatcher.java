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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.admin.client.app.VistaAdminAppSiteMap;
import com.propertyvista.portal.admin.client.app.VistaAdminAppSitePanel;
import com.propertyvista.portal.admin.client.site.VistaAdminPublicSiteFactory;
import com.propertyvista.portal.admin.client.site.VistaAdminPublicSiteMap;
import com.propertyvista.portal.admin.client.site.VistaAdminPublicSitePanel;

import com.pyx4j.essentials.client.ApplicationCommon;
import com.pyx4j.essentials.client.BaseSiteDispatcher;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.AuthenticationRequiredException;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.shared.meta.NavigUtils;

public class VistaAdminSiteDispatcher extends BaseSiteDispatcher {

    @Override
    public void onModuleLoad() {
        super.onModuleLoad();
        ApplicationCommon.initRpcGlassPanel();
        setWelcomeUri(VistaAdminPublicSiteMap.Pub.Home.Landing.class);
        show();
    }

    @Override
    protected void obtainSite(String siteName, final AsyncCallback<SitePanel> callback) {
        if (!getSitePanels().containsKey(siteName)) {

            if (NavigUtils.getSiteId(VistaAdminAppSiteMap.App.class).equals(siteName)) {
                ClientContext.obtainAuthenticationData(new AsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(new AuthenticationRequiredException("Authentication failed"));
                    }

                    @Override
                    public void onSuccess(Boolean authenticated) {
                        if (authenticated) {
                            VistaAdminAppSitePanel.asyncLoadSite(callback);
                        } else {
                            callback.onFailure(new AuthenticationRequiredException("Page require Authentication"));
                        }

                    }
                });

            } else {
                callback.onSuccess(new VistaAdminPublicSitePanel((new VistaAdminPublicSiteFactory()).createPubSite()));
            }

        } else {
            callback.onSuccess(getSitePanels().get(siteName));
        }
    }

    @Override
    public String getAppId() {
        return "vista-admin";
    }

    @Override
    protected boolean handleAuthenticationRequiredException(AuthenticationRequiredException caught, String siteName) {
        if (!super.handleAuthenticationRequiredException(caught, siteName)) {
            AbstractSiteDispatcher.show(VistaAdminPublicSiteMap.Pub.Home.Landing.class);
        }
        return true;
    }
}
