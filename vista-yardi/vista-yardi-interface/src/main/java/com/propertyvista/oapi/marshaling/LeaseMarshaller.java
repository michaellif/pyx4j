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
import com.propertyvista.oapi.model.LeaseStatusIO;
import com.propertyvista.oapi.model.PaymentFrequencyIO;
import com.propertyvista.oapi.xml.LogicalDateIO;

public class LeaseMarshaller implements Marshaller<Lease, LeaseIO> {

    private static class SingletonHolder {
        public static final LeaseMarshaller INSTANCE = new LeaseMarshaller();
    }

    private LeaseMarshaller() {
    }

    public static LeaseMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public LeaseIO unmarshal(Lease lease) {
        LeaseIO leaseIO = new LeaseIO();
        leaseIO.leaseId = lease.leaseId().getValue();
        leaseIO.status = new LeaseStatusIO(lease.status().getValue());
        leaseIO.paymentFrequency = new PaymentFrequencyIO(lease.paymentFrequency().getValue());
        leaseIO.leaseFrom = new LogicalDateIO(lease.leaseFrom().getValue());
        leaseIO.leaseTo = new LogicalDateIO(lease.leaseTo().getValue());
        leaseIO.propertyCode = lease.unit().building().propertyCode().getValue();
        leaseIO.unitNumber = lease.unit().info().number().getValue();
        return leaseIO;
    }

    @Override
    public Lease marshal(LeaseIO leaseRS) throws Exception {
        return null;
    }

}
