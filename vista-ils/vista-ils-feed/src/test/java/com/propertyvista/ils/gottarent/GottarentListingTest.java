/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.gottarent;

import org.junit.Ignore;

import com.propertyvista.biz.occupancy.ILSGottarentIntegrationAgent;
import com.propertyvista.ils.ILSTestBase;
import com.propertyvista.ils.gottarent.mapper.dto.ILSReportDTO;

//TODO - remove IGNORE when completed
@Ignore
public class GottarentListingTest extends ILSTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {
        ILSReportDTO ilsReport = new ILSGottarentIntegrationAgent().getUnitListing();
        assertTrue("No Units found", ilsReport.totalUnits().getValue() > 0);
    }
}
