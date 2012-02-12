/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertvista.generator.II.test;

import java.util.List;

import junit.framework.TestCase;

import com.propertvista.generator.II.DataModel;
import com.propertvista.generator.II.InMemoryDataModel;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.generator.LocationsGenerator;

public class BasicGeneratorTest extends TestCase {

    private DataModel dataModel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dataModel = new InMemoryDataModel();

        {

            List<Province> provinces = LocationsGenerator.loadProvincesFromFile();
            List<Country> countries = LocationsGenerator.createCountries(provinces);

            dataModel.persist(countries);
            dataModel.persist(provinces);

        }
    }

    public void testCountries() {
        System.out.println(dataModel.query(Country.class).get(0));
    }

}
