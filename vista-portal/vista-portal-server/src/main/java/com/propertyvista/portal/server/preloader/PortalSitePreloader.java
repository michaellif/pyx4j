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

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.site.Locale;
import com.propertyvista.domain.site.Locale.Lang;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.Testimonial;

public class PortalSitePreloader extends AbstractDataPreloader {

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(SiteDescriptor.class, PageDescriptor.class, PageContent.class);
    }

    @Override
    public String create() {

        try {

            Locale enLocale = EntityFactory.create(Locale.class);
            enLocale.lang().setValue(Lang.en);
            PersistenceServicesFactory.getPersistenceService().persist(enLocale);

            Locale frLocale = EntityFactory.create(Locale.class);
            frLocale.lang().setValue(Lang.fr);
            PersistenceServicesFactory.getPersistenceService().persist(frLocale);

            SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
            site.skin().setValue(Skin.skin1);
            site.baseColor().setValue("#fff");
            site.copyright().setValue("� Starlight Apartments 2011");

            site.locales().add(enLocale);
            site.locales().add(frLocale);

            {
                Testimonial testimonial = EntityFactory.create(Testimonial.class);
                testimonial.content().setValue(
                        "You know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                + "But men, why she's starring at me constantly!!!");
                testimonial.author().setValue("Uncle Vasya Sr.");
                testimonial.locale().set(enLocale);

            }

            {
                News news = EntityFactory.create(News.class);
                news.caption().setValue("Incredible offer!..");
                news.content().setValue("Just by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.");
                news.date().setValue(RandomUtil.randomLogicalDate());
                news.locale().set(enLocale);
            }

            site.childPages().add(createDynamicPage("Find an Apartment", PageDescriptor.Type.findApartment));
            site.childPages().add(createDynamicPage("Residents", PageDescriptor.Type.residents));
            {
                PageDescriptor page = createStaticPage("About us");
                page.childPages().add(createStaticPage("Overview"));
                page.childPages().add(createStaticPage("Team"));
                site.childPages().add(page);
            }
            site.childPages().add(createStaticPage("Customer Care"));
            site.childPages().add(createStaticPage("Terms Of Use"));
            site.childPages().add(createStaticPage("Privacy"));

//            ContentDescriptor frContent = EntityFactory.create(ContentDescriptor.class);
//            {
//                frContent.lang().setValue(ContentDescriptor.Lang.fr);
//
//                {
//                    Testimonial testimonial = EntityFactory.create(Testimonial.class);
//                    testimonial.content().setValue(
//                            "You know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
//                                    + "But men, why she's starring at me constantly!!!");
//                    testimonial.author().setValue("Uncle Vasya Sr.");
//                    PersistenceServicesFactory.getPersistenceService().persist(testimonial);
//                    frContent.testimonials().add(testimonial);
//                }
//
//                {
//                    News news = EntityFactory.create(News.class);
//                    news.caption().setValue("Incredible offer!..");
//                    news.content().setValue("Just by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.");
//                    news.date().setValue(RandomUtil.randomLogicalDate());
//                    PersistenceServicesFactory.getPersistenceService().persist(news);
//                    frContent.news().add(news);
//                }
//
//                frContent.childPages().add(createDynamicPage("Trouver un appartement", PageDescriptor.Type.findApartment));
//                frContent.childPages().add(createDynamicPage("Les r�sidents", PageDescriptor.Type.residents));
//                {
//                    PageDescriptor page = createStaticPage("A propos de nous", "site-about.html");
//                    page.childPages().add(createStaticPage("Vue d'ensemble", "site-overview.html"));
//                    page.childPages().add(createStaticPage("Team", "site-team.html"));
//                    frContent.childPages().add(page);
//                }
//                frContent.childPages().add(createStaticPage("Assistance Client�le", "site-customer-care.html"));
//                frContent.childPages().add(createStaticPage("Mentions l�gales", "site-customer-care.html"));
//                frContent.childPages().add(createStaticPage("Politique de confidentialit�", "site-customer-care.html"));
//                PersistenceServicesFactory.getPersistenceService().persist(frContent);
//
//            }

            PersistenceServicesFactory.getPersistenceService().persist(site);

            StringBuilder b = new StringBuilder();
            b.append("Created Pages");
            return b.toString();

        } catch (IOException e) {
            throw new Error(e);
        }

    }

    private PageDescriptor createDynamicPage(String captione, PageDescriptor.Type type) throws ClassCastException, IOException {
        return createPage(captione, type);
    }

    private PageDescriptor createStaticPage(String captione) throws ClassCastException, IOException {
        return createPage(captione, PageDescriptor.Type.staticContent);
    }

    private PageDescriptor createPage(String caption, PageDescriptor.Type type) throws ClassCastException, IOException {
        PageDescriptor page = EntityFactory.create(PageDescriptor.class);
        page.type().setValue(type);
        page.name().setValue(caption);
//        if (resourceName != null) {
//            page.content().content().setValue(IOUtils.getUTF8TextResource(resourceName, this.getClass()));
//        }
        return page;
    }

}
