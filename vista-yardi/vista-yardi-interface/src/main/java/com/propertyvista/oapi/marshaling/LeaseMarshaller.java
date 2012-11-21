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
import com.propertyvista.oapi.model.LeaseIO;

public class LeaseMarshaller implements Marshaller<Lease, LeaseIO> {

    @Override
    public LeaseIO unmarshal(Lease lease) {
        LeaseIO leaseIO = new LeaseIO();
        leaseIO.leaseId = lease.leaseId().getValue();
        leaseIO.status = lease.status().getValue().toString();
        leaseIO.paymentFrequency = lease.paymentFrequency().getValue().name();
        leaseIO.numberOfCycles = lease.paymentFrequency().getValue().getNumOfCycles();
        leaseIO.leaseFrom = lease.leaseFrom().getValue().toString();
        leaseIO.leaseTo = lease.leaseTo().getValue().toString();
        leaseIO._propertyCode = lease.unit().building().propertyCode().getValue();
        leaseIO._unitNumber = lease.unit().info().number().getValue();
        return leaseIO;
    }

    @Override
    public Lease marshal(LeaseIO leaseRS) throws Exception {
        return null;
    }

}
