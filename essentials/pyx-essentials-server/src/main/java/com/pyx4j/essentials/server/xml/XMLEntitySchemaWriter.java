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
 * Created on Aug 30, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.io.FilterOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.pyx4j.entity.server.impl.EntityPojoWrapperGenerator;
import com.pyx4j.entity.shared.IEntity;

public class XMLEntitySchemaWriter {

    public static void printSchema(Class<? extends IEntity> clazz, final java.io.OutputStream os, final boolean allowClose) {
        try {
            JAXBContext context = JAXBContext.newInstance(EntityPojoWrapperGenerator.getPojoClass(clazz));
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
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
