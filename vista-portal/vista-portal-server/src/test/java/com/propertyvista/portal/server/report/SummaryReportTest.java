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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.report.test.ReportsTestBase;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.PreloadConfig;
import com.propertyvista.common.domain.User;
import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.server.generator.PTGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.portal.server.ptapp.services.SummaryServiceImpl;

public class SummaryReportTest extends ReportsTestBase {

    public void init() throws Exception {
        if (!ServerSideConfiguration.isStartedUnderEclipse()) {
            //TODO I disabled test for now
            return;
        }

        boolean realTimeDevelopment = ServerSideConfiguration.isStartedUnderEclipse();
        if (realTimeDevelopment) {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        } else {
            VistaTestDBSetup.init();
            new VistaDataPreloaders(PreloadConfig.createTest()).preloadAll();
        }
        // Ignore all security constrains
        TestLifecycle.testSession(null, CoreBehavior.DEVELOPER);
        TestLifecycle.beginRequest();

        createReport(SummaryReport.createModel(retreiveSummary()));

    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    private static Summary retreiveSummary() {
        EntityQueryCriteria<User> userCriteria = EntityQueryCriteria.create(User.class);
        userCriteria.add(PropertyCriterion.eq(userCriteria.proto().name(), DemoData.PRELOADED_USERNAME));
        User devUser = PersistenceServicesFactory.getPersistenceService().retrieve(userCriteria);
        Assert.assertNotNull("devUser " + DemoData.PRELOADED_USERNAME, devUser);

        EntityQueryCriteria<Application> applicationCriteria = EntityQueryCriteria.create(Application.class);
        applicationCriteria.add(PropertyCriterion.eq(applicationCriteria.proto().user(), devUser));
        Application application = PersistenceServicesFactory.getPersistenceService().retrieve(applicationCriteria);
        Assert.assertNotNull("devUser application", application);

        EntityQueryCriteria<Summary> criteria = EntityQueryCriteria.create(Summary.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        Summary summary = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (summary == null) {
            summary = EntityFactory.create(Summary.class);
            summary.application().set(application);
        }
        new SummaryServiceImpl().loadTransientData(summary);
        DataDump.dump("test-summary", summary);
        return summary;

    }

    private static Summary retreiveSummaryTodo() throws IOException {
        PTGenerator generator = new PTGenerator(DemoData.PT_GENERATION_SEED, PreloadConfig.createTest());
        Application application = generator.createApplication(PTGenerator.createUser());
        Summary summary = generator.createSummary(application, null);
        return summary;
    }

    @Test
    public void testStaticText() throws Exception {
        init();

        if (!ServerSideConfiguration.isStartedUnderEclipse()) {
            //TODO I disabled test for now
            return;
        }

        Assert.assertTrue("Report title '" + SummaryReport.title + "' not found, ", containsText(SummaryReport.title));
    }

}