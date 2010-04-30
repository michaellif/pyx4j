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
package com.pyx4j.examples.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.essentials.client.BaseSiteDispatcher;
import com.pyx4j.examples.rpc.Sites;
import com.pyx4j.examples.site.client.crm.ExamplesCrmSitePanel;
import com.pyx4j.examples.site.client.headless.ExamplesHeadlessSitePanel;
import com.pyx4j.examples.site.client.pub.ExamplesPublicSitePanel;
import com.pyx4j.gwt.commons.GoogleAnalytics;
import com.pyx4j.gwt.commons.History;
import com.pyx4j.gwt.geo.GoogleAPI;
import com.pyx4j.security.shared.AuthenticationRequiredException;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.SiteCache;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.util.ResourceUriUtil;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class ExamplesSiteDispatcher extends BaseSiteDispatcher {

    private static Logger log = LoggerFactory.getLogger(ExamplesSiteDispatcher.class);

    @Override
    public void onModuleLoad() {
        super.onModuleLoad();

        //ApplicationCommon.init();

        if (Window.Location.getHost().endsWith("pyx4j-demo.appspot.com")) {
            GoogleAPI.setGoogleAPIKey("ABQIAAAAZuLUizjWCGkAYOfiIpZpgxSsRVEFJ6vLwIBuBBr9l_DYCz2brRSV7aQHBsZMSBwjF72gUZrsiWfavw");
        } else {
            // pyx4j.com
            GoogleAPI.setGoogleAPIKey("ABQIAAAAZuLUizjWCGkAYOfiIpZpgxT2nw7IAgYZCN3UZ-Glm95U7gTjpRTVD1pxXeXBpUR-ZQ5Z0YCQkesTkg");
        }

        GoogleAnalytics.setGoogleAnalyticsTracker("UA-12949578-1", ".pyx4j.com");
        CaptchaComposite.setPublicKey("6LdBxgoAAAAAAP7RdZ3kbHwVA99j1qKB97pdo6Mq");

        setWelcomeUri(ResourceUriUtil.createResourceUri(Sites.pub.name(), "home"));

        show();

    }

    @Override
    protected void onAfterLogOut() {
        super.onAfterLogOut();
        if (getCurrentSitePanel() != null) {
            if (getCurrentSitePanel().equals(getSitePanels().get(Sites.crm.name()))) {
                AbstractSiteDispatcher.show(ResourceUriUtil.createResourceUri(Sites.pub.name(), "home").uri().getValue());
            }
        }
    }

    @Override
    protected boolean handleAuthenticationRequiredException(AuthenticationRequiredException caught, String siteName) {
        if (super.handleAuthenticationRequiredException(caught, siteName)) {
            return true;
        } else if (getCurrentSitePanel() != null) {
            ((ExamplesSitePanel) getCurrentSitePanel()).getLogInLink().executeCommand();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void obtainSite(final String siteName, final AsyncCallback<SitePanel> callback) {

        if (!getSitePanels().containsKey(siteName)) {

            final Sites siteId;
            try {
                siteId = Sites.valueOf(Sites.class, siteName);
            } catch (Throwable e) {
                MessageDialog.error("Ooops", "We don't have site [" + siteName + "]");
                return;
            }

            final AsyncCallback<Site> rpcCallback = new AsyncCallback<Site>() {

                public void onFailure(Throwable t) {
                    log.error(t.getClass().getName() + "[" + t.getMessage() + "]");
                    // TODO create utility site
                    MessageDialog.error("Ooops", "System is unavailable, try again later");
                }

                public void onSuccess(final Site site) {
                    if (site == null) {
                        MessageDialog.error("DB Empty", "Contact administrator.");
                    } else {
                        switch (siteId) {
                        case pub:
                            ExamplesPublicSitePanel.asyncLoadSite(site, callback);
                            break;
                        case crm:
                            ExamplesCrmSitePanel.asyncLoadSite(site, callback);
                            break;
                        case headless:
                            ExamplesHeadlessSitePanel.asyncLoadSite(site, callback);
                            break;
                        }
                    }

                }
            };

            SiteCache.obtain(siteId.name(), rpcCallback);
        } else {
            if (getSitePanels().containsKey(siteName)) {
                callback.onSuccess(getSitePanels().get(siteName));
            }
        }

    }

    @Override
    public String getAppId() {
        return "examples";
    }

}
