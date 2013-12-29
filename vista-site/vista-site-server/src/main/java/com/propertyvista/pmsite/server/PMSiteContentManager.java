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
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.NamespaceNotFoundException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.HtmlContent;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageDescriptor.Type;
import com.propertyvista.domain.site.PageMetaTags;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptorChanges;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.SiteImageSet;
import com.propertyvista.domain.site.SiteLogoImageResource;
import com.propertyvista.domain.site.SiteTitles;
import com.propertyvista.domain.site.SocialLink;
import com.propertyvista.domain.site.SocialLink.SocialSite;
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.pmsite.server.model.PromoDataModel;
import com.propertyvista.pmsite.server.panels.NavigationItem;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.shared.SiteWasNotSetUpUserRuntimeException;
import com.propertyvista.shared.i18n.CompiledLocale;

public class PMSiteContentManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(PMSiteContentManager.class);

    private static final I18n i18n = I18n.get(PMSiteContentManager.class);

    public static final String PAGE_ID_PARAM_NAME = "pageId";

    public static String[] PARAMETER_NAMES = new String[10];

    public static StringBuilder PARAMETER_PATH = new StringBuilder();

    static {
        for (int i = 0; i < PARAMETER_NAMES.length; i++) {
            PARAMETER_NAMES[i] = PMSiteContentManager.PAGE_ID_PARAM_NAME + i;
            PARAMETER_PATH.append("/#{").append(PARAMETER_NAMES[i]).append("}");
        }
    }

    public static final int DEFAULT_STYLE_ID = 0;

    private List<AvailableLocale> allAvailableLocale;

    private final SiteDescriptor siteDescriptor;

    private PMSiteCssManager cssManager;

    private Map<AvailableLocale, List<News>> news;

    private Map<AvailableLocale, List<Testimonial>> testimonials;

    private boolean siteUpdated = false;

    public PMSiteContentManager() {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        SiteDescriptor foundSiteDescriptor;
        try {
            foundSiteDescriptor = Persistence.service().retrieve(criteria);
        } catch (NamespaceNotFoundException e) {
            foundSiteDescriptor = null;
        }
        if ((foundSiteDescriptor == null) || (!foundSiteDescriptor.enabled().isBooleanTrue() && !foundSiteDescriptor.residentPortalEnabled().isBooleanTrue())) {
            throw new SiteWasNotSetUpUserRuntimeException(i18n.tr("This property management site was not set-up yet"));
        }
        siteDescriptor = foundSiteDescriptor;
        for (PageDescriptor descriptor : siteDescriptor.childPages()) {
            createPath(descriptor);
        }
    }

    protected void setSiteUpdatedFlag() {
        siteUpdated = true;
    }

    public void clearSiteUpdatedFlag() {
        siteUpdated = false;
    }

    public boolean isSiteUpdated() {
        return siteUpdated;
    }

    public boolean refreshRequired() {
        SiteDescriptorChanges latest = Persistence.service().retrieve(SiteDescriptorChanges.class, siteDescriptor._updateFlag().getPrimaryKey());
        if ((latest != null) && latest.updated().equals(siteDescriptor._updateFlag().updated())) {
            return false;
        } else {
            return true;
        }
    }

    private static void createPath(PageDescriptor parent) {
        for (PageDescriptor descriptor : parent.childPages()) {
            descriptor._path().add(parent);
            createPath(descriptor);
        }
    }

    public PMSiteCssManager getCssManager() {
        if (cssManager == null) {
            cssManager = new PMSiteCssManager(this);
        }
        return cssManager;
    }

    public List<HomePageGadget> getNarrowAreaGadgets() {
        return siteDescriptor.homePageGadgetsNarrow();
    }

    public List<HomePageGadget> getWideAreaGadgets() {
        return siteDescriptor.homePageGadgetsWide();
    }

    public boolean isMapEnabled() {
        if (siteDescriptor.disableMapView().isNull()) {
            return true;
        } else {
            return !siteDescriptor.disableMapView().getValue();
        }
    }

    public boolean isAptListEnabled() {
        return !siteDescriptor.disableBuildingDetails().isBooleanTrue();
    }

    public List<AvailableLocale> getAllAvailableLocale() {
        if (allAvailableLocale == null) {
            EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
            criteria.asc(criteria.proto().displayOrder());
            allAvailableLocale = Persistence.service().query(criteria);
        }
        if (allAvailableLocale == null) {
            //Working on Empty DB - create default locale
            AvailableLocale l = EntityFactory.create(AvailableLocale.class);
            l.lang().setValue(CompiledLocale.en);
            l.displayOrder().setValue(1);
            allAvailableLocale = new ArrayList<AvailableLocale>();
            allAvailableLocale.add(l);
        }
        return allAvailableLocale;
    }

    public List<News> getNews(AvailableLocale locale) {
        if (news == null) {
            news = new HashMap<AvailableLocale, List<News>>();
        }
        if (news.get(locale) == null) {
            EntityListCriteria<News> criteria = EntityListCriteria.create(News.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().locale(), locale));
            criteria.desc(criteria.proto().date());
            criteria.setPageSize(4);
            criteria.setPageNumber(0);
            news.put(locale, Persistence.service().query(criteria));
        }
        return news.get(locale);
    }

    public List<Testimonial> getTestimonials(AvailableLocale locale) {
        if (testimonials == null) {
            testimonials = new HashMap<AvailableLocale, List<Testimonial>>();
        }
        if (testimonials.get(locale) == null) {
            EntityQueryCriteria<Testimonial> criteria = EntityQueryCriteria.create(Testimonial.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().locale(), locale));
            testimonials.put(locale, Persistence.service().query(criteria));
        }
        return testimonials.get(locale);
    }

    public PageDescriptor getStaticPageDescriptor(PageParameters parameters) {
        List<PageDescriptor> pages = siteDescriptor.childPages();
        PageDescriptor current = null;
        for (String paramName : PARAMETER_NAMES) {
            if (!parameters.get(paramName).isEmpty()) {
                current = getPageDescriptor(pages, parameters.get(paramName).toString());
                if (current == null) {
                    throw new PMSiteInternalException(new Error("Static Page Not Found"));
                }
                pages = current.childPages();
            }
        }
        return current;
    }

    public List<NavigationItem> getMainNavigItems() {
        List<NavigationItem> list = new ArrayList<NavigationItem>();
        for (int i = 0; i < 4 && i < siteDescriptor.childPages().size(); i++) {
            PageDescriptor descriptor = siteDescriptor.childPages().get(i);
            if (descriptor != null) {
                // exclude resident portal from menu items
                if (descriptor.type().getValue() != Type.residents) {
                    list.add(new NavigationItem(descriptor));
                }
            } else {
                break;
            }
        }
        return list;
    }

    public List<NavigationItem> getFooterNavigItems() {
        List<NavigationItem> list = new ArrayList<NavigationItem>();
        for (int i = 4; i < siteDescriptor.childPages().size(); i++) {
            PageDescriptor descriptor = siteDescriptor.childPages().get(i);
            if (descriptor != null) {
                list.add(new NavigationItem(descriptor));
            } else {
                break;
            }
        }
        return list;
    }

    public static List<NavigationItem> getNavigItems(PageDescriptor content) {
        List<NavigationItem> list = new ArrayList<NavigationItem>();
        if (content != null) {
            if (content.childPages().size() > 0) {
                list.add(new NavigationItem(content));
            }
            for (PageDescriptor descriptor : content.childPages()) {
                list.add(new NavigationItem(descriptor));
            }
        }
        return list;
    }

    public NavigationItem getSecondaryNavigItem(String pageId) {
        for (PageDescriptor pd : siteDescriptor.childPages()) {
            // look through the secondary pages only
            PageDescriptor pd2 = getPageDescriptor(pd.childPages(), pageId);
            if (pd2 != null) {
                return new NavigationItem(pd2);
            }
        }
        return null;
    }

    private static PageDescriptor getPageDescriptor(List<PageDescriptor> pages, String pageId) {
        if (pageId != null) {
            for (PageDescriptor descriptor : pages) {
                if (pageId.equals(toPageId(descriptor.name().getValue()))) {
                    return descriptor;
                }
            }
        }
        return null;
    }

    public static PageParameters getStaticPageParams(PageDescriptor descriptor) {

        PageParameters params = new PageParameters();
        for (int i = 0; i < descriptor._path().size(); i++) {
            params.add(PARAMETER_NAMES[i], toPageId(descriptor._path().get(descriptor._path().size() - 1 - i).name().getValue()));
        }

        params.add(PARAMETER_NAMES[descriptor._path().size()], toPageId(descriptor.name().getValue()));

        return params;
    }

    public static String toPageId(String caption) {
        return caption.toLowerCase().replaceAll("\\s+", "_").trim();
    }

    public SiteTitles getSiteTitles(AvailableLocale locale) {
        if (locale == null) {
            throw new NullPointerException("locale is null");
        }
        for (SiteTitles t : siteDescriptor.siteTitles()) {
            if (t.locale().equals(locale)) {
                return t;
            }
        }
        throw new Error("Locale " + locale.lang().getStringView() + " not available");
    }

    public PageMetaTags getMetaTags(AvailableLocale locale) {
        if (locale == null) {
            throw new NullPointerException("locale is null");
        }
        for (PageMetaTags t : siteDescriptor.metaTags()) {
            if (t.locale().equals(locale)) {
                return t;
            }
        }
        throw new Error("Locale " + locale.lang().getStringView() + " not available");
    }

    public String getCopyrightInfo(AvailableLocale locale) {
        return getSiteTitles(locale).copyright().getValue();
    }

    public List<City> getCities() {
        ArrayList<City> cityList = new ArrayList<City>();
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().hasProperties(), Boolean.TRUE));
        for (City city : Persistence.secureQuery(criteria.asc(criteria.proto().name()))) {
            // sanity check
            if (city.name().isNull() || city.province().name().isNull() || city.province().code().isNull()) {
                continue;
            }
            cityList.add(city);
        }
        return cityList;
    }

    public Map<String, List<String>> getProvinceCityMap() {
        return getProvinceCityMap(false);
    }

    public Map<String, List<String>> getProvinceCityMap(boolean useCode) {
        Map<String, List<String>> provCityMap = new HashMap<String, List<String>>();
        List<City> cities = getCities();
        for (City city : cities) {
            String cityName = city.name().getValue();
            if (cityName == null) {
                continue;
            }
            String provName = useCode ? city.province().code().getValue() : city.province().name().getValue();
            if (provName == null) {
                continue;
            }
            List<String> cityList = provCityMap.get(provName);
            if (cityList == null) {
                cityList = new ArrayList<String>();
                provCityMap.put(provName, cityList);
            }
            cityList.add(cityName);
        }
        return provCityMap;
    }

    public String getCaption(PageDescriptor descriptor, AvailableLocale locale) {
        if (descriptor == null) {
            return "";
        }
        for (PageCaption caption : descriptor.caption()) {
            if (locale.lang().getValue().equals(caption.locale().lang().getValue())) {
                return caption.caption().getValue();
            }
        }
        return descriptor.name().getValue();
    }

    public String getSecondaryCaption(PageDescriptor descriptor, AvailableLocale locale) {
        if (descriptor == null) {
            return "";
        }
        for (PageCaption caption : descriptor.caption()) {
            if (locale.lang().getValue().equals(caption.locale().lang().getValue())) {
                return caption.secondaryCaption().getValue();
            }
        }
        return descriptor.name().getValue();
    }

    /*
     * Media images rendered by media servlet at /contextPath/site/media/{id}/{size}.jpg
     * We want to build a relative! path from the current page down to the servlet root
     */
    public static String getPortalContextPath() {
        return ServletUtils.getRelativeServletPath(com.pyx4j.server.contexts.Context.getRequest(), "/" + VistaApplication.site + "/");
    }

    public static String getMediaImgUrl(long mediaId, ThumbnailSize size) {
        return getPortalContextPath() + DeploymentConsts.mediaImagesServletMapping + mediaId + "/" + size.name() + "." + ImageConsts.THUMBNAIL_TYPE;
    }

    public static String getSiteImageResourceUrl(SiteImageResource resource) {
        if (resource == null) {
            throw new Error("SiteImageResource cannot be null.");
        }
        return getPortalContextPath() + resource.id().getStringView() + "/" + resource.file().fileName().getStringView()
                + DeploymentConsts.siteImageResourceServletMapping;
    }

    public static String getFistVisibleMediaImgUrl(List<MediaFile> medias, ThumbnailSize size) {
        return getMediaImgUrl(getFistVisibleMedia(medias), size);
    }

    public static long getFistVisibleMedia(List<MediaFile> medias) {
        for (MediaFile media : medias) {
            if (media.isValueDetached()) {
                Persistence.service().retrieve(media);
            }
            if (PublicVisibilityType.global.equals(media.visibility().getValue())) {
                return media.getPrimaryKey().asLong();
            }
        }
        return 0;
    }

    public static List<MediaFile> getVisibleMedia(List<MediaFile> medias) {
        List<MediaFile> mediasVisible = new Vector<MediaFile>();
        for (MediaFile media : medias) {
            if (media.isValueDetached()) {
                Persistence.service().retrieve(media);
            }
            if (PublicVisibilityType.global.equals(media.visibility().getValue())) {
                mediasVisible.add(media);
            }
        }
        return mediasVisible;
    }

    public List<PromoDataModel> getPromotions(String city) {
        ArrayList<PromoDataModel> promo = new ArrayList<PromoDataModel>();

        // TODO promo building lookup
        EntityListCriteria<Building> dbCriteria = EntityListCriteria.create(Building.class);
        // account for visibility type
        dbCriteria.eq(dbCriteria.proto().marketing().visibility(), PublicVisibilityType.global);
        if (city != null) {
            dbCriteria.eq(dbCriteria.proto().info().address().city(), city);
        }
        dbCriteria.setPageSize(4);
        List<Building> buildings = PropertyFinder.getPropertyList(null, dbCriteria);
        for (Building building : buildings) {
            PromoDataModel item = new PromoDataModel();
            if (building.media().isEmpty() || building.info().address().isEmpty()) {
                continue;
            }
            item.setPropCode(building.propertyCode().getValue());
            item.setImg(getFistVisibleMediaImgUrl(building.media(), ThumbnailSize.medium));
            item.setAddress(building.info().address().streetNumber().getValue() + " " + building.info().address().streetName().getValue() + ", "
                    + building.info().address().city().getValue());
            promo.add(item);
        }

        return promo;
    }

    public PageMetaTags getCityPageMetaTags(AvailableLocale curLocale, City city) {
        PageMetaTags meta = EntityFactory.create(PageMetaTags.class);
        meta.title().setValue(i18n.tr("Rent Apartments in {0}, {1}", city.name().getValue(), city.province().name().getValue()));
        meta.description().setValue(
                i18n.tr("{0} apartments for rent by {1}, find your next rental apartment in {0} fast and easy using our apartment search.", city.name()
                        .getValue(), getSiteTitles(curLocale).residentPortalTitle().getValue()));
        meta.keywords().setValue(
                i18n.tr("rent {0} apartments, apartments {0}, apartments in {0}, {0} rental apartments, {0} apartments for rent", city.name().getValue()));
        return meta;
    }

    public Map<SocialSite, String> getSocialLinks() {
        Map<SocialSite, String> socialLinks = new HashMap<SocialSite, String>();
        for (SocialLink link : getSiteDescriptor().socialLinks()) {
            socialLinks.put(link.socialSite().getValue(), link.siteUrl().getValue());
        }
        return socialLinks;
    }

    public int getStyleId() {
        return siteDescriptor.skin().getValue().ordinal();
    }

    public String getSiteSkin() {
        return "skin" + getStyleId();
    }

    public SiteDescriptor getSiteDescriptor() {
        return siteDescriptor;
    }

    public SiteLogoImageResource getSiteLogo(AvailableLocale locale) {
        SiteLogoImageResource logo = null;
        String lang = locale.lang().getValue().name();
        IList<SiteLogoImageResource> allLogos = getSiteDescriptor().logo();
        for (SiteLogoImageResource logoRc : allLogos) {
            if (logoRc.locale().lang().getValue().name().equals(lang)) {
                logo = logoRc;
            }
        }
        if (logo == null && allLogos.size() > 0) {
            logo = allLogos.get(0);
        }
        return logo;
    }

    public SiteImageResource getSiteBanner(AvailableLocale locale) {
        List<SiteImageResource> bannerSet = null;
        String lang = locale.lang().getValue().name();
        IList<SiteImageSet> allBanners = getSiteDescriptor().banner();
        for (SiteImageSet banner : allBanners) {
            if (banner.locale().lang().getValue().name().equals(lang)) {
                bannerSet = banner.imageSet();
                break;
            }
        }
        if (bannerSet == null && allBanners.size() > 0) {
            bannerSet = allBanners.get(0).imageSet();
        }
        return bannerSet.size() > 0 ? bannerSet.get(new Random().nextInt(bannerSet.size())) : null;
    }

    public String getSiteSlogan(AvailableLocale locale) {
        String slogan = null;
        String lang = locale.lang().getValue().name();
        IList<HtmlContent> allSlogans = getSiteDescriptor().slogan();
        for (HtmlContent sloganRc : allSlogans) {
            if (sloganRc.locale().lang().getValue().name().equals(lang)) {
                slogan = sloganRc.html().getValue();
            }
        }
        if (slogan == null && allSlogans.size() > 0) {
            slogan = allSlogans.get(0).html().getValue();
        }
        return slogan;
    }

    public String getPmcInfo(AvailableLocale locale) {
        String pmcInfo = null;
        String lang = locale.lang().getValue().name();
        IList<HtmlContent> allInfo = getSiteDescriptor().pmcInfo();
        for (HtmlContent infoRc : allInfo) {
            if (infoRc.locale().lang().getValue().name().equals(lang)) {
                pmcInfo = infoRc.html().getValue();
            }
        }
        if (pmcInfo == null && allInfo.size() > 0) {
            pmcInfo = allInfo.get(0).html().getValue();
        }
        return pmcInfo;
    }

    public String poveredByUrl() {
        return "http://www.propertyvista.com";
    }

    public boolean isWebsiteEnabled() {
        return siteDescriptor.enabled().isBooleanTrue();
    }

    public boolean isResidentOnlyMode() {
        return siteDescriptor.residentPortalEnabled().isBooleanTrue() && !siteDescriptor.enabled().isBooleanTrue();
    }
}
