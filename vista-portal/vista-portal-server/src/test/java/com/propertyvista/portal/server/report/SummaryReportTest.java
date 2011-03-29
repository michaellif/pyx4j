/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.server.report;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.sf.jasperreports.engine.JasperCompileManager;

import org.junit.BeforeClass;
import org.junit.Test;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.portal.server.pt.services.SummaryServicesImpl;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.report.JRIEntityCollectionDataSource;
import com.pyx4j.entity.report.test.ReportsTestBase;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class SummaryReportTest extends ReportsTestBase {

    private static final String title = "Summary Report";

    @BeforeClass
    public static void init() throws Exception {

        boolean realTimeDevelopment = ServerSideConfiguration.isStartedUnderEclipse();
        if (realTimeDevelopment) {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        } else {
            VistaTestDBSetup.init();
            new VistaDataPreloaders().preloadAll();
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("SUBREPORT_DIR", "target/classes/com/propertyvista/portal/server/report/");
        parameters.put("ReportTitle", title);
        parameters.put("LEASE_PRICE", "Pricing and Availability...");
        parameters.put("LEASE_TERMS", "Lease Terms text...");
        parameters.put("DIGITAL_SIG", "Digital Signature...");

        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/Info.jrxml",
                "target/classes/com/propertyvista/portal/server/report/Info.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/Financial.jrxml",
                "target/classes/com/propertyvista/portal/server/report/Financial.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/Pets.jrxml",
                "target/classes/com/propertyvista/portal/server/report/Pets.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/RentRelatedCharges.jrxml",
                "target/classes/com/propertyvista/portal/server/report/RentRelatedCharges.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/ProRatedCharges.jrxml",
                "target/classes/com/propertyvista/portal/server/report/ProRatedCharges.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/ApplicationCharges.jrxml",
                "target/classes/com/propertyvista/portal/server/report/ApplicationCharges.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/PaymentSplitCharges.jrxml",
                "target/classes/com/propertyvista/portal/server/report/PaymentSplitCharges.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/Vehicles.jrxml",
                "target/classes/com/propertyvista/portal/server/report/Vehicles.jasper");
        JasperCompileManager.compileReportToFile("target/classes/com/propertyvista/portal/server/report/EmergencyContacts.jrxml",
                "target/classes/com/propertyvista/portal/server/report/EmergencyContacts.jasper");

        createReport("target/classes/com/propertyvista/portal/server/report/Summary.jrxml", parameters,
                new JRIEntityCollectionDataSource<Summary>(Arrays.asList(new Summary[] { retreiveSummary() })));
    }

    private static Summary retreiveSummary() {
        EntityQueryCriteria<Application> applicationCriteria = EntityQueryCriteria.create(Application.class);
        Application application = PersistenceServicesFactory.getPersistenceService().retrieve(applicationCriteria);
        EntityQueryCriteria<Summary> criteria = EntityQueryCriteria.create(Summary.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        Summary summary = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (summary == null) {
            summary = EntityFactory.create(Summary.class);
            summary.application().set(application);
        }
        new SummaryServicesImpl().loadTransientData(summary);
        return summary;

    }

    private static Summary retreiveSummaryTodo() throws IOException {
        VistaDataGenerator generator = new VistaDataGenerator();
        Application application = generator.createApplication(VistaDataGenerator.createUser());
        generator.createSummary(application);
        Summary summary = generator.createSummary(application);
        return summary;
    }

    @Test
    public void testStaticText() throws Exception {
        Assert.assertTrue("'" + title + "' not found, ", containsText(title));
    }

}