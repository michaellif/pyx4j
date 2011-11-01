/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.site;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.annotations.I18nComment;
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
import com.propertyvista.portal.server.preloader.AbstractVistaDataPreloader;
import com.propertyvista.shared.CompiledLocale;

public abstract class AbstractSitePreloader extends AbstractVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(AbstractSitePreloader.class);

    protected static class LocaleInfo {

        AvailableLocale aLocale;

        I18n i18n;
    }

    protected List<CompiledLocale> getLocale() {
        List<CompiledLocale> l = new Vector<CompiledLocale>();
        l.add(CompiledLocale.en);
        l.add(CompiledLocale.fr);
        if (ApplicationMode.isDevelopment()) {
            l.add(CompiledLocale.ru);
        }
        return l;
    }

    protected abstract String pmcName();

    protected abstract Skin skin();

    protected abstract String baseColor();

    protected abstract String copyright();

    @Override
    public String create() {

        List<LocaleInfo> siteLocale = new Vector<LocaleInfo>();

        for (CompiledLocale cl : getLocale()) {
            LocaleInfo li = new LocaleInfo();
            li.i18n = getI18n(cl);
            li.aLocale = EntityFactory.create(AvailableLocale.class);
            li.aLocale.lang().setValue(cl);
            Persistence.service().persist(li.aLocale);
            siteLocale.add(li);
        }

        SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
        site.skin().setValue(skin());
        site.baseColor().setValue(baseColor());
        site.copyright().setValue(copyright());

        {
            final String caption = "Find an Apartment";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.findApartment);

            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
            }
            site.childPages().add(page);
        }

        {
            final String caption = "Residents";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.residents);

            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
            }
            site.childPages().add(page);
        }

        createStaticPages(site, siteLocale);

        createNews(siteLocale);
        createTestimonial(siteLocale);

        Persistence.service().persist(site);
        return null;
    }

    protected void createStaticPages(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        {
            final String caption = "About us";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.staticContent);
            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
                addContent(page, "about.html", li.aLocale);
            }
            site.childPages().add(page);

            {
                final String childPageCaption = "Benefits";
                PageDescriptor childPage = createPage(childPageCaption, PageDescriptor.Type.staticContent);
                for (LocaleInfo li : siteLocale) {
                    addCaption(childPage, li.i18n.tr(childPageCaption), li.aLocale);
                    addContent(childPage, "benefits.html", li.aLocale);
                }
                page.childPages().add(childPage);
            }

            {
                final String childPageCaption = "Vision";
                PageDescriptor childPage = createPage(childPageCaption, PageDescriptor.Type.staticContent);
                for (LocaleInfo li : siteLocale) {
                    addCaption(childPage, li.i18n.tr(childPageCaption), li.aLocale);
                    addContent(childPage, "vision.html", li.aLocale);
                }
                page.childPages().add(childPage);
            }

            {
                final String childPageCaption = "Careers";
                PageDescriptor childPage = createPage(childPageCaption, PageDescriptor.Type.staticContent);
                for (LocaleInfo li : siteLocale) {
                    addCaption(childPage, li.i18n.tr(childPageCaption), li.aLocale);
                    addContent(childPage, "careers.html", li.aLocale);
                }
                page.childPages().add(childPage);
            }
        }

        {
            final String caption = "Contact";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.staticContent);
            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
                addContent(page, "contact.html", li.aLocale);
            }
            site.childPages().add(page);
        }
    }

    @I18nComment("This is demo content")
    protected void createTestimonial(List<LocaleInfo> siteLocale) {
        for (LocaleInfo li : siteLocale) {
            final String content = "I just had the pleasure to dealing with your superintendent at my building. I just moved in three weeks ago and was greeted right away by Manolo the Building Representative. I am a very picky guy but any issue/concern I threw at Manolo, he was able to assist with right away. I had an issue with Bell and Manolo went as far as working with the cable technician directly to ensure that my apartment was functioning to my liking. You have a great staff member in Manolo and if my first month experience is a showcase of what Starlight has to offer I can only be thankful that I found this great place to call home.";
            final String author = "Bill B., London, Ontario";
            Persistence.service().persist(createTestimonial(li.aLocale, li.i18n.tr(content), li.i18n.tr(author)));
            break;
        }
        for (LocaleInfo li : siteLocale) {
            final String content = "I just wanted to let you know what a great job your superintendent Sean was doing.  He recently repaired my faucet and was very professional and courteous. Not only did he come and fix the issue right away, he also gave me advice on how to avoid this from happening in the future. This building is my home, and having Starlight now taking over showed instant results.\nThank you again.";
            final String author = "Jane L.";
            Persistence.service().persist(createTestimonial(li.aLocale, li.i18n.tr(content), li.i18n.tr(author)));
            break;
        }
    }

    @I18nComment("This is demo content")
    protected void createNews(List<LocaleInfo> siteLocale) {
        for (LocaleInfo li : siteLocale) {
            final String caption = "Vancouver prices to keep rising";
            final String content = "The Vancouver housing market may already be unaffordable for many, but there’s enough demand to keep prices rising, according to a new forecast.";
            Persistence.service().persist(createNews(li.aLocale, li.i18n.tr(caption), li.i18n.tr(content), new LogicalDate(111, 03, 22)));
            break;
        }

        for (LocaleInfo li : siteLocale) {
            final String caption = "Ottawa, Toronto defy national sales trend ... for now";
            final String content = "Resale housing activity in August remained stable for the second consecutive month, according to new stats from The Canadian Real Estate Association, although brokers in Toronto and Ottawa benefited from an uptick in sales.";
            Persistence.service().persist(createNews(li.aLocale, li.i18n.tr(caption), li.i18n.tr(content), new LogicalDate(111, 05, 03)));
            break;
        }
    }

    protected I18n getI18n(CompiledLocale cl) {
        switch (cl) {
        case en:
            return ServerI18nFactory.get(AbstractSitePreloader.class, Locale.ENGLISH);
        case fr:
            return ServerI18nFactory.get(AbstractSitePreloader.class, Locale.FRENCH);
        case ru:
            return ServerI18nFactory.get(AbstractSitePreloader.class, new Locale("ru", "RU"));
        default:
            throw new IllegalArgumentException(cl.toString());
        }
    }

    protected PageDescriptor createPage(String caption, PageDescriptor.Type type) {
        PageDescriptor page = EntityFactory.create(PageDescriptor.class);
        page.type().setValue(type);
        page.name().setValue(caption);
        return page;
    }

    protected void addCaption(PageDescriptor page, String caption, AvailableLocale aLocale) {
        PageCaption pageCaption = EntityFactory.create(PageCaption.class);
        pageCaption.caption().setValue(caption);
        pageCaption.locale().set(aLocale);
        page.caption().add(pageCaption);
    }

    protected void addContent(PageDescriptor page, String resourceBaseName, AvailableLocale locale) {
        PageContent pageContent = EntityFactory.create(PageContent.class);
        pageContent.locale().set(locale);

        // TODO use locale to find "_fr".html resources  if available
        String contentText;
        try {
            contentText = IOUtils.getUTF8TextResource(resourceBaseName, this.getClass());
            if (contentText == null) {
                contentText = IOUtils.getUTF8TextResource(resourceBaseName, AbstractSitePreloader.class);
            }
            if (contentText == null) {
                contentText = "Page " + resourceBaseName + " was not created for ${pmcName}";
            }
        } catch (IOException e) {
            log.error("Error", e);
            contentText = "Page was not created for ${pmcName}";
        }
        String pmcName = pmcName();
        if (pmcName == null) {
            pmcName = "n/a";
        }
        contentText = contentText.replace("${pmcName}", pmcName);

        pageContent.content().setValue(contentText);
        page.content().add(pageContent);
    }

    protected Testimonial createTestimonial(AvailableLocale locale, String content, String author) {
        Testimonial testimonial = EntityFactory.create(Testimonial.class);
        testimonial.locale().set(locale);

        testimonial.content().setValue(content);
        testimonial.author().setValue(author);

        return testimonial;
    }

    protected News createNews(AvailableLocale locale, String caption, String content, LogicalDate date) {
        News news = EntityFactory.create(News.class);
        news.locale().set(locale);

        news.caption().setValue(caption);
        news.content().setValue(content);
        news.date().setValue(date);

        return news;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(SiteDescriptor.class, PageDescriptor.class, PageContent.class, AvailableLocale.class, News.class, Testimonial.class);
    }

}
