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
 * Created on Feb 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.essentials.client.console.ConsoleSiteFactory;
import com.pyx4j.essentials.client.console.ConsoleSitePanel;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.widgets.client.util.BrowserType;

public abstract class BaseSiteDispatcher extends AbstractSiteDispatcher implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(BaseSiteDispatcher.class);

    @Override
    public void onModuleLoad() {
        ClientEntityFactory.ensureIEntityImplementations();
        ClientLogger.addAppender(new RPCAppender(Level.WARN));
        ClientLogger.setDebugOn(true);
        DefaultErrorHandlerDialog.register();
        log.debug("{}", BrowserType.getCompiledType());
    }

    @Override
    protected boolean isPredefinedSite(String siteName) {
        return ConsoleSiteFactory.siteId.equals(siteName);
    }

    @Override
    protected void obtainPredefinedSite(String siteName, AsyncCallback<SitePanel> callback) {
        ConsoleSitePanel.asyncLoadSite(callback);
    }
}
