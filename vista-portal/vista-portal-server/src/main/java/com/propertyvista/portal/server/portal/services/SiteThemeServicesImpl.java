/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.IgnoreSessionToken;

import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SitePalette;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;

@IgnoreSessionToken
public class SiteThemeServicesImpl implements SiteThemeServices {

    @Override
    public void retrievePalette(AsyncCallback<SitePalette> callback) {
        SitePalette palette = null;
        Key paletteKey = (Key) CacheService.get(SitePalette.cacheKey);
        if (paletteKey != null) {
            palette = Persistence.service().retrieve(SitePalette.class, paletteKey);
        }
        if (palette == null) {
            SiteDescriptor siteDescriptor = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));
            palette = siteDescriptor.sitePalette().cloneEntity();
            CacheService.put(SitePalette.cacheKey, palette.getPrimaryKey());
        }
        callback.onSuccess(palette);
    }
}
