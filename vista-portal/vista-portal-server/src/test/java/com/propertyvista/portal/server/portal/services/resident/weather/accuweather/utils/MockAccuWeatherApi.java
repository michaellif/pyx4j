/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident.weather.accuweather.utils;

import com.propertyvista.portal.server.portal.resident.services.weather.accuweather.AccuWeatherApi;
import com.propertyvista.portal.server.portal.resident.services.weather.accuweather.beans.ForecastBundle;

/**
 * This mock only knows A0A "0A0, Toronto, Canada" location which is "12345". For any other it's going to return <code>null</code>
 */
public class MockAccuWeatherApi implements AccuWeatherApi {

    private final String mockLocationKey = "12345";

    private final ForecastBundle mockForecast;

    public MockAccuWeatherApi(ForecastBundle mockForecast) {
        this.mockForecast = mockForecast;
    }

    @Override
    public String getCountryCode(String countryName) {
        return "Canada".equals(countryName) ? "CA" : null;
    }

    @Override
    public String getLocationKeyForCity(String countryCode, String city) {
        return "CA".equals(countryCode) && "Toronto".equals(city) ? mockLocationKey : null;
    }

    @Override
    public String getLocationKeyForPostalCode(String countryCode, String postalCode) {
        return "CA".equals(countryCode) && "A0A 0A0".equals(postalCode) ? mockLocationKey : null;
    }

    @Override
    public ForecastBundle getForecast(String locationKey) {
        if (locationKey == null || !mockLocationKey.equals(locationKey)) {
            return null;
        }
        return mockForecast;
    }

}
