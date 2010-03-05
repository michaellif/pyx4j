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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.InlineWidgetFactory;
import com.pyx4j.site.client.SitePanel;

public class ConsoleSitePanel extends SitePanel implements InlineWidgetFactory {

    public ConsoleSitePanel() {
        super(ConsoleSiteFactory.createSite());
    }

    @Override
    public InlineWidget createWidget(String id) {
        if (ConsoleSiteFactory.Widgets.console$preloadWidget.name().equals(id)) {
            return new DBPreloadWidget();
        } else {
            return null;
        }
    }

    @Override
    public InlineWidgetFactory getLocalWidgetFactory() {
        return this;
    }

    public static void asyncLoadSite(final AsyncCallback<SitePanel> callback) {
        ClientContext.obtainAuthenticationData(new Runnable() {
            @Override
            public void run() {
                if (SecurityController.checkBehavior(CoreBehavior.DEVELOPER)) {
                    callback.onSuccess(new ConsoleSitePanel());
                } else {
                    callback.onFailure(new SecurityViolationException("Console require Authentication"));
                }
            }
        });

    }

}
