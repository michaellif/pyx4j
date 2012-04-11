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
package com.propertyvista.biz.occupancy;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.tenant.lease.Lease;

public interface UnitTurnoverAnalysisFacade {

    /**
     * Update turnover statistics for the relevant building if needed
     * 
     * @param lease
     *            must be active
     */
    void propagateLeaseActivationToTurnoverReport(Lease lease);

    int turnoversSinceBeginningOfTheMonth(LogicalDate asOf, Key... buildings);

}
