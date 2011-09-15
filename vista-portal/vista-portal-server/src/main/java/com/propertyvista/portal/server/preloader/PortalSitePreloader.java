/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.io.IOException;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.site.Locale;
import com.propertyvista.domain.site.Locale.Lang;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.SiteLocale;
import com.propertyvista.domain.site.Testimonial;

public class PortalSitePreloader extends AbstractDataPreloader {

    private Locale enLocale;

    private Locale frLocale;

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(SiteDescriptor.class, PageDescriptor.class, PageContent.class);
    }

    @Override
    public String create() {
        try {
            enLocale = EntityFactory.create(Locale.class);
            enLocale.lang().setValue(Lang.en);
            Persistence.service().persist(enLocale);

            frLocale = EntityFactory.create(Locale.class);
            frLocale.lang().setValue(Lang.fr);
            Persistence.service().persist(frLocale);

            SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
            site.skin().setValue(Skin.skin1);
            site.baseColor().setValue("#fff");
            site.copyright().setValue("© Starlight Apartments 2011");

            SiteLocale enSiteLocale = EntityFactory.create(SiteLocale.class);
            enSiteLocale.locale().set(enLocale);
            site.locales().add(enSiteLocale);

            SiteLocale frSiteLocale = EntityFactory.create(SiteLocale.class);
            frSiteLocale.locale().set(frLocale);
            site.locales().add(frSiteLocale);

            {
                Testimonial testimonial = EntityFactory.create(Testimonial.class);
                testimonial.locale().set(enLocale);

                testimonial.content().setValue(
                        "You know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                + "But men, why she's starring at me constantly!!!");
                testimonial.author().setValue("Uncle Vasya Sr.");

                Persistence.service().persist(testimonial);
            }

            {
                News news = EntityFactory.create(News.class);
                news.locale().set(enLocale);

                news.caption().setValue("Incredible offer!..");
                news.content().setValue("Just by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.");
                news.date().setValue(RandomUtil.randomLogicalDate());

                Persistence.service().persist(news);
            }

            site.childPages().add(createDynamicPage("Find an Apartment", "Trouver un appartement", PageDescriptor.Type.findApartment));
            site.childPages().add(createDynamicPage("Residents", "Les résidents", PageDescriptor.Type.residents));
            {
                PageDescriptor page = createStaticPage("About us", "site-about.html", "A propos de nous", "site-about-fr.html");
                page.childPages().add(createStaticPage("Overview", "site-overview.html", "Vue d'ensemble", "site-overview-fr.html"));
                page.childPages().add(createStaticPage("Team", "site-team.html", "Team", "site-team.html"));
                site.childPages().add(page);
            }
            site.childPages().add(createStaticPage("Customer Care", "site-customer-care.html", "Assistance Clientèle", "site-customer-care.html"));
            site.childPages().add(createStaticPage("Terms Of Use", "site-customer-care.html", "Mentions légales", "site-customer-care.html"));
            site.childPages().add(createStaticPage("Privacy", "site-customer-care.html", "Politique de confidentialité", "site-customer-care.html"));

            Persistence.service().persist(site);

            StringBuilder b = new StringBuilder();
            b.append("Created Pages");
            return b.toString();

        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private PageDescriptor createDynamicPage(String enCaption, String frCaption, PageDescriptor.Type type) throws ClassCastException, IOException {
        return createPage(enCaption, frCaption, type);
    }

    private PageDescriptor createStaticPage(String enCaption, String enResource, String frCaption, String frResource) throws ClassCastException, IOException {

        PageDescriptor page = createPage(enCaption, frCaption, PageDescriptor.Type.staticContent);
        Persistence.service().persist(page);

        if (enResource != null) {
            PageContent pageContent = EntityFactory.create(PageContent.class);
            pageContent.locale().set(enLocale);
            pageContent.content().setValue(IOUtils.getUTF8TextResource(enResource, this.getClass()));
            page.content().add(pageContent);
        }

        if (frResource != null) {
            PageContent pageContent = EntityFactory.create(PageContent.class);
            pageContent.locale().set(frLocale);
            pageContent.content().setValue(IOUtils.getUTF8TextResource(frResource, this.getClass()));
            page.content().add(pageContent);
        }

        return page;
    }

    private PageDescriptor createPage(String enCaption, String frCaption, PageDescriptor.Type type) throws ClassCastException, IOException {

        PageDescriptor page = EntityFactory.create(PageDescriptor.class);
        page.type().setValue(type);
        page.name().setValue(enCaption);

        PageCaption pageCaption = EntityFactory.create(PageCaption.class);
        pageCaption.caption().setValue(enCaption);
        pageCaption.locale().set(enLocale);
        page.caption().add(pageCaption);

        pageCaption = EntityFactory.create(PageCaption.class);
        pageCaption.caption().setValue(frCaption);
        pageCaption.locale().set(frLocale);
        page.caption().add(pageCaption);

        return page;
    }

}
