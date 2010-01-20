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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.site.client.domain.PageUri;

public class DestinationDispatcher implements ValueChangeHandler<String> {

    private static final Logger log = LoggerFactory.getLogger(DestinationDispatcher.class);

    private final List<SitePanel> sites = new ArrayList<SitePanel>();

    private SitePanel currentSitePanel;

    private String welcomeUri;

    public DestinationDispatcher() {
        History.addValueChangeHandler(this);
    }

    public void show(String historyToken) {
        log.debug("Show page " + historyToken);
        if (historyToken == null || historyToken.length() == 0) {
            historyToken = welcomeUri;
            if (historyToken == null) {
                throw new RuntimeException("welcomeUri is not set");
            }
        }
        SitePanel sitePanel = getSitePanel(new PageUri(historyToken));
        if (sitePanel == null) {
            historyToken = welcomeUri;
            sitePanel = getSitePanel(new PageUri(historyToken));
        }
        if (sitePanel == null) {
            throw new RuntimeException("site can't be found for welcomeUri " + welcomeUri);
        }

        if (!sitePanel.equals(currentSitePanel)) {
            if (currentSitePanel != null) {
                RootPanel.get().remove(currentSitePanel);
            }
            currentSitePanel = sitePanel;
            RootPanel.get().add(currentSitePanel);
        }
        show(sitePanel, historyToken);
    }

    private SitePanel getSitePanel(PageUri uri) {
        String siteName = uri.getSiteName();
        if (siteName == null) {
            return null;
        }
        return getSitePanel(siteName);
    }

    private SitePanel getSitePanel(String siteName) {
        for (SitePanel sitePanel : sites) {
            if (siteName.equals(sitePanel.getSiteName())) {
                return sitePanel;
            }
        }
        return null;
    }

    private void show(SitePanel sitePanel, String historyToken) {
        sitePanel.show(historyToken);
    }

    protected void addSite(SitePanel panel) {
        sites.add(panel);
    }

    public String getWelcomeUri() {
        return welcomeUri;
    }

    public void setWelcomeUri(String welcomeUri) {
        this.welcomeUri = welcomeUri;
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        show(event.getValue());
    }

}
