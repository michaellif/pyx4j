/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi;

import org.junit.Test;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

public class YardiImportTest extends YardiTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    @Test
    public void testImport() throws Exception {

        String propertyCode = "prop123";
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(propertyCode), new ExecutionMonitor());

        Building building = getBuilding(propertyCode);
        assertEquals(propertyCode, building.propertyCode().getValue());

        AptUnit unit = getUnit(building, "0111");
        assertNotNull(unit);

        Lease lease = getCurrentLease(unit);
        assertNotNull(lease);

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().serviceItem()).
        agreedPrice("1234.56");
        //.
        //effectiveDate("1-May-2011").
        //expirationDate("31-May-2011");
        // @formatter:on
    }
}
