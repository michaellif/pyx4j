/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public interface N4ManagementFacade {

    /**
     * Searches for leases that require N4.
     */
    List<LegalNoticeCandidate> getN4Candidates(BigDecimal minAmountOwed, List<Building> buildingIds, ExecutionMonitor progressMonitor);

    /**
     * Prepare N4 letters for <code>delinguqentLeases</code> as if signed by the given <code>employee</code>.
     * 
     * @throws IllegalStateException
     *             if one of the given leases doesn't owe any money.
     */
    void issueN4(N4BatchRequestDTO batchRequest, AtomicInteger progress) throws IllegalStateException;

    /**
     * Retrieves N4s sorted in descending order by the date of generation. If <code>generatedCutOffDate</code> is not <code>null</code> this will be the minimum
     * generation date of N4s.
     */
    Map<Lease, List<N4LegalLetter>> getN4(List<Lease> leaseIds, LogicalDate generatedCutOffDate);

}
