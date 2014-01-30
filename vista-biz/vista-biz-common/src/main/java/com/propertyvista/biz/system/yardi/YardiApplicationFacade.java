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

import com.propertyvista.domain.tenant.lease.Lease;

public interface YardiApplicationFacade {
    /** Create GuestCard; set pId in Lease.leaseId */
    Lease createApplication(Lease lease);

    /** Tries to hold unit in Yardi; throws exception on failure */
    void holdUnit(Lease lease);

    /** Create Future Lease; set tId in Lease.leaseId */
    Lease approveApplication(Lease lease);
}
