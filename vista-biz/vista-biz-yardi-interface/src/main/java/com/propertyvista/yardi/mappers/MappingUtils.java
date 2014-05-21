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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.yardi.entity.mits.Address;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.CanadianPostalCodeValidator;

public class MappingUtils {

    public static Building getBuilding(Key yardiInterfaceId, String propertyCode) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);

        criteria.eq(criteria.proto().propertyCode(), BuildingsMapper.getPropertyCode(propertyCode));
        criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);

        return Persistence.service().retrieve(criteria);
    }

    public static List<Province> getProvinces() {
        EntityQueryCriteria<Province> criteria = EntityQueryCriteria.create(Province.class);

        criteria.asc(criteria.proto().name());

        return Persistence.service().query(criteria);
    }

    public static String getCountry(String stateCode) {
        for (Province province : getProvinces()) {
            if (StringUtils.equals(province.code().getValue(), stateCode)) {
                return province.country().name().getValue();
            }
        }
        return null;
    }

    public static void ensureCountryOfOperation(Building building) throws YardiServiceException {
        Pmc pmc = VistaDeployment.getCurrentPmc();
        String yardiCountry = building.info().address().country().name().getValue();
        String countryOfOperation = pmc.features().countryOfOperation().getValue().toString();
        if (yardiCountry == null) {
            throw new YardiServiceException("Country not set for this building. Building not imported.");
        } else if (!yardiCountry.equals(countryOfOperation)) {
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

        String importedCountry = mitsAddress.getCountry();
        if (StringUtils.isEmpty(importedCountry)) {
            // assume country as countryOfOperation if not not set in Yardi!
            importedCountry = VistaDeployment.getCurrentPmc().features().countryOfOperation().getValue().toString();
        }

        Country country = null;
        try {
            country = getCountryByName(importedCountry);
        } catch (Throwable e) {
            error.append("\n");
            error.append("failed to get country from MITS address: " + e.getMessage());
        }
        address.country().set(country);

        Province province = null;
        try {
            province = getProvinceByCode(mitsAddress.getState());
            address.province().set(province.name());
        } catch (Throwable e) {
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
     * @throws Exception
     *             if province not found
     */
    private static final Province getProvinceByCode(String code) throws Exception {
        if (code == null) {
            throw new IllegalArgumentException("province code should not be NULL");
        }
        String normalizedCode = code.trim().toUpperCase(Locale.CANADA);

        EntityQueryCriteria<Province> criteria = EntityQueryCriteria.create(Province.class);
        criteria.eq(criteria.proto().code(), normalizedCode);
        List<Province> provinces = Persistence.service().query(criteria);
        if (provinces.isEmpty()) {
            throw new Exception("province not found code = '" + code + "'");
        }
        if (provinces.size() > 1) {
            throw new Exception("more than one province was found code = '" + code + "': " + provinces);
        }
        return provinces.get(0);
    }

    private static final Country getCountryByName(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("county name should not be NULL");
        }
        String normalizedName = name.trim().toUpperCase(Locale.CANADA);

        EntityQueryCriteria<Country> criteria = EntityQueryCriteria.create(Country.class);
        List<Country> countries = Persistence.service().query(criteria);

        List<Country> foundCountries = new ArrayList<Country>();
        for (Country country : countries) {
            if (country.name().getValue().toUpperCase(Locale.CANADA).compareTo(normalizedName) == 0) {
                foundCountries.add(country);
            }
        }
        if (foundCountries.isEmpty()) {
            throw new Exception("country not found name = '" + name + "'");
        }
        if (foundCountries.size() > 1) {
            throw new Exception("more than one country was found name = '" + name + "': " + countries);
        }
        return foundCountries.get(0);
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
