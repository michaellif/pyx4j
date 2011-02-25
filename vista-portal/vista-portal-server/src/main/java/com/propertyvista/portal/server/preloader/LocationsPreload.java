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
import java.util.Vector;

import com.propertyvista.portal.domain.ref.Country;
import com.propertyvista.portal.domain.ref.Province;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;

public class LocationsPreload extends AbstractDataPreloader {

    private static String resourceFileName(String fileName) {
        return LocationsPreload.class.getPackage().getName().replace('.', '/') + "/" + fileName;
    }

    @Override
    public String create() {
        int countriesCount = 0;
        int provinceCount = 0;

        List<Province> provinces = EntityCSVReciver.create(Province.class).loadFile(resourceFileName("Province.csv"));

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
        PersistenceServicesFactory.getPersistenceService().persist(toSaveCountry);
        countriesCount += toSaveCountry.size();
        PersistenceServicesFactory.getPersistenceService().persist(provinces);
        provinceCount += provinces.size();

        StringBuilder b = new StringBuilder();
        b.append("Created " + countriesCount + " Countries").append('\n');
        b.append("Created " + provinceCount + " Provinces");
        return b.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(Province.class, Country.class);
    }
}
