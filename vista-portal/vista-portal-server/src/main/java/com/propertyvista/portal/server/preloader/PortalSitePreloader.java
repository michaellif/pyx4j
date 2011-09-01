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
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.site.ContentDescriptor;
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
        return deleteAll(SiteDescriptor.class, ContentDescriptor.class, PageDescriptor.class, PageContent.class);
    }

    @Override
    public String create() {

        try {

            ContentDescriptor enContent = EntityFactory.create(ContentDescriptor.class);
            {
                enContent.lang().setValue(ContentDescriptor.Lang.en);

                {
                    Testimonial testimonial = EntityFactory.create(Testimonial.class);
                    testimonial.content().setValue(
                            "You know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                    + "But men, why she's starring at me constantly!!!");
                    testimonial.author().setValue("Uncle Vasya Sr.");
                    enContent.testimonials().add(testimonial);
                }

                {
                    News news = EntityFactory.create(News.class);
                    news.caption().setValue("Incredible offer!..");
                    news.content().setValue("Just by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.");
                    news.date().setValue(RandomUtil.randomLogicalDate());
                    enContent.news().add(news);
                }

                enContent.childPages().add(createDynamicPage("Find an Apartment", PageDescriptor.Type.findApartment));
                enContent.childPages().add(createDynamicPage("Residents", PageDescriptor.Type.residents));
                {
                    PageDescriptor page = createStaticPage("About us", "site-about.html");
                    page.childPages().add(createStaticPage("Overview", "site-overview.html"));
                    page.childPages().add(createStaticPage("Team", "site-team.html"));
                    enContent.childPages().add(page);
                }
                enContent.childPages().add(createStaticPage("Customer Care", "site-customer-care.html"));
                enContent.childPages().add(createStaticPage("Terms Of Use", "site-customer-care.html"));
                enContent.childPages().add(createStaticPage("Privacy", "site-customer-care.html"));
                PersistenceServicesFactory.getPersistenceService().persist(enContent);

            }

            ContentDescriptor frContent = EntityFactory.create(ContentDescriptor.class);
            {
                frContent.lang().setValue(ContentDescriptor.Lang.fr);

                {
                    Testimonial testimonial = EntityFactory.create(Testimonial.class);
                    testimonial.content().setValue(
                            "You know... I was simply abscessed with that picture: stars everywhere and you are so small in the entire Universe... "
                                    + "But men, why she's starring at me constantly!!!");
                    testimonial.author().setValue("Uncle Vasya Sr.");
                    PersistenceServicesFactory.getPersistenceService().persist(testimonial);
                    frContent.testimonials().add(testimonial);
                }

                {
                    News news = EntityFactory.create(News.class);
                    news.caption().setValue("Incredible offer!..");
                    news.content().setValue("Just by one star and get another two for free! Absolutely free! Just do not forget to pay property tax.");
                    news.date().setValue(RandomUtil.randomLogicalDate());
                    PersistenceServicesFactory.getPersistenceService().persist(news);
                    frContent.news().add(news);
                }

                frContent.childPages().add(createDynamicPage("Trouver un appartement", PageDescriptor.Type.findApartment));
                frContent.childPages().add(createDynamicPage("Les résidents", PageDescriptor.Type.residents));
                {
                    PageDescriptor page = createStaticPage("A propos de nous", "site-about.html");
                    page.childPages().add(createStaticPage("Vue d'ensemble", "site-overview.html"));
                    page.childPages().add(createStaticPage("Team", "site-team.html"));
                    frContent.childPages().add(page);
                }
                frContent.childPages().add(createStaticPage("Assistance Clientèle", "site-customer-care.html"));
                frContent.childPages().add(createStaticPage("Mentions légales", "site-customer-care.html"));
                frContent.childPages().add(createStaticPage("Politique de confidentialité", "site-customer-care.html"));
                PersistenceServicesFactory.getPersistenceService().persist(frContent);

            }

            SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
            site.skin().setValue(Skin.skin1);
            site.baseColor().setValue("#fff");
            site.copyright().setValue("© Starlight Apartments 2011");
            site.contentDescriptors().add(enContent);
            site.contentDescriptors().add(frContent);

            PersistenceServicesFactory.getPersistenceService().persist(site);

            StringBuilder b = new StringBuilder();
            b.append("Created Pages");
            return b.toString();

        } catch (IOException e) {
            throw new Error(e);
        }

    }

    private PageDescriptor createDynamicPage(String captione, PageDescriptor.Type type) throws ClassCastException, IOException {
        return createPage(captione, null, type);
    }

    private PageDescriptor createStaticPage(String captione, String resourceName) throws ClassCastException, IOException {
        return createPage(captione, resourceName, PageDescriptor.Type.staticContent);
    }

    private PageDescriptor createPage(String caption, String resourceName, PageDescriptor.Type type) throws ClassCastException, IOException {
        PageDescriptor page = EntityFactory.create(PageDescriptor.class);
        page.type().setValue(type);
        page.caption().setValue(caption);
        if (resourceName != null) {
            page.content().content().setValue(IOUtils.getUTF8TextResource(resourceName, this.getClass()));
        }
        return page;
    }

}
