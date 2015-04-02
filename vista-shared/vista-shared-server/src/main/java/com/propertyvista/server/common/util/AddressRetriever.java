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
 */
package com.propertyvista.server.common.util;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.contact.LegalAddress;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.server.common.util.StreetAddressParser.StreetAddress;

public class AddressRetriever {

    private static Logger log = LoggerFactory.getLogger(AddressRetriever.class);

    private static InternationalAddress getUnitAddress(AptUnit unit) {
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);
        InternationalAddress address = unit.building().info().address().duplicate();
        address.suiteNumber().setValue(unit.info().number().getValue());
        return address;
    }

    // Legal Address:

    public static InternationalAddress getLeaseLegalAddress(Lease lease) {
        Persistence.ensureRetrieve(lease.unit(), AttachLevel.Attached);
        return toInternationalAddress(getUnitLegalAddress(lease.unit()));
    }

    public static LegalAddress getUnitLegalAddress(AptUnit unit) {
        if (!unit.info().legalAddressOverride().getValue(false)) {
            return toLegalAddress(getUnitAddress(unit));
        } else {
            LegalAddress address = unit.info().legalAddress().duplicate();
            if (CommonsStringUtils.isEmpty(address.suiteNumber().getValue())) { // do not allow empty suiteNumber in unit address!
                address.suiteNumber().setValue(unit.info().number().getValue());
            }
            return address;
        }
    }

    public static InternationalAddress toInternationalAddress(LegalAddress address) {
        InternationalAddress legal = EntityFactory.create(InternationalAddress.class);
        legal.city().set(address.city());
        legal.province().set(address.province());
        legal.country().set(address.country());
        legal.postalCode().set(address.postalCode());

        legal.streetNumber().set(address.streetNumber());
        legal.suiteNumber().set(address.suiteNumber());

        StringBuilder streetName = new StringBuilder();
        streetName.append(address.streetName().getValue(""));
        if (!address.streetType().isNull()) {
            streetName.append(" ").append(address.streetType().getValue());
        }
        if (!address.streetDirection().isNull()) {
            streetName.append(" ").append(address.streetDirection().getValue());
        }
        legal.streetName().setValue(streetName.toString());

        return legal;
    }

    public static LegalAddress toLegalAddress(InternationalAddress address) {
        LegalAddress legal = EntityFactory.create(LegalAddress.class);
        legal.city().set(address.city());
        legal.province().set(address.province());
        legal.country().set(address.country());
        legal.postalCode().set(address.postalCode());

        legal.streetNumber().set(address.streetNumber());
        legal.streetName().set(address.streetName());
        legal.suiteNumber().set(address.suiteNumber());

        if (ISOCountry.Canada.equals(address.country().getValue())) {
            try {
                StreetAddress sa = new CanadianLegalAddressParser().parse(address.streetNumber().getValue() + " " + address.streetName().getValue(), address
                        .suiteNumber().getValue());
                legal.streetNumber().setValue(sa.streetNumber);
                legal.streetName().setValue(sa.streetName);
                legal.suiteNumber().setValue(sa.unitNumber);
                legal.streetType().setValue(sa.streetType);
                legal.streetDirection().setValue(sa.streetDirection);
            } catch (ParseException ignore) {
                log.debug("Could not parse address", ignore);
            }
        }

        return legal;
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
