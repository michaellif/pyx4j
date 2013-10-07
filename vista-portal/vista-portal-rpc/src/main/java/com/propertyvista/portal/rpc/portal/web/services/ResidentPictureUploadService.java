/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.services;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.tenant.CustomerPicture;

public interface ResidentPictureUploadService extends UploadService<CustomerPicture, CustomerPicture> {

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.BMP);

}
