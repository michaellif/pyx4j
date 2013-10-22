/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.processor;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.dto.DownloadableUploadResponseDTO;
import com.propertyvista.interfaces.importer.model.ImportIO;

public interface ImportProcessor {

    public boolean validate(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo, DownloadableUploadResponseDTO response);

    public void persist(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo, DownloadableUploadResponseDTO response);

}
