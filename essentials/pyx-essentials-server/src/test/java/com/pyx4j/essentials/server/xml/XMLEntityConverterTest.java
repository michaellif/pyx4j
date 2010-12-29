/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-12-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.essentials.server.report.XMLStringWriter;

public class XMLEntityConverterTest extends TestCase {

    public void testWrite() {

        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("First Name");

        XMLStringWriter xml = new XMLStringWriter();

        XMLEntityConverter.write(xml, employee);

        System.out.println(xml.toString());

        assertTrue(xml.toString().contains("<firstName>First Name</firstName>"));
    }

    private static Document getDom(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(null);

        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private static String getXML(IEntity entity) {
        XMLStringWriter xml = new XMLStringWriter();
        XMLEntityConverter.write(xml, entity);
        return xml.toString();
    }

    public void testPars() throws Exception {
        Employee employee1 = EntityFactory.create(Employee.class);
        employee1.firstName().setValue("First Name");

        Employee employee2 = XMLEntityConverter.pars(getDom(getXML(employee1)).getDocumentElement());

        assertEquals("Firstname", employee1.firstName().getValue(), employee2.firstName().getValue());
    }
}
