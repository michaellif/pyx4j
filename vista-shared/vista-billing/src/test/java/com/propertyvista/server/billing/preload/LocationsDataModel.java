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
package com.propertyvista.server.billing.preload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class LocationsDataModel {

    final HashMap<String, Province> provincesMap;

    public LocationsDataModel() {
        provincesMap = new HashMap<String, Province>();
    }

    public void generate(boolean persist) {

        List<Country> countries = createCountries();

        if (persist) {
            Persistence.service().persist(countries);
        }
    }

    private List<Country> createCountries() {

        List<Province> provinces = loadProvincesFromFile();

        Map<String, Country> countriesByName = new HashMap<String, Country>();
        List<Country> countries = new Vector<Country>();

        for (Province province : provinces) {
            Country country = countriesByName.get(province.country().name().getValue());
            if (country == null) {
                country = province.country();
                countriesByName.put(country.name().getValue(), country);
                countries.add(country);
            }
            country.provinces().add(province);

            provincesMap.put(province.code().getValue(), province);
        }

        return countries;
    }

    private static List<Province> loadProvincesFromFile() {
        List<Province> provinces = EntityCSVReciver.create(Province.class).loadFile(IOUtils.resourceFileName("Province.csv", LocationsDataModel.class));
        return provinces;
    }

    private static List<City> updateCitiesWithProvince(List<City> list) {
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
        all.addAll(updateCitiesWithProvince(EntityCSVReciver.create(City.class).loadFile(
                IOUtils.resourceFileName("City-Canada-city.csv", LocationsDataModel.class))));
        all.addAll(updateCitiesWithProvince(EntityCSVReciver.create(City.class).loadFile(
                IOUtils.resourceFileName("City-Canada-town.csv", LocationsDataModel.class))));
        return all;
    }
}
