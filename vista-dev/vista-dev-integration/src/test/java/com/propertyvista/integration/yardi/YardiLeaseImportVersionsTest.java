/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;

@Category(FunctionalTests.class)
public class YardiLeaseImportVersionsTest extends PaymentYardiTestBase {

    private Lease lease;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createYardiBuilding("prop1");
        createYardiLease("prop1", "t000111");
        setSysDate("2011-01-01");
        yardiImportAll(getYardiCredential("prop1"));
        loadBuildingToModel("prop1");
        lease = loadLeaseToModel("t000111");
    }

    public void testCurrentTermVersionsChange() throws Exception {
        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("initial lease version", Integer.valueOf(2), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

        // No changes in Yardi, Just run import
        yardiImportAll(getYardiCredential("prop1"));

        // Nothing changes, expect the same version
        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("initial lease version", Integer.valueOf(2), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

        // Make two change in Yardi
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop1", "t000111"). //
                    set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2015-12-31"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }
        {
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop1", "t000111", "rent"). //
                    set(LeaseChargeUpdater.Name.Amount, "1500.00");
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        yardiImportAll(getYardiCredential("prop1"));

        // Two changes made  version ++2
        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("update lease version", Integer.valueOf(4), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

        yardiImportAll(getYardiCredential("prop1"));

        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("update lease version", Integer.valueOf(4), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

    }

}
