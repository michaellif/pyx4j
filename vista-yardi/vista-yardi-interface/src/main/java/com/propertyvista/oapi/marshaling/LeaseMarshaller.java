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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.types.LeaseStatusIO;
import com.propertyvista.oapi.model.types.PaymentFrequencyIO;
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
    public LeaseIO marshal(Lease lease) {
        LeaseIO leaseIO = new LeaseIO();
        leaseIO.leaseId = lease.leaseId().getValue();
        leaseIO.status = new LeaseStatusIO(lease.status().getValue());
        leaseIO.paymentFrequency = new PaymentFrequencyIO(lease.paymentFrequency().getValue());
        leaseIO.leaseFrom = new LogicalDateIO(lease.leaseFrom().getValue());
        leaseIO.leaseTo = new LogicalDateIO(lease.leaseTo().getValue());
        leaseIO.propertyCode = lease.unit().building().propertyCode().getValue();
        leaseIO.unitNumber = lease.unit().info().number().getValue();

        Persistence.service().retrieve(lease.leaseParticipants());
        leaseIO.tenants.addAll(TenantMarshaller.getInstance().marshal(lease.leaseParticipants()));
        return leaseIO;
    }

    @Override
    public Lease unmarshal(LeaseIO leaseIO) {
        Lease lease = EntityFactory.create(Lease.class);
        lease.leaseId().setValue(leaseIO.leaseId);
        MarshallerUtils.ioToEntity(lease.status(), leaseIO.status);
        MarshallerUtils.ioToEntity(lease.paymentFrequency(), leaseIO.paymentFrequency);
        MarshallerUtils.ioToEntity(lease.leaseFrom(), leaseIO.leaseFrom);
        MarshallerUtils.ioToEntity(lease.leaseTo(), leaseIO.leaseTo);
        lease.unit().building().propertyCode().setValue(leaseIO.propertyCode);
        lease.unit().info().number().setValue(leaseIO.unitNumber);
        lease.leaseParticipants().addAll(TenantMarshaller.getInstance().unmarshal(leaseIO.tenants));
        return lease;
    }

}
