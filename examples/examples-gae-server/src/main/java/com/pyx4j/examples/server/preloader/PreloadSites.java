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
package com.pyx4j.examples.server.preloader;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.essentials.server.preloader.AbstractSitesDataPreloader;
import com.pyx4j.examples.rpc.PageType;
import com.pyx4j.examples.rpc.Sites;
import com.pyx4j.examples.rpc.Widgets;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.Site;

public class PreloadSites extends AbstractSitesDataPreloader {

    private static final Logger log = LoggerFactory.getLogger(PreloadSites.class);

    private Portlet mantraPortlet;

    private Portlet slogan1Portlet;

    private Portlet partnersListPortlet;

    @Override
    public String create() {
        createPortlets();
        createSitess();
        return createdCounts();
    }

    private void createPortlets() {
        mantraPortlet = createPortlet1();
        PersistenceServicesFactory.getPersistenceService().persist(mantraPortlet);
        slogan1Portlet = createPortlet2();
        PersistenceServicesFactory.getPersistenceService().persist(slogan1Portlet);
        partnersListPortlet = createPartnersListPortlet();
        PersistenceServicesFactory.getPersistenceService().persist(partnersListPortlet);
    }

    private void createSitess() {
        PersistenceServicesFactory.getPersistenceService().persist(createPublicSite());
        PersistenceServicesFactory.getPersistenceService().persist(createCrmSite());
        PersistenceServicesFactory.getPersistenceService().persist(createHeadlessSite());

    }

    private Portlet createPortlet1() {
        Portlet portlet = createPortlet("portlet1-mantra",

        "<span style='text-align:center;'><h4>Corporate Mantra</h4></span>",

        "<span style='text-align:center;'><h4>Have some Fun!<br>Make some money!<br>Do some good!</h4></span>");

        return portlet;
    }

    private Portlet createPartnersListPortlet() {
        Portlet portlet = createPortlet("portlet-technology",

        "<span style='text-align:center;'><h4>Our technology</h4></span>",

        "<div id='" + Widgets.pub$technologyWidget + "'></div>");

        portlet.inlineWidgetIds().add(Widgets.pub$technologyWidget.name());
        return portlet;
    }

    private Portlet createPortlet2() {
        Portlet portlet = createPortlet("portlet-slogan1",

        "<span style='text-align:center;'><h4>PYX is Your System. Your Way.</h4></span>",

        "<span style='text-align:center;'><h4>PYX lets you manage your business <br>the way it was meant to be operated. </h4></span>");

        return portlet;
    }

    @Override
    protected String footerCopiright() {
        return "&copy; 2008-2010 pyx4j.com. All rights reserved.";
    }

    private Site createPublicSite() {
        String siteId = Sites.pub.name();
        Site site = createSite(siteId, "pyx4j.com");

        site.pages().add(createPage("Home", PageType.pub$home, getPageContent(PageType.pub$home),

        null,

        new Portlet[] { partnersListPortlet, slogan1Portlet },

        new String[] { Widgets.pub$searchWidget.name() }));

        {
            site.pages().add(createPage("Examples", PageType.pub$examples, getPageContent(PageType.pub$examples),

            null,

            new Portlet[] { slogan1Portlet, mantraPortlet },

            null));

            site.pages().add(createSingleWidgetPage("Video", PageType.pub$examples$widgets, Widgets.pub$videoWidget));
        }

        site.pages().add(createPage("Contact Us", PageType.pub$contactUs, getPageContent(PageType.pub$contactUs),

        new Portlet[] { slogan1Portlet },

        new Portlet[] { mantraPortlet },

        null));

        site.pages().add(createPage("Technical Support", PageType.pub$home$technicalSupport, null));

        site.pages().add(createPage("Privacy policy", PageType.pub$home$privacyPolicy, null));

        site.pages().add(createPage("Terms of Use", PageType.pub$home$termsOfUse, null));

        return site;
    }

    private Site createCrmSite() {
        String siteId = Sites.crm.name();
        Site site = createSite(siteId, "PYXExample.com");

        site.pages().add(createSingleWidgetPage("Dashboard", PageType.crm$dashboard, Widgets.crm$dashboardWidget));

        site.pages().add(createSingleWidgetPage("Customers", PageType.crm$customers, Widgets.crm$customerListWidget));

        site.pages().add(createSingleWidgetPage("Edit Customer", PageType.crm$customers$editor, Widgets.crm$customerEditorWidget));

        site.pages().add(createSingleWidgetPage("Orders", PageType.crm$orders, Widgets.crm$orderListWidget));

        site.pages().add(createSingleWidgetPage("Edit Order", PageType.crm$orders$editor, Widgets.crm$orderEditorWidget));

        site.pages().add(createSingleWidgetPage("Resources", PageType.crm$resources, Widgets.crm$resourceListWidget));

        site.pages().add(createSingleWidgetPage("Edit Resource", PageType.crm$resources$editor, Widgets.crm$resourceEditorWidget));

        site.pages().add(createPage("Contact Us", PageType.crm$home$contactUs, null));

        site.pages().add(createPage("Technical Support", PageType.crm$home$technicalSupport, null));

        site.pages().add(createPage("Privacy policy", PageType.crm$home$privacyPolicy, null));

        site.pages().add(createPage("Terms of Use", PageType.crm$home$termsOfUse, null));

        return site;
    }

    private Site createHeadlessSite() {
        String siteId = Sites.headless.name();
        Site site = createSite(siteId, "PYXExample.com");

        site.pages().add(createPage("Page not Found", PageType.headless$headless$pageNotFound, null));

        site.pages().add(
                createPage("Change Password", PageType.headless$headless$password, "</div><div id='" + Widgets.headless$password + "'></div>", null, null,
                        new String[] { Widgets.headless$password.name() }));

        site.pages().add(
                createPage("E-mail Validation", PageType.headless$headless$activation, "</div><div id='" + Widgets.headless$activation + "'></div>", null,
                        null, new String[] { Widgets.headless$activation.name() }));

        site.pages().add(createPage("Contact Us", PageType.headless$headless$contactUs, getPageContent(PageType.pub$contactUs)));

        site.pages().add(createPage("Technical Support", PageType.headless$headless$technicalSupport, getPageContent(PageType.pub$contactUs)));

        site.pages().add(createPage("Privacy policy", PageType.headless$headless$privacyPolicy, null));

        site.pages().add(createPage("Terms of Use", PageType.headless$headless$termsOfUse, null));

        return site;
    }

    private String getPageContent(PageType page) {
        try {
            return IOUtils.getTextResource("preloader/" + page.getUri().uri().getValue() + ".txt");
        } catch (IOException e) {
            log.warn("Page text for page " + page + " is not found.");
            return null;
        }

    }

}
