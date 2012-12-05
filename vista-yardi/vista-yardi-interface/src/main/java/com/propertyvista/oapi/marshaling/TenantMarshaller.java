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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.oapi.model.TenantIO;

public class TenantMarshaller implements Marshaller<LeaseParticipant<?>, TenantIO> {

    private static class SingletonHolder {
        public static final TenantMarshaller INSTANCE = new TenantMarshaller();
    }

    private TenantMarshaller() {
    }

    public static TenantMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public TenantIO marshal(LeaseParticipant<?> participant) {
        TenantIO tenantIO = new TenantIO();
        Person person = participant.customer().person();
        tenantIO.firstName = person.name().firstName().getValue();
        tenantIO.lastName = person.name().lastName().getValue();
        tenantIO.middleName = person.name().middleName().getValue();
        tenantIO.sex.value = person.sex().getValue();
        tenantIO.phone.value = person.homePhone().getValue();
        tenantIO.email.value = person.email().getValue();
        return tenantIO;
    }

    public List<TenantIO> marshal(Collection<LeaseParticipant<?>> participants) {
        List<TenantIO> tenants = new ArrayList<TenantIO>();
        for (LeaseParticipant<?> participant : participants) {
            tenants.add(marshal(participant));
        }
        return tenants;
    }

    @Override
    public LeaseParticipant<?> unmarshal(TenantIO tenantIO) throws Exception {
        LeaseParticipant<?> participant = EntityFactory.create(LeaseParticipant.class);
        Person person = EntityFactory.create(Person.class);
        person.name().firstName().setValue(tenantIO.firstName);
        person.name().lastName().setValue(tenantIO.lastName);
        person.name().middleName().setValue(tenantIO.middleName);
        person.sex().setValue(tenantIO.sex.value);
        person.homePhone().setValue(tenantIO.phone.value);
        person.email().setValue(tenantIO.email.value);
        participant.customer().person().set(person);
        return participant;
    }

    public List<LeaseParticipant<?>> unmarshal(Collection<TenantIO> tenantIOList) throws Exception {
        List<LeaseParticipant<?>> participants = new ArrayList<LeaseParticipant<?>>();
        for (TenantIO tenantIO : tenantIOList) {
            participants.add(unmarshal(tenantIO));
        }
        return participants;
    }
}
