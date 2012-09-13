/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public interface LeadFacade {

    // in-memory Lease object state interfaces:

    Lead init(Lead lead);

    Lead persist(Lead lead);

    // DB-data Lease object state interfaces:

    void convertToApplication(Key leadId, AptUnit unitId);

    void close(Key leadId);

    // Utils:
    void setLeadRentedState(Lease leaseId);
}
