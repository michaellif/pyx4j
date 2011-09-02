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
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.Locale;
import com.propertyvista.domain.site.Locale.Lang;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteLocale;
import com.propertyvista.portal.domain.dto.PropertyListDTO;

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

    public static PropertyListDTO getPropertyList() {

        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);

        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(dbCriteria);

        PropertyListDTO ret = EntityFactory.create(PropertyListDTO.class);
        for (Building building : buildings) {

            if (building.info().address().location().isNull() || building.info().address().location().getValue().getLat() == 0) {
                continue;
            }

            //In memory filters
            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = PersistenceServicesFactory.getPersistenceService().query(floorplanCriteria);

            ret.properties().add(Converter.convert(building, floorplans));
        }
        return ret;
    }

    public static String getCaption(PageDescriptor descriptor, Locale locale) {
        for (PageCaption caption : descriptor.childCaptions()) {
            if (locale.lang().getValue().equals(caption.locale().lang().getValue())) {
                return caption.caption().getValue();
            }
        }
        return descriptor.name().getValue();
    }
}
