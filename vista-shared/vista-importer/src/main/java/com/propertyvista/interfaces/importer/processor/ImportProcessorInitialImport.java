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

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.dto.DownloadableUploadResponseDTO;
import com.propertyvista.interfaces.importer.BuildingImporter;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class ImportProcessorInitialImport implements ImportProcessor {

    private final static Logger log = LoggerFactory.getLogger(ImportProcessorInitialImport.class);

    @Override
    public boolean validate(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo, DownloadableUploadResponseDTO response) {

        status.setProgressMaximum(data.buildings().size() * 2);
        MediaConfig mediaConfig = MediaConfig.create(uploadRequestInfo);
        List<String> messages = new Vector<String>();
        int count = 0;
        for (BuildingIO building : data.buildings()) {
            messages.addAll(new BuildingImporter().verify(building, mediaConfig));
            count++;
            status.setProgress(count);
            if (status.isCanceled()) {
                break;
            }
        }
        if (messages.size() > 0) {
            log.error("validation failed {}; {}", messages.size(), ConverterUtils.convertStringCollection(messages, "\n"));
            throw new Error("Validation error count:" + messages.size() + "; messages:" + ConverterUtils.convertStringCollection(messages, "\n"));
        }
        status.setProgress(0);
        return true;
    }

    @Override
    public void persist(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo, DownloadableUploadResponseDTO response) {
        SharedGeoLocator.setMode(Mode.updateCache);
        status.setProgressMaximum(data.buildings().size() * 2);
        ImportCounters counters = new ImportCounters();
        MediaConfig mediaConfig = MediaConfig.create(uploadRequestInfo);
        int count = 0;
        try {
            for (BuildingIO building : data.buildings()) {
                log.debug("processing building {} {}", count + "/" + data.buildings().size(), building.getStringView());
                counters.add(new BuildingImporter().persist(building, mediaConfig));
                count++;
                status.setProgress(data.buildings().size() + count);
                log.info("building {} updated", building.getStringView());
                if (status.isCanceled()) {
                    break;
                }
            }
            response.message().setValue(
                    SimpleMessageFormat.format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", count, counters.floorplans, counters.units));
        } finally {
            SharedGeoLocator.save();
        }
    }

}
