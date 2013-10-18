/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public class AddressRetriever {

    public static AddressStructured getLeaseParticipantCurrentAddress(LeaseParticipant<?> participant) {
        Persistence.ensureRetrieve(participant, AttachLevel.Attached);
        return getLeaseAddress(participant.lease());
    }

    public static AddressStructured getLeaseParticipantCurrentAddress(LeaseTermParticipant<?> participant) {
        Persistence.ensureRetrieve(participant, AttachLevel.Attached);
        Persistence.ensureRetrieve(participant.leaseTermV(), AttachLevel.Attached);

        return getLeaseAddress(participant.leaseTermV().holder().lease());
    }

    public static AddressStructured getLeaseAddress(Lease lease) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.set(lease.unit().building().info().address());
        address.suiteNumber().set(lease.unit().info().number());

        return address;
    }

    public static AddressStructured getLeaseLegalAddress(Lease lease) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        return getUnitLegalAddress(lease.unit());
    }

    public static AddressStructured getUnitLegalAddress(AptUnit unit) {
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);
        if (!unit.info().legalAddressOverride().getValue(false)) {
            AddressStructured address = EntityFactory.create(AddressStructured.class);
            address.set(unit.building().info().address());
            address.suiteNumber().set(unit.info().number());
            return address;
        } else {
            return unit.info().legalAddress();
        }
    }

    // Simple form address retrieving: 

    public static AddressSimple getLeaseParticipantCurrentAddressSimple(LeaseParticipant<?> participant) {
        AddressSimple address = EntityFactory.create(AddressSimple.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(getLeaseParticipantCurrentAddress(participant), address);
        return address;
    }

    public static AddressSimple getLeaseParticipantCurrentAddressSimple(LeaseTermParticipant<?> participant) {
        AddressSimple address = EntityFactory.create(AddressSimple.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(getLeaseParticipantCurrentAddress(participant), address);
        return address;
    }

    public static AddressSimple getLeaseAddressSimple(Lease lease) {
        AddressSimple address = EntityFactory.create(AddressSimple.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(getLeaseAddress(lease), address);
        return address;
    }

}
