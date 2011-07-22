/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.generator.LocationsGenerator;
import com.propertyvista.server.common.reference.SharedData;

public class LocationsPreload extends AbstractDataPreloader {

    @Override
    public String create() {
        int countriesCount = 0;
        int provinceCount = 0;

        List<Province> provinces = LocationsGenerator.loadProvincesFromFile();
        List<Country> countries = LocationsGenerator.createCountries(provinces);

        SharedData.registerProvinces(provinces);
        SharedData.registerCountries(countries);

        PersistenceServicesFactory.getPersistenceService().persist(countries);
        countriesCount += countries.size();
        PersistenceServicesFactory.getPersistenceService().persist(provinces);
        provinceCount += provinces.size();

        Map<String, Province> provincesMap = new HashMap<String, Province>();
        for (Province province : provinces) {
            provincesMap.put(province.name().getValue(), province);
        }
        List<City> cities = LocationsGenerator.loadCityFromFile();
        for (City c : cities) {
            c.province().set(provincesMap.get(c.province().name().getValue()));
        }
        PersistenceServicesFactory.getPersistenceService().persist(cities);

        StringBuilder b = new StringBuilder();
        b.append("Created " + countriesCount + " Countries").append('\n');
        b.append("Created " + provinceCount + " Provinces");
        b.append("Created " + cities.size() + " Cities");
        return b.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(Province.class, Country.class);
    }
}
