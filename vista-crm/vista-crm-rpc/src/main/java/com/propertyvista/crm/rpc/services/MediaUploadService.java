/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.MediaFile;

//TODO remove from UI
/**
 * 
 * @deprecated use MediaUploadBuildingService or MediaUploadFloorplanService
 * 
 */
@Deprecated
public interface MediaUploadService extends UploadService<IEntity, MediaFile> {

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.BMP);

}
