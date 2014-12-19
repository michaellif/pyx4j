/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author vlads
 */
package com.propertyvista.eft.caledoncards.reports;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class DailyReportRetrieveFilterTest {

    @Test
    public void testFilter() {
        DailyReportRetrieveFilter f = new DailyReportRetrieveFilter(new File("."), "PROPERTYVISTA");
        Assert.assertNotNull("Production names", f.accept(null, "20140531_003631_PROPERTYVISTA.CSV"));
        Assert.assertNotNull("Production names", f.accept(null, "20140531_003631_PROPERTYVISTA.csv"));
        Assert.assertNull("Test names", f.accept(null, "20140531_003631_BIRCHWOOD.CSV"));
        Assert.assertNull("Test names", f.accept(null, "20140531_003631_Test.CSV"));
    }

}
