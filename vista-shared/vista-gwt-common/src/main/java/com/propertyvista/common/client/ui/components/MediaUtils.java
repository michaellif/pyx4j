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

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class MediaUtils {

    // TODO: check PublicVisibilityType of the media here and allow/decline display here... 

    public static Image createPublicMediaImage(Key mediaId, ThumbnailSize size) {
        if (mediaId == null) {
            return new Image(ClentNavigUtils.getDeploymentBaseURL() + DeploymentConsts.mediaImagesServletMapping + "0/" + size.name() + "."
                    + ImageConsts.THUMBNAIL_TYPE);
        } else {
            return new Image(ClentNavigUtils.getDeploymentBaseURL() + DeploymentConsts.mediaImagesServletMapping + mediaId.toString() + "/" + size.name() + "."
                    + ImageConsts.THUMBNAIL_TYPE);
        }
    }
}
