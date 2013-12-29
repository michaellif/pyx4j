/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.Key;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.PublicMediaURLBuilder;
import com.propertyvista.common.client.SiteImageResourceFileURLBuilder;
import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.pmc.info.PmcDocumentFile;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;

public class MediaUtils {

    public static Image createPublicMediaImage(Key mediaId, ThumbnailSize size) {
        if (mediaId == null) {
            return new Image(ClientNavigUtils.getDeploymentBaseURL() + DeploymentConsts.mediaImagesServletMapping + "0/" + size.name() + "."
                    + ImageConsts.THUMBNAIL_TYPE);
        } else {
            return new Image(ClientNavigUtils.getDeploymentBaseURL() + DeploymentConsts.mediaImagesServletMapping + mediaId.toString() + "/" + size.name()
                    + "." + ImageConsts.THUMBNAIL_TYPE);
        }
    }

    public static String createSiteImageResourceUrl(SiteImageResource resource) {
        if (resource == null) {
            return null;
        } else {
            return new SiteImageResourceFileURLBuilder().getUrl(resource.file());
        }
    }

    public static String createSiteSmallLogoUrl() {
        return ClientNavigUtils.getDeploymentBaseURL() + "/" + DeploymentConsts.portalLogoSmall + DeploymentConsts.siteImageResourceServletMapping;
    }

    public static String createSiteLargeLogoUrl() {
        return ClientNavigUtils.getDeploymentBaseURL() + "/" + DeploymentConsts.portalLogo + DeploymentConsts.siteImageResourceServletMapping;
    }

    public static String createCrmLogoUrl() {
        return ClientNavigUtils.getDeploymentBaseURL() + "/" + DeploymentConsts.crmLogo + DeploymentConsts.siteImageResourceServletMapping;
    }

    public static String createMediaImageUrl(MediaFile mediaFile) {
        return new PublicMediaURLBuilder().getUrl(mediaFile.file());
    }

    public static String createCustomerPictureUrl(CustomerPicture picture) {
        return new VistaFileURLBuilder(CustomerPicture.class).getUrl(picture.file());
    }

    public static String createLegalLetterDocumentUrl(LegalLetter legalLetter) {
        return new VistaFileURLBuilder(LegalLetter.class).getUrl(legalLetter.file());
    }

    public static String createPmcDocumentUrl(PmcDocumentFile file) {
        return new VistaFileURLBuilder(PmcDocumentFile.class).getUrl(file.file());
    }

}
