/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import java.util.Collection;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.site.SiteImageResource;

/**
 * Right now SiteImageResource are shared between objects, but there are no share panel.
 * 
 * We save them right now together with main object but not making them @owned!
 * 
 * We may change approach in future so that SiteImageResource upload will be the same as other IFiles.
 * 
 */
class SiteImageResourcePersister {

    public static void persist(SiteImageResource resource) {
        // Replace upload SiteImageResource.
        if (!resource.file().accessKey().isNull()) {
            resource.id().setValue(null);
        }
        Persistence.service().merge(resource);
    }

    public static void persist(Collection<SiteImageResource> imageSet) {
        for (SiteImageResource resource : imageSet) {
            persist(resource);
        }
    }

}
