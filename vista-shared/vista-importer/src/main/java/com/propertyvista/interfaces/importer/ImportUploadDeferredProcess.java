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
package com.propertyvista.interfaces.importer;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.dto.ImportUploadResponseDTO;

public class ImportUploadDeferredProcess extends UploadDeferredProcess<ImportUploadDTO, ImportUploadResponseDTO> {

    private static final long serialVersionUID = 1L;

    public ImportUploadDeferredProcess(ImportUploadDTO data) {
        super(data);
    }

    public void setBinary(byte[] binaryBata) {

    }

    @Override
    public void execute() {
        boolean success = false;
        try {
            Persistence.service().startBackgroundProcessTransaction();
            super.execute();
            Persistence.service().commit();
            success = true;
        } finally {
            if (!success) {
                Persistence.service().rollback();
            }
            Persistence.service().endTransaction();
        }
    }

}
