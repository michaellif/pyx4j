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
package com.propertyvista.crm.server.report;

import java.util.Vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.report.test.ReportsTestBase;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.User;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardReportTest extends ReportsTestBase {

    public void init() throws Exception {
        boolean realTimeDevelopmentWithMysSQL = ServerSideConfiguration.isStartedUnderEclipse();
        if (realTimeDevelopmentWithMysSQL) {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        } else {
            VistaTestDBSetup.init();
            //new VistaDataPreloaders().preloadAll();
        }

        // Ignore all security constrains
        TestLifecycle.testSession(null, VistaBehavior.PROPERTY_MANAGER);
        TestLifecycle.beginRequest();

        createReport(DashboardReport.createModel(retreiveData()));
    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    private static DashboardMetadata retreiveData() {
        User anyUser = EntityFactory.create(User.class);
        anyUser.setPrimaryKey(Key.DORMANT_KEY);
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), anyUser));
        criteria.add(new PropertyCriterion(criteria.proto().layoutType(), Restriction.NOT_EQUAL, DashboardMetadata.LayoutType.Report));
        Vector<DashboardMetadata> dms = EntityServicesImpl.secureQuery(criteria);

        for (DashboardMetadata dm : dms) {
            DataDump.dump("test-dashboard", dm);
            return dm; // get first available data...
        }

        return null;
    }

    @Test
    public void testStaticText() throws Exception {
        if (!ServerSideConfiguration.isStartedUnderEclipse()) {
            //TODO disabled in build for now
            return;
        }

        init();

        Assert.assertTrue("Report title '" + DashboardReport.title + "' not found, ", containsText(DashboardReport.title));
    }
}
