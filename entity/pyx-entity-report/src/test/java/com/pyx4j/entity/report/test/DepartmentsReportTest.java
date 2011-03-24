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
 * Created on Mar 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.report.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pyx4j.entity.report.JRIEntityCollectionDataSource;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;

public class DepartmentsReportTest extends ReportsTestBase {

    private static final String department1 = "Department1";

    private static final String department2 = "Department2";

    @BeforeClass
    public static void init() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("ReportTitle", "Departments Report");

        createReport("target/test-classes/reports/Departments.jrxml", parameters, new JRIEntityCollectionDataSource<Department>(createDepartments()));
    }

    private static Employee createEmploye() {
        Employee employee = EntityFactory.create(Employee.class);
        return employee;
    }

    @Test
    public void testStaticText() throws Exception {
        Assert.assertTrue("'Departments Report' not found, ", containsText("Departments Report"));
    }

    @Test
    public void testDynamicText() throws Exception {
        Assert.assertTrue("'" + department1 + "' not found, ", containsText(department1));
        Assert.assertTrue("'" + department2 + "' not found, ", containsText(department2));
    }

    static Collection<Department> createDepartments() {
        Collection<Department> departments = new ArrayList<Department>();

        {
            Department department = EntityFactory.create(Department.class);
            department.name().setValue(department1);
            department.employees().add(createEmploye());
            departments.add(department);
        }

        {
            Department department = EntityFactory.create(Department.class);
            department.name().setValue(department2);
            department.employees().add(createEmploye());
            departments.add(department);
        }
        return departments;
    }
}
