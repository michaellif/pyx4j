/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on May 27, 2011
 * @author dmitry
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.util;

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
