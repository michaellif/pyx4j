/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import com.yardi.entity.mits.YardiLease;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class LeaseMapper {

    /**
     * Maps YardiLease from YARDI System to lease. Only called for existing leases since it does not create a proper new lease.
     * 
     * @param yardiLease
     *            the YardiLeases to map
     * @return the lease list
     */

    public Lease map(YardiLease yardiLease) {
        Lease lease = EntityFactory.create(Lease.class);
        LeaseTerm term = EntityFactory.create(LeaseTerm.class);

        lease.actualMoveIn().setValue(yardiLease.getActualMoveIn() != null ? new LogicalDate(yardiLease.getActualMoveIn()) : null);
        lease.actualMoveOut().setValue(yardiLease.getActualMoveOut() != null ? new LogicalDate(yardiLease.getActualMoveOut()) : null);
        ServerSideFactory.create(LeaseFacade.class).setLeaseAgreedPrice(lease, yardiLease.getCurrentRent());
        lease.expectedMoveIn().setValue(yardiLease.getExpectedMoveInDate() != null ? new LogicalDate(yardiLease.getExpectedMoveInDate()) : null);
        lease.expectedMoveOut().setValue(yardiLease.getExpectedMoveOutDate() != null ? new LogicalDate(yardiLease.getExpectedMoveOutDate()) : null);
        term.termFrom().setValue(yardiLease.getLeaseFromDate() != null ? new LogicalDate(yardiLease.getLeaseFromDate()) : null);
        term.termTo().setValue(yardiLease.getLeaseToDate() != null ? new LogicalDate(yardiLease.getLeaseToDate()) : null);
        lease.currentTerm().set(term);

        return lease;
    }
}
