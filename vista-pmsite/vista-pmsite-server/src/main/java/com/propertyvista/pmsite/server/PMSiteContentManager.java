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

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.site.PageDescriptor;

public class PMSiteContentManager {

    public static final String PAGE_ID_PARAM_NAME = "id";

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
}
