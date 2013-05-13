/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 09, 2013
 * @author Anatoly
 */
package com.propertyvista.ils.kijiji.rs;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.kijiji.pint.rs.ILSLocations;
import com.kijiji.pint.rs.ObjectFactory;

@Path("/send")
public class KijijiApiService {

    private String generateXML() throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        Result res = new StreamResult(stringWriter);

        ObjectFactory factory = new ObjectFactory();

        XMLManager mgr = new XMLManager(factory);
        ILSLocations locations = mgr.createRentXML();

        JAXBContext context = JAXBContext.newInstance(ILSLocations.class);

        JAXBElement<ILSLocations> element = factory.createLocations(locations);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        marshaller.marshal(element, res);
        return stringWriter.getBuffer().toString();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public Response generateKijiji() throws Exception {

        String xmlString = generateXML();
        return KijijiUtils.createSuccessResponse(xmlString);
    }

}
