/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on May 8, 2013
 * @author Dimitry
 * @version $Id$
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
