/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;
import com.pyx4j.essentials.server.xml.XMLEntityParser;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcImportDTO;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.interfaces.importer.BuildingImporter;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityFactory;
import com.propertyvista.server.domain.admin.Pmc;

public class ImportUploadServiceImpl extends UploadServiceImpl<PmcImportDTO> implements ImportUploadService {

    @Override
    public long getMaxSize(HttpServletRequest request) {
        return 5 * 1024 * 1024;
    }

    @Override
    public Key onUploadRecived(UploadDeferredProcess process, UploadData data) {
        try {
            PmcImportDTO importDTO = (PmcImportDTO) process.getData();
            if (importDTO.id().isNull()) {
                throw new Error();
            }
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            Pmc pmc = PersistenceServicesFactory.getPersistenceService().retrieve(Pmc.class, importDTO.id().getValue());
            if (pmc == null) {
                throw new Error("PMC Not found");
            }
            NamespaceManager.setNamespace(pmc.dnsName().getValue());

            String imagesBaseFolder = "data/export/images/";

            XMLEntityParser parser = new XMLEntityParser(new ImportXMLEntityFactory());
            ImportIO importIO = parser.parse(ImportIO.class, getDom(data.data).getDocumentElement());
            process.status().setProgressMaximum(importIO.buildings().size());

            int count = 0;
            for (BuildingIO building : importIO.buildings()) {
                new BuildingImporter().persist(building, imagesBaseFolder);
                count++;
                process.status().setProgress(count);
            }
            process.status().setCompleted();
        } finally {
            NamespaceManager.remove();
        }
        return null;
    }

    private static Document getDom(byte[] data) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
        builder.setErrorHandler(null);
        try {
            return builder.parse(new InputSource(new ByteArrayInputStream(data)));
        } catch (SAXException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}
