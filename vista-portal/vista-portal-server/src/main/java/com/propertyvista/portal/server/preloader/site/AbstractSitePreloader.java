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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.server.ServerI18nFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.HtmlContent;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageMetaTags;
import com.propertyvista.domain.site.PortalImageSet;
import com.propertyvista.domain.site.PortalLogoImageResource;
import com.propertyvista.domain.site.ResidentPortalSettings;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.SiteTitles;
import com.propertyvista.domain.site.SocialLink;
import com.propertyvista.domain.site.SocialLink.SocialSite;
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.NewsGadgetContent;
import com.propertyvista.domain.site.gadgets.PromoGadgetContent;
import com.propertyvista.domain.site.gadgets.QuickSearchGadgetContent;
import com.propertyvista.domain.site.gadgets.TestimonialsGadgetContent;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.server.preloader.util.AbstractVistaDataPreloader;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.proxy.HttpsProxyInjection;
import com.propertyvista.shared.config.VistaDemo;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.shared.i18n.CompiledLocale;

public abstract class AbstractSitePreloader extends AbstractVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(AbstractSitePreloader.class);

    protected static class LocaleInfo {

        AvailableLocale aLocale;

        I18n i18n;
    }

    protected List<CompiledLocale> getLocale() {
        List<CompiledLocale> l = new Vector<CompiledLocale>();
        l.add(CompiledLocale.en);
        if (VistaFeatures.instance().countryOfOperation() != CountryOfOperation.UK) {
            l.add(CompiledLocale.fr);
            if (ApplicationMode.isDevelopment() && !DemoData.vistaDemo) {
                l.add(CompiledLocale.ru);
            }
            l.add(CompiledLocale.es);
            l.add(CompiledLocale.zh_CN);
            l.add(CompiledLocale.zh_TW);
        }
        return l;
    }

    protected abstract String pmcName();

    protected abstract Skin skin();

    protected abstract Integer object1();

    protected abstract Integer object2();

    protected abstract Integer contrast1();

    protected abstract Integer contrast2();

    protected abstract Integer foreground();

    protected abstract Integer background();

    protected abstract String copyright();

    @Override
    public String create() {

        List<LocaleInfo> siteLocale = new Vector<LocaleInfo>();

        for (CompiledLocale cl : getLocale()) {
            LocaleInfo li = new LocaleInfo();
            li.i18n = getI18n(cl);
            EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lang(), cl));
            li.aLocale = Persistence.service().retrieve(criteria);
            if (li.aLocale == null) {
                li.aLocale = EntityFactory.create(AvailableLocale.class);
                li.aLocale.lang().setValue(cl);
                Persistence.service().persist(li.aLocale);
            }
            siteLocale.add(li);
        }

        SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
        site._updateFlag().updated().setValue(new Date());

        site.skin().setValue(skin());
        site.sitePalette().object1().setValue(object1());
        site.sitePalette().object2().setValue(object2());
        site.sitePalette().contrast1().setValue(contrast1());
        site.sitePalette().contrast2().setValue(contrast2());
        site.sitePalette().background().setValue(background());
        site.sitePalette().foreground().setValue(foreground());

        site.enabled().setValue(Boolean.TRUE);
        site.disableMapView().setValue(Boolean.FALSE);

        {
            for (LocaleInfo li : siteLocale) {
                SiteTitles titles = EntityFactory.create(SiteTitles.class);
                titles.locale().set(li.aLocale);

                titles.crmHeader().setValue(pmcName());
                titles.residentPortalTitle().setValue(pmcName());
                titles.prospectPortalTitle().setValue(pmcName());
                titles.copyright().setValue(copyright());

                titles.residentPortalPromotions().setValue(li.i18n.tr("Featured Apartments"));

                site.siteTitles().add(titles);

                // meta tags
                String title = li.i18n.tr("Apartments for Rent Across Canada - {0}", pmcName());
                String description = li.i18n.tr("{0} is a leading Canadian Property Management Company. "
                        + "Our commitment to providing the best customer experience and highest standards "
                        + "for our properties is what sets us apart on the Canadian market.", pmcName());
                String keywords = li.i18n.tr("apartments for rent, rental apartments");
                site.metaTags().add(createMeta(li.aLocale, title, description, keywords));
            }
        }

        // social links
        {
            for (SocialSite soc : SocialSite.values()) {
                SocialLink link = EntityFactory.create(SocialLink.class);
                String url = "";
                switch (soc) {
                case Facebook:
                    url = "http://www.facebook.com";
                    break;
                case Twitter:
                    url = "http://www.twitter.com";
                    break;
                case Youtube:
                    url = "http://www.youtube.com";
                    break;
                case Flickr:
                    url = "http://www.flickr.com";
                    break;
                }
                link.socialSite().setValue(soc);
                link.siteUrl().setValue(url);
                site.socialLinks().add(link);
            }
        }

        // home page gadgets
        {
            createQuickSearchGadget(site, siteLocale);

            createPromoGadget(site, siteLocale);

            createTestimonialGadget(site, siteLocale);

            createNewsGadget(site, siteLocale);
        }

        // site pages
        {
            final String caption = "Find an Apartment";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.findApartment);

            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
                addEmptyContent(page, li.aLocale);
            }
            site.childPages().add(page);
        }

        {
            final String caption = "Residents";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.residents);

            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
                addEmptyContent(page, li.aLocale);
            }
            site.childPages().add(page);
        }

        createCustomResidentPage(site, siteLocale);

        createStaticPages(site, siteLocale);

        createLogo(site, siteLocale);

        createCrmLogo(site);

        Persistence.service().persist(site);
        return null;
    }

    protected void createCustomResidentPage(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        // enable resident portal by default
        ResidentPortalSettings settings = EntityFactory.create(ResidentPortalSettings.class);
        settings.enabled().setValue(true);
        settings.useCustomHtml().setValue(false);
        for (LocaleInfo li : siteLocale) {
            String contentText;
            try {
                contentText = getUTF8TextResource("CustomResidentsPage.html", li.aLocale);
                if (contentText == null) {
                    continue;
                }
                HtmlContent content = EntityFactory.create(HtmlContent.class);
                content.locale().set(li.aLocale);
                content.html().setValue(contentText);
                settings.customHtml().add(content);

                settings.proxyWhitelist().addAll(HttpsProxyInjection.generateWhitelist(contentText));
            } catch (IOException ignore) {
            }
        }
        if ((settings.customHtml().size() > 0) && (!VistaDemo.isDemo())) {
            // for custom resident portal content disable public portal
            settings.useCustomHtml().setValue(true);
            site.enabled().setValue(false);
        }
        site.residentPortalSettings().set(settings);
    }

    protected void createStaticPages(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        {
            final String caption = "About us";
            final String secondaryCaption = "General";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.staticContent);
            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.i18n.tr(secondaryCaption), li.aLocale);
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
                final String childPageCaption = "Our Vision";
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

            {
                final String childPageCaption = "News";
                PageDescriptor childPage = createPage(childPageCaption, PageDescriptor.Type.staticContent);
                for (LocaleInfo li : siteLocale) {
                    addCaption(childPage, li.i18n.tr(childPageCaption), li.aLocale);
                }
                page.childPages().add(childPage);
            }

            {
                final String childPageCaption = "Testimonials";
                PageDescriptor childPage = createPage(childPageCaption, PageDescriptor.Type.staticContent);
                for (LocaleInfo li : siteLocale) {
                    addCaption(childPage, li.i18n.tr(childPageCaption), li.aLocale);
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

        {
            final String caption = "Terms Of Use";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.staticContent);
            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
                addContent(page, "terms-of-use.html", li.aLocale);
            }
            site.childPages().add(page);
        }

        {
            final String caption = "Privacy";
            PageDescriptor page = createPage(caption, PageDescriptor.Type.staticContent);
            for (LocaleInfo li : siteLocale) {
                addCaption(page, li.i18n.tr(caption), li.aLocale);
                addContent(page, "privacy.html", li.aLocale);
            }
            site.childPages().add(page);
        }
    }

    protected void createQuickSearchGadget(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        HomePageGadget gadget = createGadget("Quick Search", HomePageGadget.GadgetArea.narrow);
        gadget.content().set(EntityFactory.create(QuickSearchGadgetContent.class));
        site.homePageGadgetsNarrow().add(gadget);

    }

    protected void createPromoGadget(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        HomePageGadget gadget = createGadget("Featured Apartments", HomePageGadget.GadgetArea.wide);
        gadget.content().set(EntityFactory.create(PromoGadgetContent.class));
        site.homePageGadgetsWide().add(gadget);
    }

    @I18nComment("This is demo content")
    protected void createTestimonialGadget(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        HomePageGadget gadget = createGadget("Testimonials", HomePageGadget.GadgetArea.wide);
        TestimonialsGadgetContent testimContent = EntityFactory.create(TestimonialsGadgetContent.class);
        for (LocaleInfo li : siteLocale) {
            final String content = "I just had the pleasure to dealing with your superintendent at my building. I just moved in three weeks ago and was greeted right away by Manolo the Building Representative. I am a very picky guy but any issue/concern I threw at Manolo, he was able to assist with right away. I had an issue with Bell and Manolo went as far as working with the cable technician directly to ensure that my apartment was functioning to my liking. You have a great staff member in Manolo and if my first month experience is a showcase of what Starlight has to offer I can only be thankful that I found this great place to call home.";
            final String author = "Bill B., London, Ontario";
            testimContent.testimonials().add(createTestimonial(li.aLocale, li.i18n.tr(content), li.i18n.tr(author)));
        }
        for (LocaleInfo li : siteLocale) {
            final String content = "I just wanted to let you know what a great job your superintendent Sean was doing.  He recently repaired my faucet and was very professional and courteous. Not only did he come and fix the issue right away, he also gave me advice on how to avoid this from happening in the future. This building is my home, and having Starlight now taking over showed instant results.\nThank you again.";
            final String author = "Jane L.";
            testimContent.testimonials().add(createTestimonial(li.aLocale, li.i18n.tr(content), li.i18n.tr(author)));
        }
        gadget.content().set(testimContent);
        site.homePageGadgetsWide().add(gadget);
    }

    @I18nComment("This is demo content")
    protected void createNewsGadget(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        HomePageGadget gadget = createGadget("News", HomePageGadget.GadgetArea.narrow);
        NewsGadgetContent newsContent = EntityFactory.create(NewsGadgetContent.class);
        for (LocaleInfo li : siteLocale) {
            final String caption = "Vancouver prices to keep rising";
            final String content = "The Vancouver housing market may already be unaffordable for many, but there is enough demand to keep prices rising, according to a new forecast.";
            newsContent.news().add(createNews(li.aLocale, li.i18n.tr(caption), li.i18n.tr(content), new LogicalDate(111, 03, 22)));
        }

        for (LocaleInfo li : siteLocale) {
            final String caption = "Ottawa, Toronto defy national sales trend ... for now";
            final String content = "Resale housing activity in August remained stable for the second consecutive month, according to new stats from The Canadian Real Estate Association, although brokers in Toronto and Ottawa benefited from an uptick in sales.";
            newsContent.news().add(createNews(li.aLocale, li.i18n.tr(caption), li.i18n.tr(content), new LogicalDate(111, 05, 03)));
        }
        gadget.content().set(newsContent);
        site.homePageGadgetsNarrow().add(gadget);
    }

    protected I18n getI18n(CompiledLocale cl) {
        switch (cl) {
        case en:
            return ServerI18nFactory.get(AbstractSitePreloader.class, Locale.ENGLISH);
        case en_GB:
            return ServerI18nFactory.get(AbstractSitePreloader.class, Locale.UK);
        case fr:
            return ServerI18nFactory.get(AbstractSitePreloader.class, Locale.FRENCH);
        case ru:
            return ServerI18nFactory.get(AbstractSitePreloader.class, new Locale("ru", "RU"));
        case es:
            return ServerI18nFactory.get(AbstractSitePreloader.class, new Locale("es"));
        case zh_CN:
            return ServerI18nFactory.get(AbstractSitePreloader.class, Locale.SIMPLIFIED_CHINESE);
        case zh_TW:
            return ServerI18nFactory.get(AbstractSitePreloader.class, Locale.TRADITIONAL_CHINESE);
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
        addCaption(page, caption, caption, aLocale);
    }

    protected void addCaption(PageDescriptor page, String caption, String secondaryCaption, AvailableLocale aLocale) {
        PageCaption pageCaption = EntityFactory.create(PageCaption.class);
        pageCaption.caption().setValue(caption);
        pageCaption.secondaryCaption().setValue(secondaryCaption);
        pageCaption.locale().set(aLocale);
        page.caption().add(pageCaption);
    }

    public String getUTF8TextResource(String resourceBaseName, AvailableLocale locale) throws IOException {
        String contentText;

        // Use locale to find "_fr".html resources  if available.
        String resourceI18nName = FilenameUtils.getBaseName(resourceBaseName) + "_" + locale.lang().getValue().name() + "."
                + FilenameUtils.getExtension(resourceBaseName);

        contentText = IOUtils.getUTF8TextResource(resourceI18nName, this.getClass());
        if (contentText != null) {
            return contentText;
        }
        contentText = IOUtils.getUTF8TextResource(resourceBaseName, this.getClass());
        if (contentText != null) {
            return contentText;
        }

        contentText = IOUtils.getUTF8TextResource(resourceI18nName, AbstractSitePreloader.class);
        if (contentText != null) {
            return contentText;
        }
        contentText = IOUtils.getUTF8TextResource(resourceBaseName, AbstractSitePreloader.class);
        if (contentText != null) {
            return contentText;
        }

        return contentText;

    }

    protected void addEmptyContent(PageDescriptor page, AvailableLocale locale) {
        PageContent pageContent = EntityFactory.create(PageContent.class);
        pageContent.locale().set(locale);
        page.content().add(pageContent);
    }

    protected void addContent(PageDescriptor page, String resourceBaseName, AvailableLocale locale) {
        PageContent pageContent = EntityFactory.create(PageContent.class);
        pageContent.locale().set(locale);

        String contentText;
        try {
            contentText = getUTF8TextResource(resourceBaseName, locale);
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

    protected HomePageGadget createGadget(String name, HomePageGadget.GadgetArea area) {
        HomePageGadget gadget = EntityFactory.create(HomePageGadget.class);
        gadget.status().setValue(HomePageGadget.GadgetStatus.published);
        gadget.area().setValue(area);
        gadget.name().setValue(name);
        return gadget;
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

    protected PageMetaTags createMeta(AvailableLocale locale, String title, String description, String keywords) {
        PageMetaTags meta = EntityFactory.create(PageMetaTags.class);
        meta.locale().set(locale);
        meta.title().setValue(title);
        meta.description().setValue(description);
        meta.keywords().setValue(keywords);
        return meta;
    }

    private void createCrmLogo(SiteDescriptor site) {
        String cType = "image/png";
        SiteImageResource siteImage = makeSiteImage(DeploymentConsts.crmLogo, cType);
        if (siteImage == null) {
            siteImage = makeSiteImage("logo.png", cType);
        }
        site.crmLogo().set(siteImage);
    }

    private void createLogo(SiteDescriptor site, List<LocaleInfo> siteLocale) {
        String cType = "image/png";
        SiteImageResource logoSmall = makeSiteImage(DeploymentConsts.portalLogoSmall, cType);
        SiteImageResource logoLarge = makeSiteImage(DeploymentConsts.portalLogo, cType);
        if (logoSmall != null || logoLarge != null) {
            for (LocaleInfo li : siteLocale) {
                PortalLogoImageResource res = site.logo().$();
                res.locale().set(li.aLocale);
                res.small().set(logoSmall);
                res.large().set(logoLarge);
                site.logo().add(res);
            }
        }
        // banner image
        String[] banners = new String[] { "banner.png", "banner1.png", "banner2.png", "banner3.png", "banner4.png" };
        SiteImageResource[] siteImages = new SiteImageResource[banners.length];
        for (int i = 0; i < banners.length; i++) {
            siteImages[i] = makeSiteImage(banners[i], cType);
        }
        for (LocaleInfo li : siteLocale) {
            PortalImageSet res = site.banner().$();
            res.locale().set(li.aLocale);
            site.banner().add(res);
            // banners
            for (SiteImageResource img : siteImages) {
                if (img != null) {
                    res.imageSet().add(img);
                }
            }
        }
        // slogan image
        String fileName = "slogan.png";
        SiteImageResource siteImage = makeSiteImage(fileName, cType);
        if (siteImage != null) {
            String sloganHtml = "<img style=\"vertical-align:top; margin-top:38px\" src=\"./" + siteImage.getPrimaryKey().toString() + "/slogan.png"
                    + DeploymentConsts.siteImageResourceServletMapping + "\">";
            for (LocaleInfo li : siteLocale) {
                // crm logo
                HtmlContent cont = site.slogan().$();
                cont.locale().set(li.aLocale);
                cont.html().setValue(sloganHtml);
                site.slogan().add(cont);
            }
        }
    }

    private SiteImageResource makeSiteImage(String fileName, String mime) {
        SiteImageResource siteImage = null;
        try {
            byte raw[] = IOUtils.getBinaryResource(fileName, this.getClass());
            if (raw != null) {
                Key blobKey = BlobService.persist(raw, fileName, mime);
                siteImage = EntityFactory.create(SiteImageResource.class);
                siteImage.blobKey().setValue(blobKey);
                siteImage.fileName().setValue(fileName);
                siteImage.fileSize().setValue(raw.length);
                siteImage.contentMimeType().setValue(mime);
                siteImage.timestamp().setValue(System.currentTimeMillis());
                Persistence.service().persist(siteImage);
            }
        } catch (IOException e) {
            log.error("SiteImageResource load error", e);
        }
        return siteImage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(SiteDescriptor.class, PageDescriptor.class, PageContent.class, News.class, Testimonial.class, AvailableLocale.class);
    }

}
