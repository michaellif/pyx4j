/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Mar 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client.console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.AuthenticationRequiredException;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.InlineWidgetFactory;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.client.SkinFactory;
import com.pyx4j.site.client.themes.console.ConsoleTheme;
import com.pyx4j.widgets.client.style.Theme;

public class ConsoleSitePanel extends SitePanel implements InlineWidgetFactory {

    public ConsoleSitePanel() {
        super(new ConsoleSiteFactory().createSite(), null);

        setSkinFactory(new SkinFactory() {
            @Override
            public Theme createSkin(String skinName) {
                return new ConsoleTheme();
            }
        });
    }

    @Override
    public InlineWidget createWidget(String widgetId) {
        ConsoleSiteMap.Widgets id;
        try {
            id = ConsoleSiteMap.Widgets.valueOf(widgetId);
        } catch (Throwable e) {
            return null;
        }
        switch (id) {
        case console$preloadWidget:
            return new DBPreloadWidget();
        case console$sessionsAdminWidget:
            return new SessionsAdminWidget();
        case console$simulation:
            return new SimulationWidget();
        default:
            return null;
        }
    }

    @Override
    public InlineWidgetFactory getLocalWidgetFactory() {
        return this;
    }

    public static void asyncCreateSitePanel(final AsyncCallback<SitePanel> callback) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess(new ConsoleSitePanel());
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });
    }

    public static void asyncLoadSite(final AsyncCallback<SitePanel> callback) {
        ClientContext.obtainAuthenticationData(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Boolean authenticated) {
                if (SecurityController.checkBehavior(CoreBehavior.DEVELOPER)) {
                    asyncCreateSitePanel(callback);
                } else {
                    callback.onFailure(new AuthenticationRequiredException("Console require Authentication", true));
                }
            }
        });

    }

}
