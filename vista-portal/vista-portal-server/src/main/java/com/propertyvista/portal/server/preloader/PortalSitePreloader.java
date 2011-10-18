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
import java.util.Locale;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.server.ServerI18nFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.shared.CompiledLocale;

public class PortalSitePreloader extends AbstractDataPreloader {

    private AvailableLocale enLocale;

    private AvailableLocale frLocale;

    private AvailableLocale ruLocale;

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(SiteDescriptor.class, PageDescriptor.class, PageContent.class);
    }

    @Override
    public String create() {
        try {
            enLocale = EntityFactory.create(AvailableLocale.class);
            enLocale.lang().setValue(CompiledLocale.en);
            Persistence.service().persist(enLocale);

            frLocale = EntityFactory.create(AvailableLocale.class);
            frLocale.lang().setValue(CompiledLocale.fr);
            Persistence.service().persist(frLocale);

            I18n frI18n = ServerI18nFactory.get(PortalSitePreloader.class, Locale.FRENCH);

            I18n riI18n = null;
            if (ApplicationMode.isDevelopment()) {
                ruLocale = EntityFactory.create(AvailableLocale.class);
                ruLocale.lang().setValue(CompiledLocale.ru);
                Persistence.service().persist(ruLocale);
                riI18n = ServerI18nFactory.get(PortalSitePreloader.class, new Locale("ru", "RU"));
            }

            SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
            site.skin().setValue(Skin.skin1);
            site.baseColor().setValue("#4488bb");
            site.copyright().setValue("� Starlight �partments 2011");

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

            {
                final String caption = "Find an Apartment";
                PageDescriptor page = createDynamicPage(caption, frI18n.tr(caption), PageDescriptor.Type.findApartment);

                if (ruLocale != null) {
                    addCaption(page, riI18n.tr(caption), ruLocale);
                }
                site.childPages().add(page);
            }

            {
                final String caption = "Residents";
                PageDescriptor page = createDynamicPage(caption, frI18n.tr(caption), PageDescriptor.Type.residents);

                if (ruLocale != null) {
                    addCaption(page, riI18n.tr(caption), ruLocale);
                }
                site.childPages().add(page);
            }

            {
                final String caption = "About us";
                PageDescriptor page = createStaticPage(caption, "site-about.html", frI18n.tr(caption), "site-about_fr.html");
                page.childPages().add(createStaticPage("Overview", "site-overview.html", "Vue d'ensemble", "site-overview_fr.html"));
                page.childPages().add(createStaticPage("Team", "site-team.html", "Team", "site-team.html"));

                if (ruLocale != null) {
                    addCaption(page, riI18n.tr(caption), ruLocale);

                    PageContent pageContent = EntityFactory.create(PageContent.class);
                    pageContent.locale().set(ruLocale);
                    pageContent.content().setValue(IOUtils.getUTF8TextResource("site-about_ru.html", this.getClass()));
                    page.content().add(pageContent);
                }

                site.childPages().add(page);
            }

            {
                final String caption = "Customer Care";
                PageDescriptor page = createStaticPage(caption, "site-customer-care.html", frI18n.tr(caption), "site-customer-care.html");
                if (ruLocale != null) {
                    addCaption(page, riI18n.tr(caption), ruLocale);
                    addContent(page, "site-customer-care_ru.html", ruLocale);
                }
                site.childPages().add(page);
            }

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

    private Testimonial createTestimonial(AvailableLocale locale, String content, String author) {
        Testimonial testimonial = EntityFactory.create(Testimonial.class);
        testimonial.locale().set(locale);

        testimonial.content().setValue(content);
        testimonial.author().setValue(author);

        return testimonial;
    }

    private News createNews(AvailableLocale locale, String caption, String content, LogicalDate date) {
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

    private PageDescriptor createStaticPage(String enCaption, String enResource, String frCaption, String frResource) throws IOException {

        PageDescriptor page = createPage(enCaption, frCaption, PageDescriptor.Type.staticContent);
        Persistence.service().persist(page);

        addContent(page, enResource, enLocale);
        addContent(page, frResource, frLocale);

        return page;
    }

    private void addContent(PageDescriptor page, String resource, AvailableLocale locale) throws IOException {
        PageContent pageContent = EntityFactory.create(PageContent.class);
        pageContent.locale().set(locale);
        pageContent.content().setValue(IOUtils.getUTF8TextResource(resource, this.getClass()));
        page.content().add(pageContent);
    }

    private PageDescriptor createPage(String enCaption, String frCaption, PageDescriptor.Type type) throws ClassCastException, IOException {

        PageDescriptor page = EntityFactory.create(PageDescriptor.class);
        page.type().setValue(type);
        page.name().setValue(enCaption);

        addCaption(page, enCaption, enLocale);
        addCaption(page, frCaption, frLocale);

        return page;
    }

    private void addCaption(PageDescriptor page, String caption, AvailableLocale locale) {
        PageCaption pageCaption = EntityFactory.create(PageCaption.class);
        pageCaption.caption().setValue(caption);
        pageCaption.locale().set(locale);
        page.caption().add(pageCaption);
    }

}
