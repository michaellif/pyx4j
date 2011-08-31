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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.ContentDescriptor;
import com.propertyvista.domain.site.PageDescriptor;

public class PMSiteContentManager {

    public static final String PAGE_ID_PARAM_NAME = "pageId";

    public static String[] PARAMETER_NAMES = new String[10];
    static {
        for (int i = 0; i < PARAMETER_NAMES.length; i++) {
            PARAMETER_NAMES[i] = PMSiteContentManager.PAGE_ID_PARAM_NAME + i;
        }
    }

    private final ContentDescriptor content;

    public PMSiteContentManager() {
        content = retrieveContentDescriptor();
    }

    public ContentDescriptor retrieveContentDescriptor() {
        EntityQueryCriteria<ContentDescriptor> criteria = EntityQueryCriteria.create(ContentDescriptor.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lang(), ContentDescriptor.Lang.english));

        ContentDescriptor content = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if ((content == null) || (content.isNull())) {
            throw new Error("Content for locale " + ContentDescriptor.Lang.english + " not found");
        }
        return content;
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
            if (pageId != null && pageId.equals(toPageId(descriptor.caption().getValue()))) {
                return descriptor;
            }
        }
        return null;
    }

    public PageParameters getStaticPageParams(PageDescriptor descriptor) {
        List<PageDescriptor> path = new ArrayList<PageDescriptor>();

        PageDescriptor parent = descriptor;
        while (!parent.isNull()) {
            path.add(parent);
            parent = parent.parent();
        }

        PageParameters params = new PageParameters();
        for (int i = 0; i < path.size() - 1; i++) {
            params.add(PARAMETER_NAMES[i], toPageId(path.get(path.size() - 2 - i).caption().getValue()));
        }

        return params;
    }

    private static String toPageId(String caption) {
        return caption.toLowerCase().replaceAll("\\s+", "_").trim();
    }

    public static List<City> getCities() {
        String[][] Cities = { { "Napanee", "Ontario", "ON" }, { "Kingston", "Ontario", "ON" }, { "Ottawa", "Ontario", "ON" },
                { "North York", "Ontario", "ON" }, { "Toronto", "Ontario", "ON" }, { "Mississauga", "Ontario", "ON" }, { "Etobicoke", "Ontario", "ON" },
                { "Oshawa", "Ontario", "ON" }, { "Kitchener", "Ontario", "ON" }, { "St. Catherines", "Ontario", "ON" }, { "Niagara Falls", "Ontario", "ON" },
                { "Dundas", "Ontario", "ON" }, { "Guelph", "Ontario", "ON" }, { "Waterloo", "Ontario", "ON" }, { "London", "Ontario", "ON" },
                { "Trenton", "Ontario", "ON" }, { "Listowel", "Ontario", "ON" }, { "Halifax", "Nova Scotia", "NS" }, { "Dartmouth", "Nova Scotia", "NS" },
                { "St. Johns", "New Brunswick", "NB" }, { "Mission", "British Columbia", "BC" }, { "Abbotsford", "British Columbia", "BC" },
                { "Coquitlam", "British Columbia", "BC" }, { "New Westminster", "British Columbia", "BC" }, { "Port Moody", "British Columbia", "BC" },
                { "Victoria", "British Columbia", "BC" }, { "Montreal", "Quebec", "QB" }, { "Pointe-Claire", "Quebec", "QB" }, { "Longueuil", "Quebec", "QB" },
                { "Sainte-Laurent", "Quebec", "QB" }, { "Calgary", "Alberta", "AB" }, };
        List<City> cityList = new ArrayList<City>();
        for (String[] _city : Cities) {
            City city = EntityFactory.create(City.class);
            city.name().setValue(_city[0]);
            city.province().name().setValue(_city[1]);
            city.province().code().setValue(_city[2]);
            cityList.add(city);
        }

        return cityList;
    }
}
