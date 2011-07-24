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
package com.propertyvista.server.common.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class LocationsGenerator {

    public static List<Province> loadProvincesFromFile() {
        List<Province> provinces = EntityCSVReciver.create(Province.class).loadFile(IOUtils.resourceFileName("Province.csv", LocationsGenerator.class));
        return provinces;
    }

    public static List<Country> createCountries(List<Province> provinces) {

        Map<String, Country> countries = new HashMap<String, Country>();
        List<Country> toSaveCountry = new Vector<Country>();
        for (Province provinceInfo : provinces) {
            Country c = countries.get(provinceInfo.country().name().getValue());
            if (c == null) {
                c = provinceInfo.country();
                countries.put(c.name().getValue(), c);
                toSaveCountry.add(c);
            } else {
                provinceInfo.country().set(c);
            }
        }

        return toSaveCountry;
    }

    private static List<City> mergeProvinces(List<City> list) {
        List<City> all = new Vector<City>();
        String provinceName = null;
        for (City c : list) {
            if (c.province().name().isNull()) {
                c.province().name().setValue(provinceName);
            } else {
                provinceName = c.province().name().getValue();
            }
            if (!c.name().isNull()) {
                all.add(c);
            }
        }
        return all;
    }

    public static List<City> loadCityFromFile() {
        List<City> all = new Vector<City>();
        all.addAll(mergeProvinces(EntityCSVReciver.create(City.class).loadFile(IOUtils.resourceFileName("City-Canada-city.csv", LocationsGenerator.class))));
        all.addAll(mergeProvinces(EntityCSVReciver.create(City.class).loadFile(IOUtils.resourceFileName("City-Canada-town.csv", LocationsGenerator.class))));
        return all;
    }
}
