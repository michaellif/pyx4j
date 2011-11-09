/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.server.common.reference;

import java.util.List;

import com.pyx4j.entity.cache.CacheService;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.generator.LocationsGenerator;

/**
 * Please remove this class and every usage of one!! VladS
 */
@Deprecated
public class SharedData {

    public static void init() {
        if (CacheService.get("provinces") == null) {
            List<Province> provinces;
            registerProvinces(provinces = LocationsGenerator.loadProvincesFromFile());
            registerCountries(LocationsGenerator.createCountries(provinces));
        }
    }

    public static void registerProvinces(List<Province> p) {
        CacheService.put("provinces", p);
    }

    public static void registerCountries(List<Country> c) {
        CacheService.put("countries", c);
    }

    public static List<Province> getProvinces() {
        return CacheService.get("provinces");
    }

    public static Province findProvinceByCode(String code) {
        for (Province province : getProvinces()) {
            if (province.code().getValue().equals(code)) {
                return province;
            }
        }
        return null;
    }

    public static Country findCountryCanada() {
        return findCountry("Canada");
    }

    public static Country findCountry(String name) {
        List<Country> countries = CacheService.get("countries");
        for (Country country : countries) {
            if (country.name().getValue().equals(name)) {
                return country;
            }
        }
        return null;
    }
}
