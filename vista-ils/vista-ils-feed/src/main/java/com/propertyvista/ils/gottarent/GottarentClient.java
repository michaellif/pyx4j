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

import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axis2.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gottarent.rs.Listing;
import com.gottarent.ws.GottarentWSStub;
import com.gottarent.ws.GottarentWSStub.ImportOp;
import com.gottarent.ws.GottarentWSStub.ImportResponse;

/**
 * @author smolka
 *         The class responsible to import data to the gottarent server
 */
public class GottarentClient {

    private final static Logger log = LoggerFactory.getLogger(GottarentClient.class);

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
    public static void updateGottarent(String userId, Listing requestListing) throws AxisFault, Exception, RemoteException {
        if (requestListing == null) {
            //TODO: Smolka
            return;
        }

        GottarentWSStub ws = new GottarentWSStub();
        ImportOp input = new ImportOp();

        DataSource dataSource = new ByteArrayDataSource(Base64.encodeBase64(objectToXml(requestListing, Listing.class).getBytes()));

        input.setBuffer(new DataHandler(dataSource));
        input.setUserID(userId);
        ImportResponse response = ws.importOp(new ImportOp());
        //TODO: Smolka. Gottarent response always empty. What to return?
        response = null;
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
}
