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
package com.pyx4j.examples.site.client.headless;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.examples.rpc.PageType;
import com.pyx4j.examples.site.client.ExamplesSitePanel;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.InlineWidgetFactory;
import com.pyx4j.site.client.PageLink;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.client.SitePanelLoader;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.util.ResourceUriUtil;

public class ExamplesHeadlessSitePanel extends ExamplesSitePanel {

    HeadlessInlineWidgetFactory widgetFactory = new HeadlessInlineWidgetFactory();

    private ExamplesHeadlessSitePanel(Site site) {
        super(site);

        addFooterLink(new PageLink("Technical Support", PageType.headless$headless$technicalSupport.getUri().uri().getValue()), false);
        addFooterLink(new PageLink("Privacy Policy", PageType.headless$headless$privacyPolicy.getUri().uri().getValue()), true);
        addFooterLink(new PageLink("Terms of Use", PageType.headless$headless$termsOfUse.getUri().uri().getValue()), true);
        addFooterLink(new PageLink("Contact Us", PageType.headless$headless$contactUs.getUri().uri().getValue()), true);

    }

    @Override
    public InlineWidgetFactory getLocalWidgetFactory() {
        return widgetFactory;
    }

    @Override
    protected void show(final Page page, Map<String, String> args) {
        if (page != null && args != null && ResourceUriUtil.areEqual(page.uri().getValue(), PageType.headless$headless$activation.getUri().uri().getValue())
                && args.containsKey("activate")) {
            AsyncCallback<AuthenticationResponse> callback = new AsyncCallback<AuthenticationResponse>() {
                @Override
                public void onFailure(Throwable caught) {
                    Map<String, String> args = new HashMap<String, String>();
                    args.put("isActivated", "false");
                    ExamplesHeadlessSitePanel.super.show(page, args);
                }

                @Override
                public void onSuccess(AuthenticationResponse result) {
                    ClientContext.authenticated(result);
                    Map<String, String> args = new HashMap<String, String>();
                    args.put("isActivated", "true");
                    ExamplesHeadlessSitePanel.super.show(page, args);
                }
            };
            ExamplesHeadlessSitePanel.super.show(page, args);
            //TODO
            //RPCManager.execute(ActivationServices.ActivateAccount.class, args.get("activate"), callback);

        } else {
            super.show(page, args);
        }
    }

    public static void asyncLoadSite(final Site site, final AsyncCallback<SitePanel> callback) {
        new SitePanelLoader() {
            @Override
            public void createSite(final Site site, final AsyncCallback<SitePanel> callback) {
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess(new ExamplesHeadlessSitePanel(site));
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
