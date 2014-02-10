/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.occupancy;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.tenant.lease.Lease;

public class OccupancyFacadeYardiImpl extends OccupancyFacadeAvailableForRentOnlyImpl {

    @Override
    public void reserve(Key unitId, Lease lease) {
        super.reserve(unitId, lease);
        // TODO Propagate unit reservation to Yardi!
    }

    @Override
    public void unreserve(Key unitId) {
        super.unreserve(unitId);
        // TODO Propagate unit release to Yardi!
    }

}
