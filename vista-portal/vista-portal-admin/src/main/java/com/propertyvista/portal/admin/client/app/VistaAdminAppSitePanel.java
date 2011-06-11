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
package com.propertyvista.portal.admin.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.InlineWidgetFactory;
import com.pyx4j.site.client.LinkBarMessage;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;

import com.propertyvista.portal.admin.client.VistaAdminBaseSitePanel;
import com.propertyvista.portal.admin.client.app.dashboard.DashboardWidget;
import com.propertyvista.portal.admin.client.app.user.UserEditorWidget;
import com.propertyvista.portal.admin.client.app.user.UserListWidget;
import com.propertyvista.portal.admin.client.site.VistaAdminPublicResources;

public class VistaAdminAppSitePanel extends VistaAdminBaseSitePanel implements InlineWidgetFactory {

    private LinkBarMessage welcomeMessage;

    public static void asyncLoadSite(final AsyncCallback<SitePanel> callback) {
        GlassPanel.show(GlassStyle.Transparent);
        GWT.runAsync(VistaAdminAppSitePanel.class, new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                callback.onSuccess(new VistaAdminAppSitePanel((new VistaAdminAppSiteFactory()).createAppSite()));
                GlassPanel.hide();
            }

            @Override
            public void onFailure(Throwable reason) {
                GlassPanel.hide();
                throw new UnrecoverableClientError(reason);
            }
        });
    }

    public VistaAdminAppSitePanel(Site site) {
        super(site, VistaAdminPublicResources.INSTANCE);
    }

    @Override
    public InlineWidgetFactory getLocalWidgetFactory() {
        return this;
    }

    @Override
    public InlineWidget createWidget(String id) {
        VistaAdminAppWidgets widgetId;
        try {
            widgetId = VistaAdminAppWidgets.valueOf(id);
        } catch (Throwable e) {
            return null;
        }
        switch (widgetId) {
        case app$dashboardWidget:
            return new DashboardWidget();
        case app$userListWidget:
            return new UserListWidget();
        case app$userEditorWidget:
            return new UserEditorWidget();

        default:
            return null;
        }
    }

    @Override
    public void onAfterLogIn() {
        super.onAfterLogIn();
        removeAllHeaderLinks();
        welcomeMessage = new LinkBarMessage("<b>Welcome, " + ClientContext.getUserVisit().getName() + "</b>");
        addHeaderLink(welcomeMessage, false);
        addHeaderLink(getLogOutLink(), true);
    }

    private void removeAllHeaderLinks() {
        removeHeaderLink(welcomeMessage);
        removeHeaderLink(getLogOutLink());
    }
}
