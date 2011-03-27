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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.portal.server.pt.services.SummaryServicesImpl;

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
        VistaTestDBSetup.init();
        new VistaDataPreloaders().preloadAll();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("ReportTitle", title);

        createReport("target/classes/com/propertyvista/portal/server/report/Summary.jrxml", parameters,
                new JRIEntityCollectionDataSource<Summary>(Arrays.asList(new Summary[] { retreiveSummary() })));

    }

    private static Summary retreiveSummary() {
        EntityQueryCriteria<Summary> criteria = EntityQueryCriteria.create(Summary.class);
        Application application = EntityFactory.create(Application.class);
        application.setPrimaryKey(1L);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        Summary summary = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (summary == null) {
            summary = EntityFactory.create(Summary.class);
            summary.application().set(application);
        }
        new SummaryServicesImpl().loadTransientData(summary);
        return summary;

    }

    private static Summary retreiveSummaryTodo() {
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