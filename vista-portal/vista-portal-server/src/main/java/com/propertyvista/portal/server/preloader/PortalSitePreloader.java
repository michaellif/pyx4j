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

import com.propertvista.generator.util.CommonsGenerator;
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
                enContent.lang().setValue(ContentDescriptor.Lang.english);

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

                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.findApartment);
                    page.caption().setValue("Find an Apartment");
                    page.content().content().setValue(CommonsGenerator.lipsum());
                    enContent.childPages().add(page);
                }
                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.residents);
                    page.caption().setValue("Residents");
                    enContent.childPages().add(page);
                }

                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.staticContent);
                    page.caption().setValue("About Us");
                    page.content().content().setValue(IOUtils.getUTF8TextResource("site-about.html", this.getClass()));
                    {
                        PageDescriptor page2 = EntityFactory.create(PageDescriptor.class);
                        page2.type().setValue(PageDescriptor.Type.staticContent);
                        page2.caption().setValue("Overview");
                        page2.content().content().setValue(IOUtils.getUTF8TextResource("site-overview.html", this.getClass()));
                        page.childPages().add(page2);
                    }
                    {
                        PageDescriptor page2 = EntityFactory.create(PageDescriptor.class);
                        page2.type().setValue(PageDescriptor.Type.staticContent);
                        page2.caption().setValue("Team");
                        page2.content().content().setValue(IOUtils.getUTF8TextResource("site-team.html", this.getClass()));
                        page.childPages().add(page2);
                    }
                    enContent.childPages().add(page);
                }

                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.staticContent);
                    page.caption().setValue("Customer Care");
                    page.content().content().setValue(IOUtils.getUTF8TextResource("site-customer-care.html", this.getClass()));
                    enContent.childPages().add(page);
                }

                PersistenceServicesFactory.getPersistenceService().persist(enContent);

            }

            ContentDescriptor frContent = EntityFactory.create(ContentDescriptor.class);
            {
                frContent.lang().setValue(ContentDescriptor.Lang.french);

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

                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.findApartment);
                    page.caption().setValue("Find an Apartment");
                    page.content().content().setValue(CommonsGenerator.lipsum());
                    frContent.childPages().add(page);
                }
                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.residents);
                    page.caption().setValue("Residents");
                    frContent.childPages().add(page);
                }

                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.staticContent);
                    page.caption().setValue("A propos de nous");
                    page.content().content().setValue(IOUtils.getUTF8TextResource("site-about.html", this.getClass()));
                    {
                        PageDescriptor page2 = EntityFactory.create(PageDescriptor.class);
                        page2.type().setValue(PageDescriptor.Type.staticContent);
                        page2.caption().setValue("Overview");
                        page2.content().content().setValue(IOUtils.getUTF8TextResource("site-overview.html", this.getClass()));
                        page.childPages().add(page2);
                    }
                    {
                        PageDescriptor page2 = EntityFactory.create(PageDescriptor.class);
                        page2.type().setValue(PageDescriptor.Type.staticContent);
                        page2.caption().setValue("Team");
                        page2.content().content().setValue(IOUtils.getUTF8TextResource("site-team.html", this.getClass()));
                        page.childPages().add(page2);
                    }
                    frContent.childPages().add(page);
                }

                {
                    PageDescriptor page = EntityFactory.create(PageDescriptor.class);
                    page.type().setValue(PageDescriptor.Type.staticContent);
                    page.caption().setValue("Customer Care");
                    page.content().content().setValue(IOUtils.getUTF8TextResource("site-customer-care.html", this.getClass()));
                    frContent.childPages().add(page);
                }

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

}
