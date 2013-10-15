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
import java.util.List;
import java.util.Map;

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

import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.occupancy.ILSKijijiIntegrationAgent;
import com.propertyvista.ils.kijiji.mapper.KijijiDataMapper;
import com.propertyvista.ils.kijiji.mapper.dto.ILSBuildingDTO;
import com.propertyvista.ils.kijiji.mapper.dto.ILSFloorplanDTO;

@Path("/send")
public class KijijiApiService {

    private String generateXML() throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        Result res = new StreamResult(stringWriter);

        ObjectFactory factory = new ObjectFactory();

        JAXBContext context = JAXBContext.newInstance(ILSLocations.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        Map<ILSBuildingDTO, List<ILSFloorplanDTO>> units = new ILSKijijiIntegrationAgent().getUnitListing();
        ILSLocations locations = new KijijiDataMapper(factory).createLocations(units);
        JAXBElement<ILSLocations> element = factory.createLocations(locations);

        marshaller.marshal(element, res);
        return stringWriter.getBuffer().toString();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public Response generateKijiji() throws Exception {
        // FIXME - use OAPIFilter authentication mechanism; create CrmUser with (some Interface role) per ils vendor
        NamespaceManager.setNamespace("star");

        String xmlString = generateXML();
        return KijijiUtils.createSuccessResponse(xmlString);
    }

}
