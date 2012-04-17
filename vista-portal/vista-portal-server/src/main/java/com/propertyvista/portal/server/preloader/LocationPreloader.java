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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.biz.preloader.LocationsGenerator;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.reference.SharedData;

public class LocationPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        List<Country> countries = LocationsGenerator.createCountries();
        List<Province> provinces = new ArrayList<Province>();
        for (Country country : countries) {
            provinces.addAll(country.provinces());
        }

        Persistence.service().persist(countries);

        SharedData.registerCountries(countries);
        SharedData.registerProvinces(provinces);

        List<City> cities = LocationsGenerator.loadCityFromFile();
        Persistence.service().persist(cities);

        StringBuilder b = new StringBuilder();
        b.append("Created " + countries.size() + " Countries").append('\n');
        b.append("Created " + provinces.size() + " Provinces").append('\n');
        b.append("Created " + cities.size() + " Cities");
        return b.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(Province.class, Country.class);
    }
}
