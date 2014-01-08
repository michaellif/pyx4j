/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.gottarent;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axis2.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gottarent.rs.Listing;
import com.gottarent.rs.ObjectFactory;
import com.gottarent.ws.GottarentWSStub;
import com.gottarent.ws.GottarentWSStub.ImportOp;
import com.gottarent.ws.GottarentWSStub.ImportResponse;

import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.occupancy.ILSGottarentIntegrationAgent;
import com.propertyvista.ils.gottarent.mapper.GottarentDataMapper;
import com.propertyvista.ils.gottarent.mapper.dto.ILSReportDTO;
import com.propertyvista.misc.VistaTODO;

/**
 * @author smolka
 *         The class responsible to import data to the gottarent server
 */
public class GottarentClient {

    private final static Logger log = LoggerFactory.getLogger(GottarentClient.class);

    public static void updateGottarentListing(ExecutionMonitor executionMonitor) {
        // TODO - use ExecutionMonitor to register state (target points, errors, etc) in the course of execution
        try {
            // fetch relevant data and prepare gottarent xml
            Listing listing = generateData();

            if (hasData(listing)) {
                // update gottarent server
                GottarentClient.updateGottarent("UserId", listing);
            }
        } catch (Exception e) {// TODO: Smolka
            throw new RuntimeException(e);
        }
    }

    /**
     * Import to gottarent server basing provided input
     * 
     * @param userId
     *            - vendor id in gottarent system
     * @param requestListing
     *            - gottarent input xml
     * @throws AxisFault
     * @throws Exception
     * @throws RemoteException
     */
    private static void updateGottarent(String userId, Listing requestListing) throws Exception {
        if (requestListing == null) {
            //TODO: Smolka
            return;
        }

        GottarentWSStub ws = new GottarentWSStub();
        ImportOp input = new ImportOp();

        String generatedXml = objectToXml(requestListing, Listing.class);
        DataDump.dump("Gottarent", generatedXml);
        if (!VistaTODO.ILS_TestMode) {
            DataSource dataSource = new ByteArrayDataSource(Base64.encodeBase64(generatedXml.getBytes()));

            input.setBuffer(new DataHandler(dataSource));
            input.setUserID(userId);
            ImportResponse response = ws.importOp(new ImportOp());
            //TODO: Smolka. Gottarent response always empty. What to return?
            response = null;
        } else {
            // validate xml against current schema
            Source schemaSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("xsd/gottarent/ILS_Gottarent.xsd"));
            Source xmlSource = new StreamSource(new StringReader(generatedXml));
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                Schema schema = schemaFactory.newSchema(schemaSource);
                Validator validator = schema.newValidator();
                validator.validate(xmlSource);
                log.info("XML Validation Completed");
            } catch (Exception e) {
                log.info("XML Validation Failed: {}", e.getMessage());
            }

        }
    }

    private static <T> String objectToXml(T object, Class<T> classType) throws Exception {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(classType);
            StringWriter writerTo = new StringWriter();
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, writerTo);
            log.trace("Marshaled object string: \n" + writerTo.toString());
            return writerTo.toString();
        } catch (JAXBException e) {
            log.error("Failed to convert object to XMl format. " + e.getMessage());
            throw new Exception(e);
        }
    }

    private static boolean hasData(Listing listing) {
        return listing != null && listing.getCompany() != null && listing.getCompany().getPortfolio() != null
                && listing.getCompany().getPortfolio().getBuilding() != null && listing.getCompany().getPortfolio().getBuilding().size() > 0;
    }

    private static Listing generateData() throws JAXBException {

        ILSReportDTO ilsReport = new ILSGottarentIntegrationAgent().getUnitListing();

        return new GottarentDataMapper(new ObjectFactory()).createListing(ilsReport);
    }
}
