/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;

public interface IdAssignmentFacade {

    void assignId(Building building);

    void assignId(Lead lead);

    void assignId(MasterOnlineApplication masterOnlineApplication);

    void assignId(Customer customer);

    void assignId(Lease lease);

    String createAccountNumber();

}
