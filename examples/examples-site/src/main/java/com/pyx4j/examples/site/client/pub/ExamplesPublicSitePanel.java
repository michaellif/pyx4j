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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.pub;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.examples.rpc.PageType;
import com.pyx4j.examples.site.client.ExamplesSitePanel;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.InlineWidgetFactory;
import com.pyx4j.site.client.LinkBarMessage;
import com.pyx4j.site.client.PageLink;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.client.SitePanelLoader;
import com.pyx4j.site.shared.domain.Site;

public class ExamplesPublicSitePanel extends ExamplesSitePanel {

    private LinkBarMessage welcomeMessage;

    private static InlineWidgetFactory publicWidgetFactory = new PublicInlineWidgetFactory();

    private ExamplesPublicSitePanel(Site site) {
        super(site);

        addFooterLink(new PageLink("Technical Support", PageType.pub$home$technicalSupport.getUri()), false);
        addFooterLink(new PageLink("Privacy Policy", PageType.pub$home$privacyPolicy.getUri()), true);
        addFooterLink(new PageLink("Terms of Use", PageType.pub$home$termsOfUse.getUri()), true);
        addFooterLink(new PageLink("Contact Us", PageType.pub$contactUs.getUri()), true);

    }

    @Override
    public void onAfterLogIn() {
        super.onAfterLogIn();
        removeAllHeaderLinks();
        welcomeMessage = new LinkBarMessage("<b>Welcome, " + ClientContext.getUserVisit().getName() + "</b>");
        addHeaderLink(welcomeMessage, false);
        addHeaderLink(getLogOutLink(), true);
    }

    @Override
    public void onAfterLogOut() {
        super.onAfterLogOut();
        removeAllHeaderLinks();
        addHeaderLink(getLogInLink(), false);
    }

    private void removeAllHeaderLinks() {
        removeHeaderLink(welcomeMessage);
        removeHeaderLink(getLogInLink());
        removeHeaderLink(getLogOutLink());

    }

    @Override
    public InlineWidgetFactory getLocalWidgetFactory() {
        return publicWidgetFactory;
    }

    public static void asyncLoadSite(final Site site, final AsyncCallback<SitePanel> callback) {
        new SitePanelLoader() {
            @Override
            public void createSite(final Site site, final AsyncCallback<SitePanel> callback) {
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess(new ExamplesPublicSitePanel(site));
                    }

                    @Override
                    public void onFailure(Throwable reason) {
                        handleRunAsyncFailure(reason, site, callback);
                    }
                });
            }
        }.createSite(site, callback);
    }

}
