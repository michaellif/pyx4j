/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 10, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.propertyvista.crm.rpc.services.MediaUploadBuildingService;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class MediaUploadBuildingServiceImpl extends MediaUploadAbstractServiceImpl implements MediaUploadBuildingService {

    @Override
    protected ImageTarget imageResizeTarget() {
        return ImageTarget.Building;
    }

}
