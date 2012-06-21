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
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public class AddressRetriever {

    public static void getLeaseParticipantCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseParticipant participant) {
        Persistence.service().retrieve(participant);
        if ((participant == null) || (participant.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(LeaseParticipant.class).getCaption() + "' " + participant.getPrimaryKey()
                    + " NotFound");
        }

        Persistence.service().retrieve(participant.leaseV());
        Persistence.service().retrieve(participant.leaseV().holder());
        Persistence.service().retrieve(participant.leaseV().holder().unit().building());

        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.set(participant.leaseV().holder().unit().building().info().address());
        address.suiteNumber().set(participant.leaseV().holder().unit().info().number());

        callback.onSuccess(address);
    }
}
