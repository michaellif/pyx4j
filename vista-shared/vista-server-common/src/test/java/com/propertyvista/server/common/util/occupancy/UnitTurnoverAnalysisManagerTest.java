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

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class UnitTurnoverAnalysisManagerTest extends UnitTurnoverAnalysisManagerTestBase {

    @Test
    public void testOneUnit() {
        lease(unit(1), "2011-12-01", "2012-12-05");
        lease(unit(1), "2011-12-07", "2013-01-01");

        recalcTurnovers("2013-01-01");
        expect("2013-01-01", 0);

        lease(unit(1), "2013-01-05", "2013-02-28");

        recalcTurnovers("2013-01-03");
        expect("2013-01-03", 0);
        expect("2013-01-05", 1);
        expect("2013-01-10", 1);
        expect("2013-01-31", 1);
    }

    @Test
    public void testTwoUnits() {
        lease(unit(1), "2011-12-01", "2012-12-05");
        lease(unit(1), "2011-12-07", "2013-01-01");
        lease(unit(2), "2011-10-05", "2013-01-10");

        recalcTurnovers("2013-01-01");
        expect("2013-01-01", 0);

        lease(unit(1), "2013-01-05", "2013-02-28");
        lease(unit(2), "2013-01-15", "2013-03-01");

        recalcTurnovers("2013-01-03");
        expect("2013-01-03", 0);
        expect("2013-01-05", 1);
        expect("2013-01-10", 1);
        expect("2013-01-15", 2);
    }

}
