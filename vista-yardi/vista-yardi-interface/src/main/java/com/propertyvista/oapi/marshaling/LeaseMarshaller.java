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
        if (lease == null || lease.isNull()) {
            return null;
        }
        LeaseIO leaseIO = new LeaseIO();
        leaseIO.leaseId = MarshallerUtils.getValue(lease.leaseId());
        leaseIO.propertyCode = MarshallerUtils.getValue(lease.unit().building().propertyCode());
        leaseIO.unitNumber = MarshallerUtils.getValue(lease.unit().info().number());

        leaseIO.status = MarshallerUtils.createIo(LeaseStatusIO.class, lease.status());
        leaseIO.paymentFrequency = MarshallerUtils.createIo(PaymentFrequencyIO.class, lease.paymentFrequency());
        leaseIO.leaseFrom = MarshallerUtils.createIo(LogicalDateIO.class, lease.leaseFrom());
        leaseIO.leaseTo = MarshallerUtils.createIo(LogicalDateIO.class, lease.leaseTo());

        Persistence.service().retrieve(lease.leaseParticipants());
        leaseIO.tenants.addAll(TenantMarshaller.getInstance().marshal(lease.leaseParticipants()));
        return leaseIO;
    }

    @Override
    public Lease unmarshal(LeaseIO leaseIO) {
        Lease lease = EntityFactory.create(Lease.class);
        lease.leaseId().setValue(leaseIO.leaseId);
        lease.unit().building().propertyCode().setValue(leaseIO.propertyCode);
        lease.unit().info().number().setValue(leaseIO.unitNumber);

        MarshallerUtils.setValue(lease.status(), leaseIO.status);
        MarshallerUtils.setValue(lease.paymentFrequency(), leaseIO.paymentFrequency);
        MarshallerUtils.setValue(lease.leaseFrom(), leaseIO.leaseFrom);
        MarshallerUtils.setValue(lease.leaseTo(), leaseIO.leaseTo);
        lease.leaseParticipants().addAll(TenantMarshaller.getInstance().unmarshal(leaseIO.tenants));
        return lease;
    }

}
