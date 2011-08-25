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
package com.propertyvista.crm.server.services;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;

import org.xml.sax.InputSource;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;

import com.propertyvista.crm.rpc.dto.UpdateUploadDTO;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.interfaces.importer.BuildingUpdater;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;

public class UpdateUploadServiceImpl extends UploadServiceImpl<UpdateUploadDTO> implements UpdateUploadService {

    @Override
    public long getMaxSize(HttpServletRequest request) {
        return 5 * 1024 * 1024;
    }

    @Override
    public Key onUploadRecived(UploadDeferredProcess process, UploadData data) {
        String imagesBaseFolder = "data/export/images/";

        ImportIO importIO = ImportUtils.parse(ImportIO.class, new InputSource(new ByteArrayInputStream(data.data)));
        process.status().setProgressMaximum(importIO.buildings().size());

        int count = 0;
        ImportCounters counters = new ImportCounters();
        for (BuildingIO building : importIO.buildings()) {
            counters.add(new BuildingUpdater().update(building, imagesBaseFolder));
            count++;
            process.status().setProgress(count);
        }
        process.status().setMessage(SimpleMessageFormat.format("Updated {0} units in {1} building(s)", counters.units, counters.buildings));
        process.status().setCompleted();
        return null;
    }
}
