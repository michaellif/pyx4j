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
package com.propertyvista.interfaces.importer;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.server.xml.XMLEntityParser;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.dto.ImportUploadResponseDTO;
import com.propertyvista.dto.ImportDataFormatType;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.model.ImportInformation;
import com.propertyvista.interfaces.importer.parser.RentRollImportParser;
import com.propertyvista.interfaces.importer.parser.UnitAvailabilityImportParser;
import com.propertyvista.interfaces.importer.parser.VistaXMLImportParser;
import com.propertyvista.interfaces.importer.processor.ImportProcessor;
import com.propertyvista.interfaces.importer.processor.ImportProcessorBuildingUpdater;
import com.propertyvista.interfaces.importer.processor.ImportProcessorFlatFloorplanAndUnits;
import com.propertyvista.interfaces.importer.processor.ImportProcessorInitialImport;
import com.propertyvista.interfaces.importer.processor.ImportProcessorUpdateUnitAvailability;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityFactory;

public class ImportUtils {

    private static final I18n i18n = I18n.get(ImportUtils.class);

    public static ImportIO parse(ImportDataFormatType importAdapterType, byte[] data, DownloadFormat format) {
        switch (importAdapterType) {
        case vista:
            return new VistaXMLImportParser().parse(data, format);
        case rentRoll:
            return new RentRollImportParser().parse(data, format);
        case unitAvailability:
            return new UnitAvailabilityImportParser().parse(data, format);
        default:
            throw new Error("Unsupported DataFormatType type");
        }
    }

    public static ImportProcessor createImportProcessor(ImportUploadDTO uploadRequestInfo, ImportIO importIO) {

        switch (uploadRequestInfo.type().getValue()) {
        case updateUnitAvailability:
            return new ImportProcessorUpdateUnitAvailability();
        case newData:
            return new ImportProcessorInitialImport();
        case updateData:
            return new ImportProcessorBuildingUpdater();
        case flatFloorplanAndUnits:
            return new ImportProcessorFlatFloorplanAndUnits();
        default:
            throw new Error("Unsupported import type");
        }
    }

    public static boolean createValidationErrorResponse(IEntity entity, DeferredProcessProgressResponse status, UploadResponse<ImportUploadResponseDTO> response) {
        final ProcessingResponseReport report = new ProcessingResponseReport();
        EntityGraph.applyRecursivelyAllObjects(entity, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if ((entity instanceof ImportInformation) && (((ImportInformation) entity).invalid().getValue(Boolean.FALSE))) {
                    report.addMessage((ImportInformation) entity);
                }
                return true;
            }
        });

        if (report.getMessagesCount() > 0) {
            if (response.message == null) {
                response.message = i18n.tr("There are validation {0} errors in uploaded file", report.getMessagesCount());
            }
            response.data.success().setValue(Boolean.FALSE);
            String fileName = "validationError.csv";
            response.data.resultUrl().setValue(fileName);
            report.createDownloadable(fileName);
            return true;
        } else {
            return false;
        }
    }

    public static void createProcessingResponse(IEntity entity, DeferredProcessProgressResponse status, UploadResponse<ImportUploadResponseDTO> response) {
        final ProcessingResponseReport report = new ProcessingResponseReport();
        EntityGraph.applyRecursivelyAllObjects(entity, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if ((entity instanceof ImportInformation) && (!((ImportInformation) entity).message().isNull())) {
                    report.addMessage((ImportInformation) entity);
                }
                return true;
            }
        });

        if (report.getMessagesCount() > 0) {
            response.data = EntityFactory.create(ImportUploadResponseDTO.class);
            String fileName = "processingResults.csv";
            response.data.resultUrl().setValue(fileName);
            report.createDownloadable(fileName);
        }
    }

    public static <T extends IEntity> T parse(Class<T> entityClass, InputSource input) {
        XMLEntityParser parser = new XMLEntityParser(new ImportXMLEntityFactory());
        return parser.parse(entityClass, newDocument(input).getDocumentElement());
    }

    public static Document newDocument(InputSource input) {
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
            return builder.parse(input);
        } catch (SAXException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
