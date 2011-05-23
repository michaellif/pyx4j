/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.propertyvista.portal.server.geo.bean.GeoPairs;
import com.propertyvista.portal.server.importer.bean.Residential;

public class XmlUtil {

    /**
     * TODO - change this to generics later
     */
    public static Residential unmarshallResidential(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Residential.class);
        Unmarshaller um = context.createUnmarshaller();
        Residential residential = (Residential) um.unmarshal(new StringReader(xml));
        return residential;
    }

    /**
     * TODO - change this to generics later
     */
    public static GeoPairs unmarshallGeoPairs(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(GeoPairs.class);
        Unmarshaller um = context.createUnmarshaller();
        GeoPairs geoPairs = (GeoPairs) um.unmarshal(new StringReader(xml));
        return geoPairs;
    }

    /**
     * TODO - change this to generics later
     */
    public static String marshallGeoPairs(GeoPairs pairs) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(GeoPairs.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(pairs, System.out);

        return "";
    }
}
