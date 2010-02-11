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
 * Created on Jan 20, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.gwt.commons.GoogleAnalytics;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.site.shared.domain.ResourceUri;
import com.pyx4j.widgets.client.GlassPanel;

public abstract class SiteDispatcher {

    private static final Logger log = LoggerFactory.getLogger(SiteDispatcher.class);

    private SitePanel currentSitePanel;

    private ResourceUri welcomeUri;

    public SiteDispatcher() {
        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                show(event.getValue());
            }
        });
        ClientSecurityController.instance().addValueChangeHandler(new ValueChangeHandler<Set<Behavior>>() {
            @Override
            public void onValueChange(ValueChangeEvent<Set<Behavior>> event) {
                onAuthenticationChange();
            }
        });

        RootPanel.get().add(GlassPanel.instance());

    }

    //TODO handle wrong tokens !!!
    public void show(String path) {
        //Split path to uri and args
        String uri = null;
        Map<String, String> args = null;
        int splitIndex = path.indexOf(ResourceUri.ARGS_GROUP_SEPARATOR);
        if (splitIndex == -1) {
            uri = path;
        } else {
            uri = path.substring(0, splitIndex);
            if (path.length() > splitIndex) {
                args = parsArgs(path.substring(splitIndex + 1));
            }
        }

        log.debug("Page URI " + uri);
        log.debug("Page Args " + args);

        if (uri == null || uri.length() == 0) {
            uri = welcomeUri.uri().getValue();
            args = null;
            if (uri == null) {
                throw new RuntimeException("welcomeUri is not set");
            }
        }
        final String siteName = uri.substring(0, uri.indexOf(com.pyx4j.site.shared.domain.ResourceUri.SITE_SEPARATOR));
        final String finalUri = uri;
        final Map<String, String> finalArgs = args;

        //TODO check site permission
        if (true) {
            obtainSitePanel(siteName, new AsyncCallback<SitePanel>() {
                @Override
                public void onFailure(Throwable caught) {
                    //TODO 
                }

                @Override
                public void onSuccess(SitePanel sitePanel) {
                    if (sitePanel != null) {
                        show(sitePanel, finalUri, finalArgs);
                    } else {
                        throw new Error("sitePanel is not found");
                    }
                }
            });
        }

    }

    protected void show(SitePanel sitePanel, String uri, Map<String, String> args) {
        if (uri != null) {
            if (!sitePanel.equals(currentSitePanel)) {
                if (currentSitePanel != null) {
                    RootPanel.get().remove(currentSitePanel);
                }
                currentSitePanel = sitePanel;
                RootPanel.get().add(currentSitePanel);
            }
            GoogleAnalytics.track("#" + uri);
            sitePanel.show(uri, args);
        }
    }

    public void obtainSitePanel(String siteName, AsyncCallback<SitePanel> callback) {

    }

    protected void onAuthenticationChange() {
        if (ClientContext.isAuthenticated()) {
            onAfterLogIn();
        } else {
            onAfterLogOut();
        }
    }

    protected void onAfterLogOut() {
        log.debug("onAfterLogOut");
        for (SitePanel panel : getAllSitePanels()) {
            panel.onAfterLogOut();
        }

    }

    protected void onAfterLogIn() {
        log.debug("onAfterLogIn");
        for (SitePanel panel : getAllSitePanels()) {
            panel.onAfterLogIn();
        }

    }

    public abstract Iterable<SitePanel> getAllSitePanels();

    public ResourceUri getWelcomeUri() {
        return welcomeUri;
    }

    public void setWelcomeUri(ResourceUri welcomeUri) {
        this.welcomeUri = welcomeUri;
    }

    public SitePanel getCurrentSitePanel() {
        return currentSitePanel;
    }

    public void setCurrentSitePanel(SitePanel currentSitePanel) {
        this.currentSitePanel = currentSitePanel;
    }

    private Map<String, String> parsArgs(String substring) {
        Map<String, String> args = null;
        String[] nameValues = substring.split(ResourceUri.ARGS_SEPARATOR);
        if (nameValues.length > 0) {
            args = new HashMap<String, String>();
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split(ResourceUri.NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    args.put(nameAndValue[0], nameAndValue[1]);
                } else {
                    log.warn("Can't pars argument {}", nameValues[i]);
                }
            }
        }
        return args;
    }

}
