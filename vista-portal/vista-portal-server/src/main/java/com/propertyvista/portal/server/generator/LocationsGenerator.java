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
package com.propertyvista.portal.server.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.portal.server.preloader.LocationsPreload;
import com.propertyvista.portal.server.ptapp.util.PreloadUtil;

import com.pyx4j.essentials.server.csv.EntityCSVReciver;

public class LocationsGenerator {

    public static List<Province> loadProvincesFromFile() {
        List<Province> provinces = EntityCSVReciver.create(Province.class).loadFile(PreloadUtil.resourceFileName(LocationsPreload.class, "Province.csv"));
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
}
