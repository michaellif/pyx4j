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
import com.propertyvista.domain.site.ContentDescriptor;
import com.propertyvista.domain.site.ContentDescriptor.Lang;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.portal.domain.dto.PropertyListDTO;

public class PMSiteContentManager {

    public static final String PAGE_ID_PARAM_NAME = "pageId";

    public static String[] PARAMETER_NAMES = new String[10];
    static {
        for (int i = 0; i < PARAMETER_NAMES.length; i++) {
            PARAMETER_NAMES[i] = PMSiteContentManager.PAGE_ID_PARAM_NAME + i;
        }
    }

    private ContentDescriptor content;

    public PMSiteContentManager() {
        retrieveContentDescriptor(getLocale());
    }

    public void setLocale(Lang lang) {
        ((WebRequestCycle) RequestCycle.get()).getWebResponse().addCookie(new Cookie("locale", lang.name()));
        retrieveContentDescriptor(lang);
    }

    public Lang getLocale() {
        Cookie localeCookie = null;
        Cookie[] cookies = ((WebRequest) ((WebRequestCycle) RequestCycle.get()).getRequest()).getCookies();
        if (cookies == null) {
            return Lang.en;
        }
        for (Cookie cookie : cookies) {
            if ("locale".equals(cookie.getName())) {
                localeCookie = cookie;
                break;
            }
        }
        if (localeCookie != null) {
            try {
                return Lang.valueOf(localeCookie.getValue());
            } catch (Exception e) {
                return Lang.en;
            }

        } else {
            return Lang.en;
        }

    }

    private void retrieveContentDescriptor(ContentDescriptor.Lang lang) {
        EntityQueryCriteria<ContentDescriptor> criteria = EntityQueryCriteria.create(ContentDescriptor.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lang(), lang));

        ContentDescriptor content = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if ((content == null) || (content.isNull())) {
            throw new Error("Content for locale " + lang + " not found");
        }

        for (PageDescriptor descriptor : content.childPages()) {
            createPath(descriptor);
        }
        this.content = content;
    }

    private void createPath(PageDescriptor parent) {
        System.out.println(parent);
        for (PageDescriptor descriptor : parent.childPages()) {
            descriptor.path().add(parent);
            createPath(descriptor);
        }
    }

    public ContentDescriptor getContentDescriptor() {
        return content;
    }

    public PageDescriptor getStaticPageDescriptor(PageParameters parameters) {
        List<PageDescriptor> pages = content.childPages();
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
        for (int i = 0; i < descriptor.path().size(); i++) {
            params.add(PARAMETER_NAMES[i], toPageId(descriptor.path().get(descriptor.path().size() - 1 - i).name().getValue()));
        }

        params.add(PARAMETER_NAMES[descriptor.path().size()], toPageId(descriptor.name().getValue()));

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
}
