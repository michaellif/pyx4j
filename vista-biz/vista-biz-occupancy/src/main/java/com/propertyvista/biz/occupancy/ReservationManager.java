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
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitReservation;
import com.propertyvista.domain.tenant.lease.Lease;

public class ReservationManager {

    private final static I18n i18n = I18n.get(ReservationManager.class);

    public void reserve(Lease lease, int durationHours) {
        AptUnitReservation currentReservation = getCurrentReserved(lease.unit().getPrimaryKey());
        if (currentReservation != null) {
            if (currentReservation.lease().equals(lease)) {
                currentReservation.dateTo().setValue(DateUtils.addSeconds(SystemDateManager.getDate(), -1));
                Persistence.service().persist(currentReservation);
            } else {
                throw new UserRuntimeException(i18n.tr("Unit is already reserved"));
            }
        }

        AptUnitReservation reservation = EntityFactory.create(AptUnitReservation.class);
        reservation.lease().set(lease);
        reservation.unit().set(lease.unit());
        reservation.dateFrom().setValue(SystemDateManager.getDate());
        reservation.dateTo().setValue(DateUtils.addHours(SystemDateManager.getDate(), durationHours));
        Persistence.service().persist(reservation);
    }

    public boolean unreserveIfReservered(Lease lease) {
        AptUnitReservation reservation = getCurrentReserved(lease.unit().getPrimaryKey());
        if (reservation == null) {
            return false;
        } else if (reservation.lease().equals(lease)) {
            reservation.dateTo().setValue(DateUtils.addSeconds(SystemDateManager.getDate(), -1));
            Persistence.service().persist(reservation);
            return true;
        } else {
            // is reserved to different lease.
            return false;
        }
    }

    public Pair<Date, Lease> isReserved(Key unitId) {
        AptUnitReservation reservation = getCurrentReserved(unitId);
        if (reservation == null) {
            return null;
        } else {
            return new Pair<Date, Lease>(reservation.dateTo().getValue(), reservation.lease());
        }
    }

    public AptUnitReservation getCurrentReserved(Key unitId) {
        EntityQueryCriteria<AptUnitReservation> criteria = EntityQueryCriteria.create(AptUnitReservation.class);
        criteria.eq(criteria.proto().unit(), unitId);
        criteria.ge(criteria.proto().dateTo(), SystemDateManager.getDate());
        criteria.le(criteria.proto().dateFrom(), SystemDateManager.getDate());
        return Persistence.service().retrieve(criteria);
    }
}
