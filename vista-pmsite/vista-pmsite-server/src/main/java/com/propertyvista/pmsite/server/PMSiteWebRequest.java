/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

import com.pyx4j.entity.cache.CacheService;

public class PMSiteWebRequest extends ServletWebRequest {

    private final PMSiteContentManager contentManager;

    public PMSiteWebRequest(HttpServletRequest httpServletRequest, String filterPrefix) {
        super(httpServletRequest, filterPrefix);

        // get PMContentManager with site descr separate (if changed update it)

        final String cacheKey = "pm-site";

        PMSiteContentManager cm = (PMSiteContentManager) CacheService.get(cacheKey);
        if (cm == null) {
            cm = new PMSiteContentManager();
            CacheService.put(cacheKey, cm);
        } else {
            if (cm.refresh()) {
                CacheService.put(cacheKey, cm);
            }
        }
        contentManager = cm;
    }

    public PMSiteContentManager getContentManager() {
        return contentManager;
    }
}
