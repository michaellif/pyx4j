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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public class AddressRetriever {

    public static void getLeaseParticipantCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseTermParticipant participant) {
        Persistence.service().retrieve(participant);
        if ((participant == null) || (participant.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(LeaseTermParticipant.class).getCaption() + "' " + participant.getPrimaryKey()
                    + " NotFound");
        }

        Persistence.service().retrieve(participant.leaseTermV());
        Persistence.service().retrieve(participant.leaseTermV().holder().lease());
        Persistence.service().retrieve(participant.leaseTermV().holder().lease().unit().building());

        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.set(participant.leaseTermV().holder().lease().unit().building().info().address());
        address.suiteNumber().set(participant.leaseTermV().holder().lease().unit().info().number());

        callback.onSuccess(address);
    }

    public static void getLeaseParticipantCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseParticipant participant) {
        Persistence.service().retrieve(participant);
        if ((participant == null) || (participant.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(LeaseTermParticipant.class).getCaption() + "' " + participant.getPrimaryKey()
                    + " NotFound");
        }

        Persistence.ensureRetrieve(participant.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(participant.lease().unit().building(), AttachLevel.Attached);

        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.set(participant.lease().unit().building().info().address());
        address.suiteNumber().set(participant.lease().unit().info().number());

        callback.onSuccess(address);
    }
}
