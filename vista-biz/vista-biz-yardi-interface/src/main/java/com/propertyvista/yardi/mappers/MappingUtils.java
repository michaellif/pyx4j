/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mappers;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.yardi.entity.mits.Address;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.CanadianPostalCodeValidator;

public class MappingUtils {

    public static Building retrieveBuilding(Key yardiInterfaceId, String propertyCode) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);

        criteria.eq(criteria.proto().propertyCode(), BuildingsMapper.getPropertyCode(propertyCode));
        criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);

        return Persistence.service().retrieve(criteria);
    }

    public static void ensureCountryOfOperation(Building building) throws YardiServiceException {
        Pmc pmc = VistaDeployment.getCurrentPmc();
        ISOCountry yardiCountry = building.info().address().country().getValue();
        CountryOfOperation countryOfOperation = pmc.features().countryOfOperation().getValue();
        if (yardiCountry == null) {
            throw new YardiServiceException("Country not set for this building. Building not imported.");
        } else if (countryOfOperation == null || !yardiCountry.equals(countryOfOperation.country)) {
            throw new YardiServiceException("Country for this building does not match country of operation");
        }
    }

    public static InternationalAddress getAddress(Address mitsAddress, StringBuilder error) {
        // TODO instantiate address parser according to the building country
        // extract street name and number
        String[] streetNumName = mitsAddress.getAddress1().split("\\s+", 2);
        // combine address2 parts
        StringBuilder address2 = new StringBuilder();
        for (String addressPart : mitsAddress.getAddress2()) {
            if (address2.length() > 0) {
                address2.append("\n");
            }
            address2.append(addressPart);
        }
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);
        if (streetNumName.length == 2) {
            address.streetNumber().setValue(streetNumName[0]);
            address.streetName().setValue(streetNumName[1]);
        } else {
            address.streetName().setValue(mitsAddress.getAddress1());
        }
        address.suiteNumber().setValue(address2.toString());

        ISOCountry country = ISOCountry.forName(mitsAddress.getCountry());
        if (country == null && !VistaDeployment.getCurrentPmc().features().countryOfOperation().isNull()) {
            // assume country as countryOfOperation if not not set in Yardi!
            country = VistaDeployment.getCurrentPmc().features().countryOfOperation().getValue().country;
        }
        if (country == null) {
            error.append("\n");
            error.append("failed to find ISO Country from MITS address: " + mitsAddress.getCountry());
        }
        address.country().setValue(country);

        ISOProvince province = ISOProvince.forCode(mitsAddress.getState());
        if (province != null) {
            address.province().setValue(province.name);
        } else {
            error.append("\nProvince from MITS address not found; used as is: " + mitsAddress.getState());
            address.province().setValue(mitsAddress.getState());
        }

        address.city().setValue(mitsAddress.getCity());

        CanadianPostalCodeValidator pcValidator = new CanadianPostalCodeValidator(mitsAddress.getPostalCode());
        if (!pcValidator.isValid()) {
            error.append("\nInvalid Canadian Postal Code: " + pcValidator.original());
        }
        address.postalCode().setValue(pcValidator.format());

        return address;
    }

    public static LogicalDate toLogicalDate(String yyyyMMdd) {
        LogicalDate date = null;
        try {
            date = new LogicalDate(new SimpleDateFormat("yyyy-MM-dd").parse(yyyyMMdd));
        } catch (Exception ignore) {
            // ignore
        }
        return date;
    }

    /**
     * Sort list of leases by ascending by lease end date, so current and open ended ones go last...
     * 
     * @param leases
     *            - list to sort
     * @return - sorted input list
     */

    public static List<Lease> sortLeases(List<Lease> leases) {
        Collections.sort(leases, new Comparator<Lease>() {
            @Override
            public int compare(Lease l1, Lease l2) {
                int res = 0;

                if (l1.leaseTo().isNull()) {
                    if (l2.leaseTo().isNull()) {
                        res = 0; // both are null - assume they are the same
                    } else {
                        res = 1; // first is null - it is greater 
                    }
                } else {
                    if (l2.leaseTo().isNull()) {
                        res = -1; // second is null - first is less 
                    } else {
                        res = l1.leaseTo().getValue().compareTo(l2.leaseTo().getValue());
                    }
                }

                return res;
            }
        });

        return leases;
    }
}
