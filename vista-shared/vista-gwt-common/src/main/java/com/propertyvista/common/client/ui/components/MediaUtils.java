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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.Key;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.pmc.info.PmcDocumentFile;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
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

    public static String createPmcDocumentUrl(PmcDocumentFile file) {
        String baseURL = ClientNavigUtils.getDeploymentBaseURL();
        if (SecurityController.checkBehavior(VistaBasicBehavior.Operations)) {
            // Admin is exception, TODO use FileURLBuilder
            baseURL += VistaApplication.operations + "/";
        }
        return baseURL + DeploymentConsts.pmcDocumentServletMapping + file.id().getStringView() + "/" + file.fileName().getStringView();
    }

    public static String createApplicationDocumentUrl(ApplicationDocumentFile file) {
        return ClientNavigUtils.getDeploymentBaseURL() + DeploymentConsts.applicationDocumentServletMapping + file.id().getStringView() + "/"
                + file.fileName().getStringView();
    }

    public static String createLegalLetterDocumentUrl(LegalLetter file) {
        return GWT.getModuleBaseURL() + DeploymentConsts.legalLetterServletMappning + file.id().getStringView() + "/" + file.fileName().getStringView();
    }

    public static String createSiteImageResourceUrl(SiteImageResource resource) {
        return ClientNavigUtils.getDeploymentBaseURL() + resource.id().getStringView() + "/" + resource.fileName().getStringView()
                + DeploymentConsts.siteImageResourceServletMapping;
    }

    public static String createCustomerPictureUrl(CustomerPicture picture) {
        return ClientNavigUtils.getDeploymentBaseURL() + picture.blobKey().getStringView() + "/" + picture.fileName().getStringView()
                + DeploymentConsts.customerPictureServletMapping;
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
}
