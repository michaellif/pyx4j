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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;

public class AddressRetriever {

    // Legal Address:

    public static InternationalAddress getLeaseLegalAddress(Lease lease) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        return getUnitLegalAddress(lease.unit());
    }

    public static InternationalAddress getUnitLegalAddress(AptUnit unit) {
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);
        if (!unit.info().legalAddressOverride().getValue(false)) {
            InternationalAddress address = EntityFactory.create(InternationalAddress.class);
            address.set(unit.building().info().address());
            String line2 = address.suiteNumber().getValue();
            address.suiteNumber().setValue((CommonsStringUtils.isEmpty(line2) ? "" : line2 + ", ") + "Apt " + unit.info().number().getValue());
            return address;
        } else {
            return unit.info().legalAddress();
        }
    }

    // Simple form address retrieving: 

    public static InternationalAddress getLeaseParticipantCurrentAddress(LeaseParticipant<?> participant) {
        return getLeaseAddress(participant.lease());
    }

    public static InternationalAddress getLeaseParticipantCurrentAddress(LeaseTermParticipant<?> participant) {
        return getLeaseAddress(participant.leaseTermV().holder().lease());
    }

    public static InternationalAddress getLeaseAddress(Lease lease) {
        return getLeaseLegalAddress(lease);
    }

    public static InternationalAddress getOnlineApplicationAddress(OnlineApplication onlineApplication) {
        return getLeaseAddress(onlineApplication.masterOnlineApplication().leaseApplication().lease());
    }
}
