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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.Status;

public class EntityArgsConverterTest extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(EntityArgsConverterTest.class);

    private static final String FIRST_NAME = "First Name";

    private static final String DEPARTMENT_NAME = "Department Name";

    @SuppressWarnings("deprecation")
    private static final Date FROM = new Date(111, 5, 6, 12, 30);

    public void testConvertToArgs() {

        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue(FIRST_NAME);
        employee.from().setValue(FROM);
        employee.reliable().setValue(true);
        employee.holidays().setValue(22L);
        employee.rating().setValue(5);
        employee.salary().setValue(22.5);

        employee.employmentStatus().setValue(EmploymentStatus.PART_TIME);
        employee.accessStatus().setValue(Status.SUSPENDED);

        employee.department().name().setValue(DEPARTMENT_NAME);

        Map<String, List<String>> args = EntityArgsConverter.convertToArgs(employee);

        log.debug(args.toString());

        assertEquals(employee.getValue().size(), args.size());
        assertEquals(employee.firstName().getValue(), args.get(employee.firstName().getFieldName()).get(0));
        assertEquals(TimeUtils.simpleFormat(employee.from().getValue(), EntityArgsConverter.DATE_TIME_FORMAT), args.get(employee.from().getFieldName()).get(0));
        assertEquals(employee.reliable().getValue().toString(), args.get(employee.reliable().getFieldName()).get(0));
        assertEquals(employee.holidays().getValue().toString(), args.get(employee.holidays().getFieldName()).get(0));
        assertEquals(employee.rating().getValue().toString(), args.get(employee.rating().getFieldName()).get(0));
        assertEquals(employee.salary().getValue().toString(), args.get(employee.salary().getFieldName()).get(0));

        assertEquals(employee.employmentStatus().getValue().name(), args.get(employee.employmentStatus().getFieldName()).get(0));
        assertEquals(employee.accessStatus().getValue().name(), args.get(employee.accessStatus().getFieldName()).get(0));

        assertEquals(employee.department().name().getValue().toString(),
                args.get(EntityArgsConverter.convertPathToDotNotation(employee.department().name().getPath())).get(0));

    }

    public void testCreateFromArgs() {

        Map<String, List<String>> args = new HashMap<String, List<String>>();

        Employee proto = EntityFactory.getEntityPrototype(Employee.class);

        args.put(proto.firstName().getFieldName(), Arrays.asList(new String[] { FIRST_NAME }));
        args.put(proto.from().getFieldName(), Arrays.asList(new String[] { TimeUtils.simpleFormat(FROM, EntityArgsConverter.DATE_TIME_FORMAT) }));
        args.put(proto.reliable().getFieldName(), Arrays.asList(new String[] { "true" }));
        args.put(proto.holidays().getFieldName(), Arrays.asList(new String[] { "22" }));
        args.put(proto.rating().getFieldName(), Arrays.asList(new String[] { "5" }));
        args.put(proto.salary().getFieldName(), Arrays.asList(new String[] { "22.5" }));

        args.put(proto.employmentStatus().getFieldName(), Arrays.asList(new String[] { EmploymentStatus.PART_TIME.name() }));
        args.put(proto.accessStatus().getFieldName(), Arrays.asList(new String[] { Status.SUSPENDED.name() }));

        args.put(EntityArgsConverter.convertPathToDotNotation(proto.department().name().getPath()), Arrays.asList(new String[] { DEPARTMENT_NAME }));

        Employee employee = EntityArgsConverter.createFromArgs(Employee.class, args);

        log.debug(employee.toString());

        assertEquals(employee.firstName().getValue(), FIRST_NAME);
        assertEquals(employee.from().getValue(), FROM);
        assertEquals((boolean) employee.reliable().getValue(), true);
        assertEquals((long) employee.holidays().getValue(), 22L);
        assertEquals((int) employee.rating().getValue(), 5);
        assertEquals((double) employee.salary().getValue(), 22.5);

        assertEquals(employee.employmentStatus().getValue(), EmploymentStatus.PART_TIME);
        assertEquals(employee.accessStatus().getValue(), Status.SUSPENDED);

        assertEquals(employee.department().name().getValue(), DEPARTMENT_NAME);

    }
}
