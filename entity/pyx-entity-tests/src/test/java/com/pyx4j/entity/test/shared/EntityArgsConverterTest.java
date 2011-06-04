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
 * Created on Jun 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.entity.test.shared.domain.Employee;

public class EntityArgsConverterTest extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(EntityArgsConverterTest.class);

    private static final String FIRST_NAME = "First Name";

    private static final String DEPARTMENT_NAME = "Department Name";

    private static final Date FROM = new Date();

    public void testConvertToArgs() {

        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue(FIRST_NAME);
        employee.from().setValue(FROM);
        employee.reliable().setValue(true);
        employee.holidays().setValue(22L);
        employee.rating().setValue(5);
        employee.salary().setValue(22.5);

        employee.department().name().setValue(DEPARTMENT_NAME);

        Map<String, String> args = EntityArgsConverter.convertToArgs(employee);

        log.info(args.toString());

        //TODO
        //assertEquals(employee.getValue().size(), args.size());
        assertEquals(employee.firstName().getValue(), args.get(employee.firstName().getFieldName()));
        //TODO
        //  assertEquals(EntityArgsConverter.DATE_FORMAT.format(employee.from().getValue()), args.get(employee.from().getFieldName()));
        assertEquals(employee.reliable().getValue().toString(), args.get(employee.reliable().getFieldName()));
        assertEquals(employee.holidays().getValue().toString(), args.get(employee.holidays().getFieldName()));
        assertEquals(employee.rating().getValue().toString(), args.get(employee.rating().getFieldName()));
        assertEquals(employee.salary().getValue().toString(), args.get(employee.salary().getFieldName()));

    }

    public void testCreateFromArgs() {

        Map<String, String> args = new HashMap<String, String>();

        Employee proto = EntityFactory.getEntityPrototype(Employee.class);

        args.put(proto.firstName().getFieldName(), FIRST_NAME);
        //TODO
        //  args.put(proto.from().getFieldName(), EntityArgsConverter.DATE_FORMAT.format(FROM));
        args.put(proto.reliable().getFieldName(), "true");
        args.put(proto.holidays().getFieldName(), "22");
        args.put(proto.rating().getFieldName(), "5");
        args.put(proto.salary().getFieldName(), "22.5");

        Employee employee = EntityArgsConverter.createFromArgs(Employee.class, args);

        log.info(employee.toString());

        assertEquals(employee.firstName().getValue(), FIRST_NAME);
        //TODO
        //   assertEquals(EntityArgsConverter.DATE_FORMAT.format(employee.from().getValue()), EntityArgsConverter.DATE_FORMAT.format(FROM));
        assertEquals((boolean) employee.reliable().getValue(), true);
        assertEquals((long) employee.holidays().getValue(), 22L);
        assertEquals((int) employee.rating().getValue(), 5);
        assertEquals((double) employee.salary().getValue(), 22.5);

    }
}
