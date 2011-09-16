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

import com.pyx4j.commons.LogicalDate;
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

            Persistence.service().persist(
                    createTestimonial(enLocale,
                            "You know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                    + "But men, why she's starring at me constantly!!!", "Uncle Vasya Sr."));

            Persistence.service().persist(
                    createTestimonial(enLocale,
                            "You2 know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                    + "But men, why she's starring at me constantly!!!", "Uncle Vasya Sr."));

            Persistence.service().persist(
                    createTestimonial(enLocale,
                            "You3 know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                    + "But men, why she's starring at me constantly!!!", "Uncle Vasya Sr."));

            Persistence.service().persist(
                    createTestimonial(enLocale,
                            "You4 know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                    + "But men, why she's starring at me constantly!!!", "Uncle Vasya Sr."));

            Persistence
                    .service()
                    .persist(
                            createNews(
                                    enLocale,
                                    "Vancouver prices to keep rising",
                                    "The Vancouver housing market may already be unaffordable for many, but there’s enough demand to keep prices rising, according to a new forecast.",
                                    new LogicalDate(111, 03, 22)));

            Persistence
                    .service()
                    .persist(
                            createNews(
                                    frLocale,
                                    "Les prix à Vancouver continuer à augmenter",
                                    "Le marché du logement de Vancouver peut-être déjà inabordable pour beaucoup, mais il ya une demande suffisante pour maintenir la hausse des prix, selon une nouvelle prévision.",
                                    new LogicalDate(111, 03, 22)));

            Persistence
                    .service()
                    .persist(
                            createNews(
                                    enLocale,
                                    "Ottawa, Toronto defy national sales trend ... for now",
                                    "Resale housing activity in August remained stable for the second consecutive month, according to new stats from The Canadian Real Estate Association, although brokers in Toronto and Ottawa benefited from an uptick in sales.",
                                    new LogicalDate(111, 05, 03)));

            Persistence
                    .service()
                    .persist(
                            createNews(
                                    frLocale,
                                    "Ottawa, Toronto défient nationale tendance des ventes ... pour l'instant",
                                    "L'activité de revente de logements en août est resté stable pour le deuxième mois consécutif, selon de nouvelles statistiques de l'Association canadienne de l'immeuble, bien que les courtiers à Toronto et Ottawa ont bénéficié d'une hausse des ventes.",
                                    new LogicalDate(111, 05, 03)));

            Persistence.service().persist(
                    createNews(enLocale, "Incredible offer4!..",
                            "Just4 by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.",
                            RandomUtil.randomLogicalDate()));

            Persistence.service().persist(
                    createNews(enLocale, "Incredible offer5!..",
                            "Just5 by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.",
                            RandomUtil.randomLogicalDate()));

            Persistence.service().persist(
                    createNews(enLocale, "Incredible offer6!..",
                            "Just6 by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.",
                            RandomUtil.randomLogicalDate()));

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

    private Testimonial createTestimonial(Locale locale, String content, String author) {
        Testimonial testimonial = EntityFactory.create(Testimonial.class);
        testimonial.locale().set(locale);

        testimonial.content().setValue(content);
        testimonial.author().setValue(author);

        return testimonial;
    }

    private News createNews(Locale locale, String caption, String content, LogicalDate date) {
        News news = EntityFactory.create(News.class);
        news.locale().set(locale);

        news.caption().setValue(caption);
        news.content().setValue(content);
        news.date().setValue(date);

        return news;
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
