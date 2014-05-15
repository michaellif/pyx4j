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

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;

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

    public static AddressStructured getOnlineApplicationAddress(OnlineApplication onlineApplication) {
        Persistence.ensureRetrieve(onlineApplication, AttachLevel.Attached);
        Persistence.ensureRetrieve(onlineApplication.masterOnlineApplication(), AttachLevel.Attached);
        Persistence.ensureRetrieve(onlineApplication.masterOnlineApplication().leaseApplication(), AttachLevel.Attached);
        return getLeaseAddress(onlineApplication.masterOnlineApplication().leaseApplication().lease());
    }

    // Legal Address:

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

    public static InternationalAddress getLeaseParticipantCurrentAddressSimple(LeaseParticipant<?> participant) {
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(getLeaseParticipantCurrentAddress(participant), address);
        return address;
    }

    public static InternationalAddress getLeaseParticipantCurrentAddressSimple(LeaseTermParticipant<?> participant) {
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(getLeaseParticipantCurrentAddress(participant), address);
        return address;
    }

    public static InternationalAddress getLeaseAddressSimple(Lease lease) {
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(getLeaseAddress(lease), address);
        return address;
    }

    public static InternationalAddress getOnlineApplicationAddressSimple(OnlineApplication onlineApplication) {
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(getOnlineApplicationAddress(onlineApplication), address);
        return address;
    }
}
