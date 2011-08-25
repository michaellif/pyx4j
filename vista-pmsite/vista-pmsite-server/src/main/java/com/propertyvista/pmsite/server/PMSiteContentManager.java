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

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.StaticPage;
import com.propertyvista.pmsite.server.panels.NavigationItem;

public class PMSiteContentManager {

    public static PageDescriptor retrieveMainNavig() {
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

    static List<NavigationItem> getNavigationItems() {
        List<NavigationItem> list = new ArrayList<NavigationItem>();

        list.add(new NavigationItem(FindAptPage.class, "Find an Apartment"));
        list.add(new NavigationItem(ResidentsPage.class, "Residents"));
        list.add(new NavigationItem(StaticPage.class, "About Us", "about"));
        list.add(new NavigationItem(StaticPage.class, "Contact", "contact"));

        return list;
    }
}
