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

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.essentials.server.preloader.AbstractSitesDataPreloader;
import com.pyx4j.examples.rpc.PageType;
import com.pyx4j.examples.rpc.Sites;
import com.pyx4j.examples.rpc.Widgets;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.util.ResourceUriUtil;

public class PreloadSites extends AbstractSitesDataPreloader {

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
        Portlet portlet = createPortlet("portlet1-partners",

        "<span style='text-align:center;'><h4>Our Partners</h4></span>",

        "<div id='" + Widgets.pub$partnersWidget + "'></div>");

        portlet.inlineWidgetIds().add(Widgets.pub$partnersWidget.name());
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

        site
                .pages()
                .add(
                        createPage(
                                "Home",
                                PageType.pub$home,
                                "<t2>Develop amazing web apps built on Google framework. PYX is a platform for rapid development of cross-browser web apps. PYX helps you build sustainable applications faster than ever.</t2>",

                                null,

                                new Portlet[] { partnersListPortlet, slogan1Portlet },

                                new String[] { Widgets.pub$searchWidget.name() }));

        site.pages().add(createPage("Examples", ResourceUriUtil.createResourceUri(siteId, "advice"), null,

        null,

        new Portlet[] { slogan1Portlet, mantraPortlet },

        null));

        site.pages().add(createPage("Contact Us", PageType.pub$home$contactUs, null,

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
        site.logoUrl().setValue("images/logo_hiring.png");

        site.pages().add(
                createPage("Dashboard", PageType.employer$organizationProfile, null, null, null, new String[] { Widgets.employer$organizationProfileWidget
                        .name() }));

        site.pages().add(
                createPage("Customers", PageType.employer$jobs, "<div id='" + Widgets.employer$jobPosting + "'></div>", null, null,
                        new String[] { Widgets.employer$jobPosting.name() }));

        site.pages().add(
                createPage("Orders", PageType.employer$personalSettings, "</div><div id='" + Widgets.employer$personalSettingsWidget + "'></div>", null, null,
                        new String[] { Widgets.employer$personalSettingsWidget.name() }));

        site.pages().add(
                createPage("Resources", PageType.employer$personalSettings, "</div><div id='" + Widgets.employer$personalSettingsWidget + "'></div>", null,
                        null, new String[] { Widgets.employer$personalSettingsWidget.name() }));

        site.pages().add(createPage("Contact Us", PageType.employer$home$contactUs, null));

        site.pages().add(createPage("Technical Support", PageType.employer$home$technicalSupport, null));

        site.pages().add(createPage("Privacy policy", PageType.employer$home$privacyPolicy, null));

        site.pages().add(createPage("Terms of Use", PageType.employer$home$termsOfUse, null));

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

        site.pages().add(createPage("Contact Us", PageType.headless$headless$contactUs, null));

        site.pages().add(createPage("Technical Support", PageType.headless$headless$technicalSupport, null));

        site.pages().add(createPage("Privacy policy", PageType.headless$headless$privacyPolicy, null));

        site.pages().add(createPage("Terms of Use", PageType.headless$headless$termsOfUse, null));

        return site;
    }

    private Page createPage(String caption, PageType pageType, String html) {
        return createPage(caption, pageType.getUri(), html);
    }

    private Page createPage(String caption, PageType pageType, String html, Portlet[] leftPortlets, Portlet[] rightPortlets, String[] inlineWidgets) {
        return createPage(caption, pageType.getUri(), html, leftPortlets, rightPortlets, inlineWidgets);
    }

}
