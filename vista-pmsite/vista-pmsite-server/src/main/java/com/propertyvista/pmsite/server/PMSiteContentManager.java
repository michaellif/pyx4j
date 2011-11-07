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
import java.util.Vector;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptorChanges;
import com.propertyvista.domain.site.SiteTitles;
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.pmsite.server.model.PromoDataModel;
import com.propertyvista.pmsite.server.panels.NavigationItem;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.SearchType;
import com.propertyvista.shared.CompiledLocale;

public class PMSiteContentManager implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private SiteDescriptor siteDescriptor;

    private Map<AvailableLocale, List<News>> news;

    private Map<AvailableLocale, List<Testimonial>> testimonials;

    public PMSiteContentManager() {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        siteDescriptor = Persistence.service().retrieve(criteria);
        if (siteDescriptor == null) {
            throw new UserRuntimeException(i18n.tr("This property management site was not set-up yet"));
        }
        updatePages();
    }

    private void updatePages() {
        for (PageDescriptor descriptor : siteDescriptor.childPages()) {
            createPath(descriptor);
        }
    }

    public boolean refresh() {
        SiteDescriptorChanges latest = Persistence.service().retrieve(SiteDescriptorChanges.class, siteDescriptor._updateFlag().getPrimaryKey());
        if ((latest != null) && latest.updated().equals(siteDescriptor._updateFlag().updated())) {
            return false;
        } else {
            siteDescriptor = Persistence.service().retrieve(SiteDescriptor.class, siteDescriptor.getPrimaryKey());
            if (siteDescriptor == null) {
                siteDescriptor = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));
            }
            updatePages();
            return true;
        }
    }

    private static void createPath(PageDescriptor parent) {
        for (PageDescriptor descriptor : parent.childPages()) {
            descriptor._path().add(parent);
            createPath(descriptor);
        }
    }

    public List<AvailableLocale> getAllAvailableLocale() {
        if (allAvailableLocale == null) {
            EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
            criteria.asc(criteria.proto().displayOrder().getPath().toString());
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
            criteria.desc(criteria.proto().date().getPath().toString());
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
                    throw new Error("No page found");
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
                list.add(new NavigationItem(descriptor));
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

    private static PageDescriptor getPageDescriptor(List<PageDescriptor> pages, String pageId) {
        for (PageDescriptor descriptor : pages) {
            if (pageId != null && pageId.equals(toPageId(descriptor.name().getValue()))) {
                return descriptor;
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

    private static String toPageId(String caption) {
        return caption.toLowerCase().replaceAll("\\s+", "_").trim();
    }

    public SiteTitles getSiteTitles(AvailableLocale locale) {
        for (SiteTitles t : siteDescriptor.siteTitles()) {
            if (t.locale().equals(locale)) {
                return t;
            }
        }
        return null;
    }

    public String getCopyrightInfo(AvailableLocale locale) {
        return getSiteTitles(locale).copyright().getValue();
    }

    public List<City> getCities() {
        ArrayList<City> cityList = new ArrayList<City>();
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().hasProperties(), Boolean.TRUE));
        PropertySearchCriteria propSearch = EntityFactory.create(PropertySearchCriteria.class);
        propSearch.searchType().setValue(PropertySearchCriteria.SearchType.city);
        for (City city : EntityServicesImpl.secureQuery(criteria)) {
            // sanity check
            if (city.name().isNull() || city.province().name().isNull() || city.province().code().isNull()) {
                continue;
            }
            // make sure the city has buildings to show
            propSearch.city().setValue(city.name().getValue());
            propSearch.province().setValue(city.province().name().getValue());
            if (getPropertyList(propSearch).size() < 1) {
                continue;
            }
            cityList.add(city);
        }
        return cityList;
    }

    public Map<String, List<String>> getProvinceCityMap() {
        Map<String, List<String>> provCityMap = new HashMap<String, List<String>>();
        List<City> cities = getCities();
        for (City city : cities) {
            String cityName = city.name().getValue();
            if (cityName == null) {
                continue;
            }
            String provName = city.province().name().getValue();
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

    public static List<Building> getPropertyList(PropertySearchCriteria searchCriteria) {
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);

        // add search criteria
        if (SearchType.city.equals(searchCriteria.searchType().getValue())) {
            String city = searchCriteria.city().getValue();
            if (city != null) {
                dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().info().address().city(), city));
            }
        }
        final List<Building> buildings = Persistence.service().query(dbCriteria);
        ArrayList<Building> remove = new ArrayList<Building>();
        for (Building bld : buildings) {
            // do some sanity check
            if (bld.info().address().location().isNull() || bld.info().address().location().getValue().getLat() == 0) {
                remove.add(bld);
                continue;
            }
            if (getBuildingFloorplans(bld).size() < 1) {
                remove.add(bld);
                continue;
            }
        }
        buildings.removeAll(remove);

        return buildings;
    }

    public static boolean isPublicFileMedia(Media m) {
        return (m.type().getValue() == Media.Type.file && m.visibility().getValue() == PublicVisibilityType.global);
    }

    public static Building getBuildingDetails(long propId) {
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().id(), propId));
        List<Building> buildings = Persistence.service().query(dbCriteria);
        if (buildings.size() != 1) {
            return null;
        }
        Building bld = buildings.get(0);
        // check if we have any valid floorplans
        if (getBuildingFloorplans(bld).size() < 1) {
            return null;
        }
        // attach phone info
        Persistence.service().retrieve(bld.contacts().phones());
        return bld;
    }

    public static Map<Floorplan, List<AptUnit>> getBuildingFloorplans(Building bld) {
        final Map<Floorplan, List<AptUnit>> floorplans = new HashMap<Floorplan, List<AptUnit>>();
        EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), bld));
        for (Floorplan fp : Persistence.service().query(criteria)) {
            List<AptUnit> units = PMSiteContentManager.getBuildingAptUnits(bld, fp);
            // do some sanity check so we don't render incomplete floorplans
            if (units.size() > 0) {
                floorplans.put(fp, units);
            }
        }
        return floorplans;
    }

    public static List<AptUnit> getBuildingAptUnits(Building bld, Floorplan fp) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), bld));
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), fp));
        return Persistence.service().query(criteria);
    }

    public static List<BuildingAmenity> getBuildingAmenities(Building bld) {
        EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), bld));
        return Persistence.service().query(criteria);
    }

    public static Floorplan getFloorplanDetails(long planId) {
        EntityQueryCriteria<Floorplan> dbCriteria = EntityQueryCriteria.create(Floorplan.class);
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().id(), planId));
        List<Floorplan> plans = Persistence.service().query(dbCriteria);
        if (plans.size() != 1) {
            return null;
        }
        Floorplan fp = plans.get(0);
        if (getFloorplanUnits(fp).size() < 1) {
            return null;
        }
        return fp;
    }

    public static List<AptUnit> getFloorplanUnits(Floorplan fp) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), fp.building().getPrimaryKey().asLong()));
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), fp));
        return Persistence.service().query(criteria);
    }

    public static List<FloorplanAmenity> getFloorplanAmenities(Floorplan fp) {
        EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), fp));
        return Persistence.service().query(criteria);
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
     * Media images rendered by media servlet at /contextPath/media/{id}/{size}.jpg
     * We want to build a relative! path from the current page down to the servlet root
     */
    public static String getMediaImgUrl(long mediaId, ThumbnailSize size) {
        String servletPath = com.pyx4j.server.contexts.Context.getRequest().getServletPath();
        // shift back for every path segment; then remove one segment - for the script name
        String servletRoot = servletPath.replaceAll("/+[^/]*", "../").replaceFirst("../", "");
        return servletRoot + "media/" + mediaId + "/" + size.name() + ".jpg";
    }

    public static String getFistVisibleMediaImgUrl(List<Media> medias, ThumbnailSize size) {
        return getMediaImgUrl(getFistVisibleMedia(medias), size);
    }

    public static long getFistVisibleMedia(List<Media> medias) {
        for (Media media : medias) {
            if (media.isValuesDetached()) {
                Persistence.service().retrieve(media);
            }
            if (PublicVisibilityType.global.equals(media.visibility().getValue()) && Media.Type.file == (media.type().getValue())) {
                return media.getPrimaryKey().asLong();
            }
        }
        return 0;
    }

    public static List<Media> getVisibleMedia(List<Media> medias) {
        List<Media> mediasVisible = new Vector<Media>();
        for (Media media : medias) {
            if (media.isValuesDetached()) {
                Persistence.service().retrieve(media);
            }
            if (PublicVisibilityType.global.equals(media.visibility().getValue()) && Media.Type.file == (media.type().getValue())) {
                mediasVisible.add(media);
            }
        }
        return mediasVisible;
    }

    public List<PromoDataModel> getPromotions() {
        ArrayList<PromoDataModel> promo = new ArrayList<PromoDataModel>();

        // TODO promo building lookup
        EntityListCriteria<Building> dbCriteria = EntityListCriteria.create(Building.class);
        dbCriteria.setPageSize(4);
        List<Building> buildings = Persistence.service().query(dbCriteria);
        for (Building bld : buildings) {
            PromoDataModel item = new PromoDataModel();
            if (bld.media().isEmpty() || bld.info().address().isEmpty()) {
                continue;
            }
            item.setPropId(bld.id().getValue().asLong());
            item.setImg(getFistVisibleMediaImgUrl(bld.media(), ThumbnailSize.medium));
            item.setAddress(bld.info().address().streetNumber().getValue() + " " + bld.info().address().streetName().getValue() + ", "
                    + bld.info().address().city().getValue());
            promo.add(item);
        }

        return promo;
    }

    public static enum SocialSite {
        Facebook, Twitter, Youtube, Flickr
    }

    public Map<SocialSite, String> getSocialLinks() {
        Map<SocialSite, String> socialLinks = new HashMap<SocialSite, String>();
        socialLinks.put(SocialSite.Facebook, "http://www.facebook.com/pages/Starlight-Apartments/175770575825466");
        socialLinks.put(SocialSite.Twitter, "http://twitter.com/#!/StarlightApts");
        socialLinks.put(SocialSite.Youtube, "http://www.youtube.com/user/StarlightApts");
        socialLinks.put(SocialSite.Flickr, "http://www.flickr.com/StarlightApts");
        return socialLinks;
    }

    public int getStyleId() {
        return siteDescriptor.skin().getValue().ordinal();
    }

    public SiteDescriptor getSiteDescriptor() {
        return siteDescriptor;
    }

}
