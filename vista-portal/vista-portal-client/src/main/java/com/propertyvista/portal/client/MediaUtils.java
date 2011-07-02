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
package com.propertyvista.portal.client;

import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class MediaUtils {

    public static final String mediaImagesServletMapping = "media/";

    public static Image createPublicMediaImage(IPrimitive<Key> mediaPk, ThumbnailSize size) {
        if (mediaPk.isNull()) {
            return new Image(PortalImages.INSTANCE.noImage());
        } else {
            return new Image(ClentNavigUtils.getDeploymentBaseURL() + mediaImagesServletMapping + mediaPk.getValue().toString() + "/" + size.name() + ".jpg");
        }
    }

}
