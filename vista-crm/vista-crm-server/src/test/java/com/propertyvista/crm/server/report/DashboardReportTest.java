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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.report.test.ReportsTestBase;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardReportTest extends ReportsTestBase {

    public void init() throws Exception {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));

        // Ignore all security constrains
        TestLifecycle.testSession(null, CoreBehavior.DEVELOPER);
        TestLifecycle.beginRequest();

        createReport(DashboardReport.createModel(retreiveDashboard()));
    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    private static DashboardMetadata retreiveDashboard() {
        DashboardMetadata dm = EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(DashboardMetadata.class, new Key(1)));
        DataDump.dump("test-dashboard", dm);
        return dm;
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
