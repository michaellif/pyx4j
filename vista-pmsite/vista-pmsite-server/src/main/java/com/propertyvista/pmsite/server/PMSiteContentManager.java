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

    private final SiteDescriptor site;

    private final List<News> news;

    private final List<Testimonial> testimonials;

    List<AvailableLocale> allAvailableLocale;

    private AvailableLocale locale;

    public PMSiteContentManager() {
        site = retrieveSiteDescriptor();

        EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
        criteria.asc(criteria.proto().displayOrder().getPath().toString());
        allAvailableLocale = Persistence.service().query(criteria);

        locale = readLocaleFromCookie();

        news = retrieveNews();
        testimonials = retrieveTestimonials();

    }

    public AvailableLocale getLocale() {
        return locale;
    }

    public List<AvailableLocale> getAllAvailableLocale() {
        return allAvailableLocale;
    }

    public void setLocale(AvailableLocale locale) {
        this.locale = locale;
        ((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie("locale", locale.lang().getValue().name()));
    }

    private AvailableLocale readLocaleFromCookie() {
        Cookie localeCookie = null;
        List<Cookie> cookies = ((WebRequest) RequestCycle.get().getRequest()).getCookies();
        if (cookies == null) {
            return allAvailableLocale.get(0);
        }
        for (Cookie cookie : cookies) {
            if ("locale".equals(cookie.getName())) {
                localeCookie = cookie;
                break;
            }
        }

        if ((localeCookie != null) && (localeCookie.getValue() != null)) {
            CompiledLocale lang = null;
            try {
                lang = CompiledLocale.valueOf(localeCookie.getValue());
            } catch (IllegalArgumentException ignore) {
            }

            if (lang != null) {
                for (AvailableLocale l : allAvailableLocale) {
                    if (lang.equals(l.lang().getValue())) {
                        return l;
                    }
                }
            }
        }

        // Locale not found, select the first one.
        return allAvailableLocale.get(0);
    }

    private SiteDescriptor retrieveSiteDescriptor() {

        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        SiteDescriptor site = Persistence.service().retrieve(criteria);
        if (site == null) {
            //Working on Empty DB
            site = EntityFactory.create(SiteDescriptor.class);
        }

        for (PageDescriptor descriptor : site.childPages()) {
            createPath(descriptor);
        }
        return site;
    }

    public SiteDescriptor getSiteDescriptor() {
        return site;
    }

    private List<News> retrieveNews() {
        EntityListCriteria<News> criteria = EntityListCriteria.create(News.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().locale().lang(), locale.lang().getValue()));
        criteria.desc(criteria.proto().date().getPath().toString());
        criteria.setPageSize(4);
        criteria.setPageNumber(0);
        return Persistence.service().query(criteria);
    }

    public List<News> getNews() {
        return news;
    }

    private List<Testimonial> retrieveTestimonials() {
        EntityQueryCriteria<Testimonial> criteria = EntityQueryCriteria.create(Testimonial.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().locale().lang(), locale.lang().getValue()));
        return Persistence.service().query(criteria);
    }

    public List<Testimonial> getTestimonials() {
        return testimonials;
    }

    private void createPath(PageDescriptor parent) {
//        System.out.println(parent);
        for (PageDescriptor descriptor : parent.childPages()) {
            descriptor._path().add(parent);
            createPath(descriptor);
        }
    }

    public PageDescriptor getStaticPageDescriptor(PageParameters parameters) {
        List<PageDescriptor> pages = site.childPages();
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

    private PageDescriptor getPageDescriptor(List<PageDescriptor> pages, String pageId) {
        for (PageDescriptor descriptor : pages) {
            if (pageId != null && pageId.equals(toPageId(descriptor.name().getValue()))) {
                return descriptor;
            }
        }
        return null;
    }

    public PageParameters getStaticPageParams(PageDescriptor descriptor) {

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
