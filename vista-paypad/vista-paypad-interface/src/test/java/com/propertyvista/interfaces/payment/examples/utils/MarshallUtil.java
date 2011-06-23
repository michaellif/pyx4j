/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.payment.examples.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class MarshallUtil {

    public static <T> void printSchema(Class<T> clazz, final java.io.OutputStream os, final boolean allowClose) throws JAXBException, IOException {

        JAXBContext context = JAXBContext.newInstance(clazz);
        context.generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                StreamResult sr = new StreamResult(new FilterOutputStream(os) {

                    @Override
                    public void close() throws IOException {
                        if (allowClose) {
                            super.close();
                        }
                    }

                });
                sr.setSystemId("");
                return sr;
            }
        });
    }

    public static <T> T unmarshal(Class<T> clazz, String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller um = context.createUnmarshaller();
        @SuppressWarnings("unchecked")
        T result = (T) um.unmarshal(new StringReader(xml));
        return result;
    }

    public static <T> void marshal(T data, java.io.OutputStream os) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(data.getClass());
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(data, os);
    }

    public static <T> String marshall(T data) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(data.getClass());
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(data, sw);
        return sw.toString();
    }

}
