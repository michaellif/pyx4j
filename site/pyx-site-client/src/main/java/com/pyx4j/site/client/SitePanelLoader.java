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
 * Created on Feb 11, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.shared.domain.Site;

public abstract class SitePanelLoader {

    private static Logger log = LoggerFactory.getLogger(SitePanelLoader.class);

    protected static final int RECOVERABLE_LOAD_RETRY_MAX = 3;

    private int retryAttempt = 0;

    protected void handleRunAsyncFailure(Throwable caught, final Site site, final AsyncCallback<SitePanel> callback) {
        log.error("Code download failed", caught);
        if ((RPCManager.isRecoverableAppengineFailure(caught)) && (retryAttempt < RECOVERABLE_LOAD_RETRY_MAX)) {
            retryAttempt++;
            createSite(site, callback);
        } else {
            UncaughtHandler.onUnrecoverableError(caught, "SiteAsync");
        }
    }

    public abstract void createSite(final Site site, final AsyncCallback<SitePanel> callback);

}
