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

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.Locale;
import com.propertyvista.domain.site.Locale.Lang;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteLocale;
import com.propertyvista.pmsite.server.converter.BuildingAmenityAmenityDTOConverter;
import com.propertyvista.pmsite.server.converter.BuildingPropertyDTOConverter;
import com.propertyvista.pmsite.server.converter.FloorplanFloorplanPropertyDTOConverter;
import com.propertyvista.pmsite.server.model.ApartmentModel;
import com.propertyvista.pmsite.server.model.NewsDataModel;
import com.propertyvista.pmsite.server.model.PromoDataModel;
import com.propertyvista.pmsite.server.model.SearchCriteriaModel;
import com.propertyvista.pmsite.server.model.TestimDataModel;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.SearchType;

public class PMSiteContentManager implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PAGE_ID_PARAM_NAME = "pageId";

    public static String[] PARAMETER_NAMES = new String[10];
    static {
        for (int i = 0; i < PARAMETER_NAMES.length; i++) {
            PARAMETER_NAMES[i] = PMSiteContentManager.PAGE_ID_PARAM_NAME + i;
        }
    }

    private final SiteDescriptor site;

    private Locale locale;

    public PMSiteContentManager() {
        site = retrieveSiteDescriptor();
        locale = readLocaleFromCookie();
        if (locale == null) {
            if (site.locales().size() > 0) {
                locale = site.locales().get(0).locale();
            } else {
                throw new Error("No locales found");
            }
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        ((WebRequestCycle) RequestCycle.get()).getWebResponse().addCookie(new Cookie("locale", locale.lang().getValue().name()));
    }

    private Locale readLocaleFromCookie() {
        Cookie localeCookie = null;
        Cookie[] cookies = ((WebRequest) ((WebRequestCycle) RequestCycle.get()).getRequest()).getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("locale".equals(cookie.getName())) {
                localeCookie = cookie;
                break;
            }
        }
        if (localeCookie != null) {
            try {
                Lang lang = Lang.valueOf(localeCookie.getValue());
                for (SiteLocale locale : site.locales()) {
                    if (lang.equals(locale.locale().lang().getValue())) {
                        return locale.locale();
                    }
                }

            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private SiteDescriptor retrieveSiteDescriptor() {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);

        SiteDescriptor site = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        for (PageDescriptor descriptor : site.childPages()) {
            createPath(descriptor);
        }
        return site;
    }

    private void createPath(PageDescriptor parent) {
        System.out.println(parent);
        for (PageDescriptor descriptor : parent.childPages()) {
            descriptor._path().add(parent);
            createPath(descriptor);
        }
    }

    public SiteDescriptor getSiteDescriptor() {
        return site;
    }

    public PageDescriptor getStaticPageDescriptor(PageParameters parameters) {
        List<PageDescriptor> pages = site.childPages();
        PageDescriptor current = null;
        for (String paramName : PARAMETER_NAMES) {
            if (parameters.containsKey(paramName)) {
                current = getPageDescriptor(pages, parameters.getString(paramName));
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
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().hasProperties(), Boolean.TRUE));
        return EntityServicesImpl.secureQuery(criteria);

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

    public static PropertyListDTO getPropertyList(PropertySearchCriteria searchCriteria) {
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);

        // add search criteria
        if (SearchType.city.equals(searchCriteria.searchType().getValue())) {
            String city = searchCriteria.city().getValue();
            if (city != null) {
                dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().info().address().city(), city));
            }
        }
        List<Building> buildings = Persistence.service().query(dbCriteria);

        PropertyListDTO ret = EntityFactory.create(PropertyListDTO.class);
        for (Building building : buildings) {

            if (building.info().address().location().isNull() || building.info().address().location().getValue().getLat() == 0) {
                continue;
            }

            PropertyDTO propertyDTO = new BuildingPropertyDTOConverter().createDTO(building);
            {
                EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
                floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
                for (Floorplan floorplan : Persistence.service().query(floorplanCriteria)) {
                    propertyDTO.floorplansProperty().add(new FloorplanFloorplanPropertyDTOConverter().createDTO(floorplan));
                }
            }
            {
                EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
                for (BuildingAmenity amenity : Persistence.service().query(criteria)) {
                    propertyDTO.amenities().add(new BuildingAmenityAmenityDTOConverter().createDTO(amenity));
                }
            }
            if (!building.media().isEmpty()) {
                propertyDTO.mainMedia().setValue(building.media().get(0).getPrimaryKey());
            }
            ret.properties().add(propertyDTO);
        }
        return ret;
    }

    public static ApartmentModel getPropertyModel(SearchCriteriaModel searchCriteria) {
        ApartmentModel model = new ApartmentModel();

        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);

        // add search criteria
        if (searchCriteria.getSearchType() == SearchCriteriaModel.SearchType.City) {
            String city = searchCriteria.getCity();
            if (city != null) {
                dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().info().address().city(), city));
            }
        }
        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(dbCriteria);
        model.setBuildingList(buildings);

        for (Building building : buildings) {
            long propId = building.id().getValue().asLong();
            // add floorplans
            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = PersistenceServicesFactory.getPersistenceService().query(floorplanCriteria);
            model.putBuildingUnits(propId, floorplans);
            for (Floorplan fp : floorplans) {
                long fpId = fp.id().getValue().asLong();
                // get price and sq. footage
            }
            // add amenities
            // add media
        }

        return model;
    }

    public static String getCaption(PageDescriptor descriptor, Locale locale) {
        for (PageCaption caption : descriptor.caption()) {
            if (locale.lang().getValue().equals(caption.locale().lang().getValue())) {
                return caption.caption().getValue();
            }
        }
        return descriptor.name().getValue();
    }

    public static String getMediaImgUrl(long mediaId, String size) {
        return "/vista/media/" + mediaId + "/" + size + ".png";
    }

    public static List<NewsDataModel> getNews() {
        ArrayList<NewsDataModel> news = new ArrayList<NewsDataModel>();
        NewsDataModel item = new NewsDataModel();
        item.setDate(null)
                .setHeadline("Indirect references to such addresses should be contained within theenterprise.")
                .setText(
                        "Private hosts can communicate with all other hostsinside the enterprise, both public and private.  While nothaving external (outside of the enterprise) IP connectivity privatehosts can still have access to external services via mediatinggateways. Such hosts will use the private address spacedefined above.");
        news.add(item);
        item = new NewsDataModel();
        item.setDate(null)
                .setHeadline(" However, they cannothave IP connectivity to any host outside of the enterprise.")
                .setText(
                        "While nothaving external (outside of the enterprise) IP connectivity privatehosts can still have access to external services via mediatinggateways.  In particular, Internet service providers should takemeasures to prevent such leakage.");
        news.add(item);

        return news;
    }

    public static List<PromoDataModel> getPromotions() {
        ArrayList<PromoDataModel> promo = new ArrayList<PromoDataModel>();

        // do promo building lookup
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(dbCriteria);
        for (Building bld : buildings) {
            PromoDataModel item = new PromoDataModel();
            if (bld.media().isEmpty() || bld.info().address().isEmpty()) {
                continue;
            }
            item.setImg(getMediaImgUrl(bld.media().get(0).getPrimaryKey().asLong(), "medium"));
            item.setAddress(bld.info().address().streetNumber().getValue() + " " + bld.info().address().streetName().getValue() + ", "
                    + bld.info().address().city().getValue());
            promo.add(item);
            if (promo.size() >= 4) {
                break;
            }
        }

        return promo;
    }

    public static List<TestimDataModel> getTestimonials() {
        ArrayList<TestimDataModel> testim = new ArrayList<TestimDataModel>();
        TestimDataModel item = new TestimDataModel();
        item.setName("Pinoccio").setQuote("Indirect references to such addresses should be contained within theenterprise.");
        testim.add(item);
        item = new TestimDataModel();
        item.setName("Seven Dwarfs").setQuote(" However, they cannothave IP connectivity to any host outside of the enterprise.");
        testim.add(item);

        return testim;
    }
}
