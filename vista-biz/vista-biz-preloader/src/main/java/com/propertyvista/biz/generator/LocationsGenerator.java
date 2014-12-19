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
 */
package com.propertyvista.biz.generator;

import java.util.List;
import java.util.Vector;

import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.shared.config.VistaFeatures;

public class LocationsGenerator {

    public static final boolean ukLocationsReady = false;

    private static List<City> updateCitiesWithProvince(List<City> list) {
        List<City> all = new Vector<City>();
        ISOProvince province = null;
        for (City c : list) {
            if (c.province().isNull()) {
                c.province().setValue(province);
            } else {
                province = c.province().getValue();
            }
            if (!c.name().isNull()) {
                all.add(c);
            }
        }
        return all;
    }

    public static List<City> loadCityFromFile() {
        List<City> all = new Vector<City>();
        if (ukLocationsReady && VistaFeatures.instance().countryOfOperation() == CountryOfOperation.UK) {
            all.addAll(updateCitiesWithProvince(EntityCSVReciver.create(City.class).loadResourceFile(
                    IOUtils.resourceFileName("City-UK-town.csv", LocationsGenerator.class))));
        } else {
            all.addAll(updateCitiesWithProvince(EntityCSVReciver.create(City.class).loadResourceFile(
                    IOUtils.resourceFileName("City-Canada-city.csv", LocationsGenerator.class))));
            all.addAll(updateCitiesWithProvince(EntityCSVReciver.create(City.class).loadResourceFile(
                    IOUtils.resourceFileName("City-Canada-town.csv", LocationsGenerator.class))));
        }
        return all;
    }
}
