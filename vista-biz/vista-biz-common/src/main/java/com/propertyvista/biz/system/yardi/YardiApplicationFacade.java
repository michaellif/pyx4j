/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public interface YardiApplicationFacade {
    /**
     * Create GuestCard; set pId in Lease.leaseId
     * Inside is UnitOfWork
     * 
     * @throws YardiServiceException
     */
    void createApplication(Lease leaseId) throws YardiServiceException;

    /** Tries to hold unit in Yardi; throws exception on failure */
    void holdUnit(Lease leaseId) throws YardiServiceException;

    void unreserveUnit(Lease leaseId) throws YardiServiceException;

    /**
     * NOTE. Once added, Lease participants cannot be deleted via Yardi ILS/GuestCard API.
     * It is required to call this method only once when Lease Participants are finalized.
     */
    void addLeaseParticipants(Lease lease) throws YardiServiceException;

    /**
     * Create Future Lease; set tId in Lease.leaseId
     * Rely on External UnitOfWork
     */
    Lease approveApplication(Lease leaseId) throws YardiServiceException;

    /**
     * @param buildingId
     *            for which Online Applications will be enabled
     * @throws UserRuntimeException
     *             is there are changes to be made in yardi
     */
    void validateApplicationAcceptance(Building buildingId) throws UserRuntimeException;
}
