/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.occupancy;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitReservation;
import com.propertyvista.domain.tenant.lease.Lease;

public class ReservationManager {

    public void reserve(Lease lease, int durationHours) {
        AptUnitReservation r = EntityFactory.create(AptUnitReservation.class);
        r.lease().set(lease);
        r.unit().set(lease.unit());
        r.dateFrom().setValue(SystemDateManager.getDate());
        r.dateTo().setValue(DateUtils.addHours(SystemDateManager.getDate(), durationHours));
        Persistence.service().persist(r);
    }

    public boolean unreserveIfReservered(Lease lease) {
        return false;
    }

    public Pair<Date, Lease> isReserved(Key unitId) {
        // TODO Auto-generated method stub
        return null;
    }

}
