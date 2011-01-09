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
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.ConcreteEntity;
import com.pyx4j.entity.test.shared.domain.inherit.RefferenceEntity;
import com.pyx4j.essentials.server.report.XMLStringWriter;

public class XMLEntityConverterTest extends TestCase {

    private static Employee createData() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.setPrimaryKey(Long.valueOf(22));
        employee.firstName().setValue("First Name");
        employee.employmentStatus().setValue(EmploymentStatus.FULL_TIME);

        employee.homeAddress().streetName().setValue("Home Street");

        Task t1 = EntityFactory.create(Task.class);
        t1.setPrimaryKey(Long.valueOf(23));
        t1.description().setValue("Task1");
        t1.notes().add("Note 1");
        t1.notes().add("Note 2");
        t1.oldStatus().add(Status.SUSPENDED);

        employee.tasks().add(t1);

        Task t2 = EntityFactory.create(Task.class);
        t2.setPrimaryKey(Long.valueOf(24));
        t2.finished().setValue(Boolean.TRUE);
        t2.description().setValue("Task2");
        t2.notes().add("Note 21");
        t2.notes().add("Note 22");
        t2.oldStatus().add(Status.ACTIVE);
        employee.tasks().add(t2);

        return employee;
    }

    public void testWrite() {

        Employee employee = createData();

        XMLStringWriter xml = new XMLStringWriter();

        XMLEntityConverter.write(xml, employee);

        //System.out.println(xml.toString());

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
        Employee employee1 = createData();

        Employee employee2 = XMLEntityConverter.pars(getDom(getXML(employee1)).getDocumentElement());

        assertEquals("Level 1 value", employee1.firstName().getValue(), employee2.firstName().getValue());
        assertEquals("Level 1 enum value", employee1.employmentStatus().getValue(), employee2.employmentStatus().getValue());
        assertEquals("Level 2 value", employee1.homeAddress().streetName().getValue(), employee2.homeAddress().streetName().getValue());
    }

    public void TODO_testAbstractMember() throws Exception {
        ConcreteEntity ent1 = EntityFactory.create(ConcreteEntity.class);
        ent1.setPrimaryKey(1L);
        ent1.name1().setValue("1");
        ent1.name().setValue("1.00");

        Concrete1Entity ent11 = EntityFactory.create(Concrete1Entity.class);
        ent11.setPrimaryKey(11L);
        ent11.name1().setValue("1.1");

        ent1.refference().set(ent11);

        String xml = getXML(ent1);
        System.out.println(xml);
        ConcreteEntity ent2 = XMLEntityConverter.pars(getDom(xml).getDocumentElement());

        assertTrue("item1 Not Same data\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2));
    }

    public void testAbstractSetMember() throws Exception {
        RefferenceEntity rootEntity = EntityFactory.create(RefferenceEntity.class);
        rootEntity.setPrimaryKey(0L);

        ConcreteEntity ent1 = EntityFactory.create(ConcreteEntity.class);
        ent1.setPrimaryKey(1L);
        ent1.name1().setValue("1");
        ent1.name().setValue("1.00");
        rootEntity.refferences().add(ent1);

        Concrete1Entity ent2 = EntityFactory.create(Concrete1Entity.class);
        ent2.setPrimaryKey(2L);
        ent2.name1().setValue("2");
        ent2.name11().setValue("2.11");
        rootEntity.refferences().add(ent2);

        String xml = getXML(rootEntity);
        //System.out.println(xml);
        RefferenceEntity rootEntity2 = XMLEntityConverter.pars(getDom(xml).getDocumentElement());

        Iterator<Base1Entity> it = rootEntity2.refferences().iterator();

        Base1Entity item1 = it.next();
        Base1Entity item2 = it.next();
        if (item1.name1().getValue().equals("2")) {
            // swap the order for tests
            Base1Entity t = item1;
            item1 = item2;
            item2 = t;
        }

        assertTrue("item1 data type " + item1.getClass(), item1 instanceof ConcreteEntity);
        assertTrue("item2 data type " + item2.getClass(), item2 instanceof Concrete1Entity);

        assertEquals("item1 value", ent1, item1);
        assertEquals("item2 value", ent2, item2);

        assertTrue("item1 Not Same data\n" + ent1.toString() + "\n!=\n" + item1.toString(), EntityGraph.fullyEqual(ent1, item1));
        assertTrue("item2 Not Same data\n" + ent2.toString() + "\n!=\n" + item2.toString(), EntityGraph.fullyEqual(ent2, item2));
    }

}
