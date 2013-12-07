/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.services;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.media.ApplicationDocumentFile;

//TODO Remove, use ApplicationDocumentProspectUploadService or ApplicationDocumentProspectCRMService
@Deprecated
public interface ApplicationDocumentUploadService extends UploadService<IEntity, ApplicationDocumentFile> {

    // TODO remove, Service call is made to obtain formats
    @Deprecated
    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.TIF, DownloadFormat.BMP, DownloadFormat.PDF);

}
