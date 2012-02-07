/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util.occupancy;

import org.junit.Before;
import org.junit.Test;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

public class AptUnitOccupancyManagerTest extends AptUnitOccupancyManagerTestBase {

    // idea.
//    void testInitialization() {
//        setup("2011-01-01", null, Status,vacant);
//        
//        now("2011-02-03");
//        getUOM().scopeAvalable();
//        
//        expect("2011-01-01", "2011-02-02", Status,vacant);
//        expect("2011-02-03", null, Status.avalable);
//        
//    }
//    
//    void testReservation() {
//        setup("2011-01-01", null, Status,avalable);
//        
//        now("2011-02-03");
//        
//        l = createLease();
//        l.leaseStartDate().set(sDate("2011-02-15"));        
//        getUOM().reserve(l);
//        
//        expect("2011-01-01", "2011-02-02", Status,avalable);
//        expect("2011-02-03", null, Status.reserved);
//    }

    // real imp.

    @Test
    public void testInitialization() {
        setup("2011-01-01", "MAX_DATE", Status.vacant);
        expect("2011-01-01", "MAX_DATE", Status.vacant); // for now just sanity check
    }

    @Override
    @Before
    public void setup() {
        super.setup();
    }
}
