/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.reference;

import java.util.Date;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.site.SiteDescriptorChanges;

public class PMSiteContentCache {

    public static final String cacheKey = "pm-site";

    /**
     * Allow to update site in database shared between different instances of application.
     */
    public static void siteDescriptorUpdated() {
        EntityQueryCriteria<SiteDescriptorChanges> criteria = EntityQueryCriteria.create(SiteDescriptorChanges.class);
        SiteDescriptorChanges updateFlag = Persistence.service().retrieve(criteria);
        if (updateFlag != null) {
            updateFlag.updated().setValue(new Date());
            Persistence.service().persist(updateFlag);
        }

        CacheService.remove(cacheKey);
    }
}
