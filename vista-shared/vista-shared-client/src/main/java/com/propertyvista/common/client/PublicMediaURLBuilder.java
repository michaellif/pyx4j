/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.common.client;

import com.google.gwt.core.client.GWT;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;

public class PublicMediaURLBuilder extends VistaFileURLBuilder {

    public PublicMediaURLBuilder() {
        super(MediaFile.class);
    }

    @Override
    protected String getUrl(String fileId, String fileName) {
        return GWT.getModuleBaseURL() + DeploymentConsts.mediaImagesServletMapping + fileId + "/" + ThumbnailSize.large.name() + "."
                + ImageConsts.THUMBNAIL_TYPE;
    }
}
