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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.site.client.domain.ResourceUri;

public abstract class SiteDispatcher implements ValueChangeHandler<String> {

    private static final Logger log = LoggerFactory.getLogger(SiteDispatcher.class);

    private final List<SitePanel> sitePanels = new ArrayList<SitePanel>();

    private SitePanel currentSitePanel;

    private ResourceUri welcomeUri;

    private Boolean logedIn;

    public SiteDispatcher() {
        History.addValueChangeHandler(this);
        ClientSecurityController.instance().addValueChangeHandler(new ValueChangeHandler<Set<Behavior>>() {
            @Override
            public void onValueChange(ValueChangeEvent<Set<Behavior>> event) {
                onAuthenticationChange();
            }
        });

    }

    public void show(String historyToken) {
        log.debug("Show page " + historyToken);
        if (historyToken == null || historyToken.length() == 0) {
            historyToken = welcomeUri.getUri();
            if (historyToken == null) {
                throw new RuntimeException("welcomeUri is not set");
            }
        }
        SitePanel sitePanel = getSitePanel(new ResourceUri(historyToken));
        if (sitePanel == null) {
            historyToken = welcomeUri.getUri();
            sitePanel = getSitePanel(new ResourceUri(historyToken));
        }
        if (sitePanel == null) {
            throw new RuntimeException("site can't be found for welcomeUri " + welcomeUri);
        }
        show(sitePanel, historyToken);
    }

    protected void show(SitePanel sitePanel, String historyToken) {
        if (!sitePanel.equals(currentSitePanel)) {
            if (currentSitePanel != null) {
                RootPanel.get().remove(currentSitePanel);
            }
            currentSitePanel = sitePanel;
            RootPanel.get().add(currentSitePanel);
        }
        if (historyToken == null) {
            sitePanel.showCurrent();
        } else {
            sitePanel.show(historyToken);
        }
    }

    public void showCurrent(SitePanel sitePanel) {
        show(sitePanel, null);
    }

    private SitePanel getSitePanel(ResourceUri uri) {
        String siteName = uri.getSiteName();
        if (siteName == null) {
            return null;
        }
        return getSitePanel(siteName);
    }

    public SitePanel getSitePanel(String siteName) {
        for (SitePanel sitePanel : sitePanels) {
            if (siteName.equals(sitePanel.getSiteName())) {
                return sitePanel;
            }
        }
        return null;
    }

    protected void onAuthenticationChange() {
        if (ClientContext.isAuthenticated()) {
            if (logedIn == null || !logedIn) {
                onAfterLogIn();
                logedIn = true;
            }
        } else {
            if (logedIn == null || logedIn) {
                onAfterLogOut();
                logedIn = false;
            }
        }
    }

    abstract protected void onAfterLogOut();

    abstract protected void onAfterLogIn();

    protected void addSitePanel(SitePanel panel) {
        sitePanels.add(panel);
    }

    public Iterable<SitePanel> getAllSitePanels() {
        return sitePanels;
    }

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

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        show(event.getValue());
    }

}
