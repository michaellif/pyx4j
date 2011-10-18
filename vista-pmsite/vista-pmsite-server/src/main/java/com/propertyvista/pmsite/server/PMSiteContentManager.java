/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.i18n.server.I18nManager;

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
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.pmsite.server.model.PromoDataModel;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.SearchType;
import com.propertyvista.shared.CompiledLocale;

/*
 * Uses property wrapper PMSiteContent that will lazy initialize properties on first access.
 * This will help to eliminate direct access to uninitialized properties and ensure that all
 * properties are initialized before returning to the outer class. 
 */
public class PMSiteContentManager implements Serializable {

    private static final long serialVersionUID = 1L;

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

    // Property wrapper with lazy initialization of its properties on first request
    private static class PMSiteContent {
        private static SiteDescriptor site;

        private static Integer styleId = null;

        private static List<News> news;

        private static List<Testimonial> testimonials;

        private static List<AvailableLocale> allAvailableLocale;

        private static AvailableLocale locale;

        public static int getStyleId() {
            if (styleId == null) {
                styleId = DEFAULT_STYLE_ID;
                Cookie pmsiteStyleCookie = null;
                List<Cookie> cookies = ((WebRequest) RequestCycle.get().getRequest()).getCookies();
                if (cookies == null) {
                    return styleId;
                }
                for (Cookie cookie : cookies) {
                    if ("pmsiteStyle".equals(cookie.getName())) {
                        pmsiteStyleCookie = cookie;
                        break;
                    }
                }
                if (pmsiteStyleCookie != null) {
                    styleId = Integer.valueOf(pmsiteStyleCookie.getValue());
                }
            }
            return styleId;
        }

        public static void setStyleId(int id) {
            styleId = id;
            ((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie("pmsiteStyle", String.valueOf(styleId)));
        }

        public static AvailableLocale getLocale() {
            if (locale == null) {
                locale = readLocaleFromCookie();
            }
            return locale;
        }

        public static void setLocale(AvailableLocale l) {
            locale = l;
            ((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie("locale", locale.lang().getValue().name()));
        }

        public static List<AvailableLocale> getAllAvailableLocale() {
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

        public static SiteDescriptor getSiteDescriptor() {
            if (site == null) {
                EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
                site = Persistence.service().retrieve(criteria);
                if (site == null) {
                    //Working on Empty DB
                    site = EntityFactory.create(SiteDescriptor.class);
                    // TODO populate with default values, such as color scheme etc
                }

                for (PageDescriptor descriptor : site.childPages()) {
                    createPath(descriptor);
                }
            }
            return site;
        }

        public static List<News> getNews() {
            if (news == null) {
                EntityListCriteria<News> criteria = EntityListCriteria.create(News.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().locale().lang(), getLocale().lang().getValue()));
                criteria.desc(criteria.proto().date().getPath().toString());
                criteria.setPageSize(4);
                criteria.setPageNumber(0);
                news = Persistence.service().query(criteria);
            }
            return news;
        }

        public static List<Testimonial> getTestimonials() {
            if (testimonials == null) {
                EntityQueryCriteria<Testimonial> criteria = EntityQueryCriteria.create(Testimonial.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().locale().lang(), getLocale().lang().getValue()));
                testimonials = Persistence.service().query(criteria);
            }
            return testimonials;
        }

        private static void createPath(PageDescriptor parent) {
//          System.out.println(parent);
            for (PageDescriptor descriptor : parent.childPages()) {
                descriptor._path().add(parent);
                createPath(descriptor);
            }
        }

        private static AvailableLocale readLocaleFromCookie() {

            Locale locale = I18nManager.getThreadLocale();
            try {
                CompiledLocale lang = CompiledLocale.valueOf(locale.getLanguage() + "_" + locale.getCountry());
                for (AvailableLocale l : getAllAvailableLocale()) {
                    if (lang.equals(l.lang().getValue())) {
                        return l;
                    }
                }
            } catch (IllegalArgumentException ignore) {
            }

            for (AvailableLocale l : getAllAvailableLocale()) {
                if (locale.getLanguage().equals(l.lang().getValue().name())) {
                    return l;
                }
            }
            // Locale not found, select the first one.
            return getAllAvailableLocale().get(0);
        }

    }

    public static int getSiteStyle() {
        return PMSiteContent.getStyleId();
    }

    public static void setSiteStyle(int id) {
        PMSiteContent.setStyleId(id);
    }

    public static AvailableLocale getLocale() {
        return PMSiteContent.getLocale();
    }

    public static void setLocale(AvailableLocale locale) {
        PMSiteContent.setLocale(locale);
    }

    public static List<AvailableLocale> getAllAvailableLocale() {
        return PMSiteContent.getAllAvailableLocale();
    }

    public static SiteDescriptor getSiteDescriptor() {
        return PMSiteContent.getSiteDescriptor();
    }

    public static List<News> getNews() {
        return PMSiteContent.getNews();
    }

    public static List<Testimonial> getTestimonials() {
        return PMSiteContent.getTestimonials();
    }

    public static PageDescriptor getStaticPageDescriptor(PageParameters parameters) {
        List<PageDescriptor> pages = PMSiteContent.getSiteDescriptor().childPages();
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

    public static String getCopyrightInfo() {
//        return "© Starlight Apartments 2011";
        return getSiteDescriptor().copyright().getValue();
    }

    public static List<City> getCities() {
        ArrayList<City> cityList = new ArrayList<City>();
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().hasProperties(), Boolean.TRUE));
        for (City city : EntityServicesImpl.secureQuery(criteria)) {
            if (!city.name().isNull() && !city.province().name().isNull() && !city.province().code().isNull()) {
                cityList.add(city);
            }
        }
        return cityList;
    }

    public static Map<String, List<String>> getProvinceCityMap() {
        Map<String, List<String>> provCityMap = new HashMap<String, List<String>>();
        List<City> cities = PMSiteContentManager.getCities();
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

    public static String getCaption(PageDescriptor descriptor, AvailableLocale locale) {
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

    public static List<PromoDataModel> getPromotions() {
        ArrayList<PromoDataModel> promo = new ArrayList<PromoDataModel>();

        // do promo building lookup
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        dbCriteria.add(new PropertyCriterion(dbCriteria.proto().id(), Restriction.GREATER_THAN, 10));
        List<Building> buildings = Persistence.service().query(dbCriteria);
        for (Building bld : buildings) {
            PromoDataModel item = new PromoDataModel();
            if (bld.media().isEmpty() || bld.info().address().isEmpty()) {
                continue;
            }
            item.setPropId(bld.id().getValue().asLong());
            item.setImg(getMediaImgUrl(bld.media().get(0).getPrimaryKey().asLong(), ThumbnailSize.medium));
            item.setAddress(bld.info().address().streetNumber().getValue() + " " + bld.info().address().streetName().getValue() + ", "
                    + bld.info().address().city().getValue());
            promo.add(item);
            if (promo.size() >= 4) {
                break;
            }
        }

        return promo;
    }

}
