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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.client.domain.Site;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SiteDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(SiteDemo.class);

    public void onModuleLoad() {

        SitePanel pagePanel = new SitePanel("PYX4J Site Demo");

        Site staticProperties = new Site();

        pagePanel.setLogoImage(staticProperties.logoUrl);

        for (int i = 0; i < staticProperties.pages.size(); i++) {
            pagePanel.addPage(staticProperties.pages.get(i), i == 0);
        }

        for (int i = 0; i < staticProperties.headerLinks.size(); i++) {
            pagePanel.addHeaderLink(staticProperties.headerLinks.get(i), i != 0);
        }

        for (int i = 0; i < staticProperties.footerLinks.size(); i++) {
            pagePanel.addFooterLink(staticProperties.footerLinks.get(i), i != 0);
        }

        pagePanel.setFooterCopiright(staticProperties.footerCopiright);
        pagePanel.show(History.getToken());
        RootPanel.get().add(pagePanel);

    }
}
