/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident.weather.openweathermap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.propertyvista.portal.server.portal.resident.services.weather.openweathermap.OpenWeatherMapApi;
import com.propertyvista.portal.server.portal.resident.services.weather.openweathermap.Weatherdata;

/**
 * Returns static data parsed from a resource response file provided in the constructor
 */
public class MockupOpenWeatherMapApiImpl implements OpenWeatherMapApi {

    private final String mockApiResponeResourceName;

    public MockupOpenWeatherMapApiImpl(String mockApiResponeResourceName) {
        this.mockApiResponeResourceName = mockApiResponeResourceName;
    }

    @Override
    public Weatherdata getWeatherdata(String cityName, String countryName) {
        Weatherdata weatherData = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(Weatherdata.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            weatherData = (Weatherdata) unmarshaller.unmarshal(getClass().getResource(mockApiResponeResourceName));
        } catch (JAXBException e) {
            throw new Error(e);
        }
        return weatherData;
    }

}
