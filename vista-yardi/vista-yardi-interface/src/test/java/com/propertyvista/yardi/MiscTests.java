/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.mapper.MappingUtils;

public class MiscTests {

    @BeforeClass
    public static void init() {

    }

    @Test
    public void sortLeasesTest() throws Exception {
        Lease l1 = EntityFactory.create(Lease.class);
        Lease l2 = EntityFactory.create(Lease.class);

        List<Lease> leases = new ArrayList<Lease>();
        leases.add(l1);
        leases.add(l2);

        MappingUtils.sortLeases(leases);

        assertEquals(leases.get(0), l1);
        
        // -------------------------------

        l1.leaseTo().setValue(new LogicalDate());
        l2.leaseTo().setValue(null);

        leases.clear();
        leases.add(l1);
        leases.add(l2);

        MappingUtils.sortLeases(leases);

        assertEquals(leases.get(0), l1);

        // -------------------------------

        l1.leaseTo().setValue(null);
        l2.leaseTo().setValue(new LogicalDate());

        leases.clear();
        leases.add(l1);
        leases.add(l2);

        MappingUtils.sortLeases(leases);

        assertEquals(leases.get(0), l2);

        // -------------------------------

        l1.leaseTo().setValue(new LogicalDate());
        l2.leaseTo().setValue(new LogicalDate());

        leases.clear();
        leases.add(l1);
        leases.add(l2);

        MappingUtils.sortLeases(leases);

        assertEquals(leases.get(0), l1);

        // -------------------------------

        l1.leaseTo().setValue(DateUtils.daysAdd(new LogicalDate(), 2));
        l2.leaseTo().setValue(DateUtils.daysAdd(new LogicalDate(), 1));

        leases.clear();
        leases.add(l1);
        leases.add(l2);

        MappingUtils.sortLeases(leases);

        assertEquals(leases.get(0), l2);
    }
}
