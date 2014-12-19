/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 26, 2013
 * @author stanp
 */
package com.propertyvista.ils.kijiji.mapper;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.SiteLogoImageResource;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.shared.i18n.CompiledLocale;

public class KijijiMapperUtils {

    public static SiteDescriptor getSiteDescriptor() {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        return Persistence.service().retrieve(criteria);
    }

    // TODO - account for locale
    public static SiteLogoImageResource getSiteLogo() {
        SiteLogoImageResource logo = null;
        IList<SiteLogoImageResource> allLogos = getSiteDescriptor().logo();
        for (SiteLogoImageResource logoRc : allLogos) {
            if (logoRc.locale().lang().getValue() == CompiledLocale.en_CA) {
                logo = logoRc;
            }
        }
        if (logo == null && allLogos.size() > 0) {
            logo = allLogos.get(0);
        }
        return logo;
    }

    public static String getSiteImageResourceUrl(SiteImageResource resource) {
        return getPortalHomeUrl() + resource.id().getStringView() + "/" + resource.file().fileName().getStringView()
                + DeploymentConsts.siteImageResourceServletMapping;
    }

    public static String getMediaImgUrl(MediaFile media, ThumbnailSize size) {
        return getPortalHomeUrl() + DeploymentConsts.mediaImagesServletMapping + media.id().getStringView() + "/" + size.name() + "."
                + ImageConsts.THUMBNAIL_TYPE;
    }

    public static String getPortalHomeUrl() {
        String homeUrl = VistaDeployment.getBaseApplicationURL(VistaApplication.site, false);
        return homeUrl + (homeUrl.endsWith("/") ? "" : "/");
    }
}
