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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.dto.ImportUploadResponseDTO;
import com.propertyvista.interfaces.importer.model.ImportIO;

public class ImportProcessorFlatFloorplanAndUnits implements ImportProcessor {

    private static final I18n i18n = I18n.get(ImportProcessorFlatFloorplanAndUnits.class);

    private final static Logger log = LoggerFactory.getLogger(ImportProcessorFlatFloorplanAndUnits.class);

    @Override
    public boolean validate(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo,
            UploadResponse<ImportUploadResponseDTO> response) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void persist(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo,
            UploadResponse<ImportUploadResponseDTO> response) {
        // TODO Auto-generated method stub

    }

}
