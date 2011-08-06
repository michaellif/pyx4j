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

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.generator.LocationsGenerator;

public class SharedData {

    private static List<Province> provinces;

    private static List<Country> countries;

    public static void init() {
        if (provinces != null) { // assume that this is already initialized
            return;
        }
        provinces = LocationsGenerator.loadProvincesFromFile();
        countries = LocationsGenerator.createCountries(provinces);
    }

    public static void registerProvinces(List<Province> p) {
        provinces = p;
    }

    public static void registerCountries(List<Country> c) {
        countries = c;
    }

    public static List<Province> getProvinces() {
        return provinces;
    }

    public static Province findProvinceByCode(String code) {
        if (provinces == null) {
            throw new IllegalStateException("SharedData is not initialized");
        }
        for (Province province : provinces) {
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
        for (Country country : countries) {
            if (country.name().getValue().equals(name)) {
                return country;
            }
        }
        return null;
    }
}
