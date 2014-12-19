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
package com.propertyvista.test.mock.models;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.ref.ProvincePolicyNode;
import com.propertyvista.test.mock.MockDataModel;

public class LocationsDataModel extends MockDataModel<ProvincePolicyNode> {

    final HashMap<ISOProvince, ProvincePolicyNode> provincesMap;

    public LocationsDataModel() {
        provincesMap = new HashMap<>();
    }

    @Override
    protected void generate() {
        List<ProvincePolicyNode> provinces = loadProvincesFromFile();

        for (ProvincePolicyNode province : provinces) {
            provincesMap.put(province.province().getValue(), province);
            Persistence.service().persist(province);
        }
    }

    private static List<ProvincePolicyNode> loadProvincesFromFile() {
        List<ProvincePolicyNode> provinces = EntityCSVReciver.create(ProvincePolicyNode.class).loadResourceFile(
                IOUtils.resourceFileName("Province.csv", LocationsDataModel.class));
        return provinces;
    }

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
        all.addAll(updateCitiesWithProvince(EntityCSVReciver.create(City.class).loadResourceFile(
                IOUtils.resourceFileName("City-Canada-city.csv", LocationsDataModel.class))));
        all.addAll(updateCitiesWithProvince(EntityCSVReciver.create(City.class).loadResourceFile(
                IOUtils.resourceFileName("City-Canada-town.csv", LocationsDataModel.class))));
        return all;
    }

    ProvincePolicyNode getProvincePolicyNode(ISOProvince prov) {
        return prov == null ? null : provincesMap.get(prov);
    }

    ProvincePolicyNode getProvincePolicyNode(String code) {
        return getProvincePolicyNode(ISOProvince.forCode(code));
    }
}
