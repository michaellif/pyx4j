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
package com.propertyvista.portal.server.portal.resident.services.weather.accuweather;

import com.sun.jersey.api.uri.UriTemplate;

import com.propertyvista.portal.server.portal.resident.services.weather.accuweather.beans.ForecastBundle;

public class AccuWeatherApiImpl implements AccuWeatherApi {

    private final String apiKey;

    private final String apiWeatherUrl;

    private final String version;

    public AccuWeatherApiImpl(String apiKey, boolean isDev) {
        this.apiKey = apiKey;
        this.apiWeatherUrl = isDev ? "apidev.accuweather.com" : "api.accuweather.com";
        this.version = "1";
    }

    @Override
    public String getCountryCode(String countryName) {
        new UriTemplate(
                "http://{accuWeatherServer}.accuweather.com/locations/{version}/countries/{regionCode}{.{format}}?{language={languageCode}}&apikey={your key}");
        return null;
    }

    @Override
    public String getLocationKeyForCity(String countryCode, String city) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLocationKeyForPostalCode(String countryCode, String postalCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ForecastBundle getForecast(String locationKey) {
        // TODO Auto-generated method stub
        return null;
    }
}
