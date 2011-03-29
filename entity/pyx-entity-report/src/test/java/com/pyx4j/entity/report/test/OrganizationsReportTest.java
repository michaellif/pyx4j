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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import net.sf.jasperreports.engine.JasperCompileManager;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Organization;

public class OrganizationsReportTest extends ReportsTestBase {

    private static final String organization1 = "Organization1";

    private static final String organization2 = "Organization2";

    @BeforeClass
    public static void init() throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("REPORT_TITLE", "Organizations Report");
        parameters.put("SUBREPORT_DIR", "target/test-classes/reports/");

        JasperCompileManager.compileReportToFile("target/test-classes/reports/Departments.jrxml", "target/test-classes/reports/Departments.jasper");
        createReport(new JasperReportModel("reports.Organizations", createOrganizations(), parameters));

    }

    @Test
    public void testStaticText() throws Exception {
        Assert.assertTrue("'Organizations Report' not found, ", containsText("Organizations Report"));
    }

    @Test
    public void testDynamicText() throws Exception {
        Assert.assertTrue("'" + organization1 + "' not found, ", containsText(organization1));
        Assert.assertTrue("'" + organization2 + "' not found, ", containsText(organization2));
    }

    static List<Organization> createOrganizations() {
        List<Organization> organizations = new ArrayList<Organization>();

        {
            Organization organization = EntityFactory.create(Organization.class);
            organization.name().setValue(organization1);
            for (Department department : DepartmentsReportTest.createDepartments()) {
                organization.departments().add(department);
            }
            organizations.add(organization);
        }

        {
            Organization organization = EntityFactory.create(Organization.class);
            organization.name().setValue(organization2);
            for (Department department : DepartmentsReportTest.createDepartments()) {
                organization.departments().add(department);
            }
            organizations.add(organization);
        }
        return organizations;
    }

}
