/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;

import com.propertyvista.oapi.model.Lease;

@WebService(endpointInterface = "com.propertyvista.oapi.LeaseService")
public class LeaseServiceImpl implements LeaseService {

    static private Map<String, Lease> leases = new HashMap<String, Lease>();

    @Override
    public void createLease(Lease lease) {
        leases.put(lease.leaseId, lease);
    }

    @Override
    public Lease getLeaseByLeaseId(String leaseId) {
        return leases.get(leaseId);
    }

}
