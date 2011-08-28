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
import com.propertyvista.domain.site.PageDescriptor;

public class PMSiteContentManager {

    public static final String PAGE_ID_PARAM_NAME = "pageId";

    public static String[] PARAMETER_NAMES = new String[10];
    static {
        for (int i = 0; i < PARAMETER_NAMES.length; i++) {
            PARAMETER_NAMES[i] = PMSiteContentManager.PAGE_ID_PARAM_NAME + i;
        }
    }

    private final PageDescriptor landing;

    public PMSiteContentManager() {
        landing = retrieveLandingPageDescriptor();
    }

    public PageDescriptor retrieveLandingPageDescriptor() {
        EntityQueryCriteria<PageDescriptor> criteria = EntityQueryCriteria.create(PageDescriptor.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), PageDescriptor.Type.landing));

        PageDescriptor landing = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if ((landing == null) || (landing.isNull())) {
            throw new Error("Landing page not found");
        }

        EntityQueryCriteria<PageDescriptor> childPagesCriteria = EntityQueryCriteria.create(PageDescriptor.class);
        childPagesCriteria.add(PropertyCriterion.eq(childPagesCriteria.proto().parent(), landing));
        landing.childPages().addAll(PersistenceServicesFactory.getPersistenceService().query(childPagesCriteria));

        // Return level 2 children
        for (PageDescriptor c : landing.childPages()) {
            EntityQueryCriteria<PageDescriptor> c2 = EntityQueryCriteria.create(PageDescriptor.class);
            c2.add(PropertyCriterion.eq(c2.proto().parent(), c));
            c.childPages().addAll(PersistenceServicesFactory.getPersistenceService().query(c2));
        }

        return landing;
    }

    public PageDescriptor getLandingPage() {
        return landing;
    }

    public PageDescriptor getStaticPageDescriptor(PageParameters parameters) {
        PageDescriptor descriptor = landing;
        for (String paramName : PARAMETER_NAMES) {
            if (parameters.containsKey(paramName)) {
                descriptor = getChild(descriptor, parameters.getString(paramName));
            } else {
                return landing.equals(descriptor) ? null : descriptor;
            }
        }
        return descriptor;
    }

    private PageDescriptor getChild(PageDescriptor parent, String pageId) {
        for (PageDescriptor descriptor : parent.childPages()) {
            if (pageId != null && pageId.equals(toPageId(descriptor.caption().getValue()))) {
                return descriptor;
            }
        }
        return null;
    }

    public PageParameters getStaticPageParams(PageDescriptor descriptor) {
        List<PageDescriptor> path = new ArrayList<PageDescriptor>();

        PageDescriptor parent = descriptor;
        while (!landing.equals(parent) && !parent.isNull()) {
            path.add(parent);
            parent = parent.parent();
        }

        PageParameters params = new PageParameters();
        for (int i = 0; i < path.size(); i++) {
            params.add(PARAMETER_NAMES[i], toPageId(path.get(path.size() - i - 1).caption().getValue()));
        }

        return params;
    }

    private static String toPageId(String caption) {
        return caption.toLowerCase().replaceAll("\\s+", "_").trim();
    }

    public static List<City> getCities() {

        City city = EntityFactory.create(City.class);

        city.province().name().setValue("Ontario");
        city.province().code().setValue("Ontario");
        city.name().setValue("Toronto");

        city.province().name().getValue();
        city.name().getValue();

        return null;
    }

}
