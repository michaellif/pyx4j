/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.propertyvista.domain.tenant.lease.Lease;

/**
 * This one currently knows to generate leased segment that are consistent with
 * 
 */
public class UnitOccupancyGeneratingPreloader {

    public void preloadOccupancy(List<Lease> leases) {
        Collections.sort(leases, new Comparator<Lease>() {
            @Override
            public int compare(Lease arg0, Lease arg1) {
                return arg0.leaseFrom().compareTo(arg1.leaseFrom());
            }
        });

        for (final Lease lease : leases) {
            if (!lease.unit().id().isNull() & !lease.status().isNull()) {

                // TODO update occupancy based on lease
                // TODO ...
                // TODO profit!
            }
        }
    }
}
