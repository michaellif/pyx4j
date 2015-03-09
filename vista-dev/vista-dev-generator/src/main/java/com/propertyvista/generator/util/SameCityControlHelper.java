/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2015
 * @author ernestog
 */
package com.propertyvista.generator.util;

import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.generator.BuildingsGenerator.BuildingsGeneratorConfig;

public class SameCityControlHelper {

    // This value has to be equal to same number of addresses for each city in xls addresses files
    private static final int NUM_BUILDINGS_SAME_CITY = 4;

    private static class LastAddressMainData {
        private String city = null;

        private String provinceCode = null;

        private int nLocations = 0;

        private void setNewMainDataAddress(InternationalAddress address) {
            city = address.city().getValue();
            ISOProvince prov = ISOProvince.forName(address.province().getValue(), address.country().getValue());
            provinceCode = prov.code;
            nLocations = 1;
        }
    }

    private LastAddressMainData lastAddress = null;

    public BuildingsGeneratorConfig updatedBuildingConfig(BuildingsGeneratorConfig config) {
        if (lastAddress != null) {
            config.provinceCode = lastAddress.provinceCode;
            config.city = lastAddress.city;
        } else {
            // Get a new random city for the building and set address data to BuildingGeneratorConfing
            InternationalAddress address = CommonsGenerator.randomCityAddressByCountry(config.country);
            ISOProvince province = ISOProvince.forName(address.province().getValue(), address.country().getValue());
            config.provinceCode = province.code;
            config.city = address.city().getValue();
            DataGenerator.cleanRandomDuplicates("addressByProvinceAndCity");
        }

        return config;
    }

    public void updateLastAddress(InternationalAddress address) {

        if (lastAddress == null) {
            lastAddress = new LastAddressMainData();
            lastAddress.setNewMainDataAddress(address);
            return;
        }

        if (++lastAddress.nLocations >= NUM_BUILDINGS_SAME_CITY) {
            lastAddress = null;
        }

    }

}
