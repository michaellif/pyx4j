/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.oapi.model.LeaseRS;

public class LeaseMarshaller implements Marshaller<Lease, LeaseRS> {

    @Override
    public LeaseRS unmarshal(Lease lease) {
        LeaseRS leaseRS = new LeaseRS();
        leaseRS.leaseId = lease.leaseId().getValue();
        leaseRS.status = lease.status().getValue().toString();
        leaseRS.propertyCode = lease.unit().building().propertyCode().getValue();
        leaseRS.unitNumber = lease.unit().info().number().getValue();
        return leaseRS;
    }

    @Override
    public Lease marshal(LeaseRS leaseRS) throws Exception {
        return null;
    }

}
