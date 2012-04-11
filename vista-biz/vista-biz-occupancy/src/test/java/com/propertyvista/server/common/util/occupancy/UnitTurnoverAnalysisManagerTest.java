/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy;

import org.junit.Test;

import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacadeImpl;

public class UnitTurnoverAnalysisManagerTest extends UnitTurnoverAnalysisManagerTestBase {

    public UnitTurnoverAnalysisManagerTest() {
        super(new UnitTurnoverAnalysisFacadeImpl());
    }

    @Test
    public void testWithOneUnit() {

        expect("2011-01-01", 0);
        expect("2011-12-01", 0);

        lease(unit(1), "2011-12-01", "2012-12-05");

        expect("2011-12-01", 0);
        expect("2011-12-02", 0);
        expect("2012-12-03", 0);
        expect("2012-12-05", 0);
        expect("2012-12-06", 0);

        lease(unit(1), "2012-12-07", "2013-01-01");

        expect("2012-12-01", 0);
        expect("2012-12-02", 0);
        expect("2012-12-03", 0);
        expect("2012-12-05", 0);
        expect("2012-12-06", 0);
        expect("2012-12-07", 1);
        expect("2012-12-31", 1);

        expect("2013-01-01", 0);
        expect("2013-01-07", 0);

        lease(unit(1), "2013-01-05", "2013-02-28");

        expect("2012-12-01", 0);
        expect("2012-12-02", 0);
        expect("2012-12-03", 0);
        expect("2012-12-05", 0);
        expect("2012-12-06", 0);
        expect("2012-12-07", 1);
        expect("2012-12-31", 1);

        expect("2013-01-01", 0);
        expect("2013-01-05", 1);
        expect("2013-01-07", 1);

        expect("2013-02-01", 0);

    }

    @Test
    public void testWithTwoUnits() {
        expect("2011-12-01", 0);

        lease(unit(1), "2011-12-01", "2012-12-05");
        lease(unit(2), "2011-12-02", "2012-12-15");

        expect("2012-12-01", 0);
        expect("2012-12-02", 0);
        expect("2012-12-05", 0);
        expect("2012-12-15", 0);

        lease(unit(1), "2012-12-07", "2013-01-01");

        expect("2012-12-05", 0);
        expect("2012-12-07", 1);

        lease(unit(2), "2012-12-17", "2013-01-01");

        expect("2012-12-05", 0);
        expect("2012-12-07", 1);
        expect("2012-12-17", 2);
        expect("2012-12-31", 2);
        expect("2013-01-01", 0);

    }

}
