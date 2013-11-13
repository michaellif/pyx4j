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
package com.propertyvista.portal.server.portal.services.resident.weather.accuweather;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.rpc.portal.web.dto.WeatherForecastDTO;
import com.propertyvista.portal.rpc.portal.web.dto.WeatherForecastDTO.TemperatureUnit;
import com.propertyvista.portal.server.portal.services.resident.weather.WeatherForecaster;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.beans.ForecastBundle;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.beans.ForecastBundle.Forecast;

public class AccuWeatherWeatherForecaster implements WeatherForecaster {

    private final AccuWeatherApi apiAdapter;

    public AccuWeatherWeatherForecaster(AccuWeatherApi apiAdapter) {
        this.apiAdapter = apiAdapter;
    }

    @Override
    public List<WeatherForecastDTO> forecastWeather(AddressStructured address) {
        List<WeatherForecastDTO> weatherForecast = new ArrayList<WeatherForecastDTO>();

        do {
            String locationKey = locationKeyFromAddress(address);
            if (locationKey == null) {
                break;
            }

            ForecastBundle forecastBundle = apiAdapter.getForecast(locationKey);
            if (forecastBundle == null) {
                break;
            }

            forecastBunlde2WeatherForecastList(forecastBundle, weatherForecast);

        } while (false);

        return weatherForecast;
    }

    private String locationKeyFromAddress(AddressStructured address) {
        String locationKey = null;
        do {
            String countryCode = apiAdapter.getCountryCode(address.country().name().getValue());
            if (countryCode == null) {
                break;
            }

            if (!address.postalCode().isNull()) {
                locationKey = apiAdapter.getLocationKeyForPostalCode(countryCode, address.postalCode().getValue());
            }

        } while (false);

        return locationKey;
    }

    private void forecastBunlde2WeatherForecastList(ForecastBundle forecastBundle, List<WeatherForecastDTO> weatherForecast) {
        for (Forecast forecast : forecastBundle.getForecast()) {
            weatherForecast.add(forecast2WeatherForecast(forecast));
        }
    }

    private WeatherForecastDTO forecast2WeatherForecast(Forecast forecast) {
        GregorianCalendar cal = forecast.getDateTime().toGregorianCalendar();
        Date from = cal.getTime();
        cal.add(GregorianCalendar.HOUR, 1);
        Date to = cal.getTime();

        WeatherForecastDTO weatherForecastDto = EntityFactory.create(WeatherForecastDTO.class);
        weatherForecastDto.providerName().setValue("AccuWeather.com");
        weatherForecastDto.linkToProvidersWebstite().setValue(forecast.getLink());
        weatherForecastDto.from().setValue(from);
        weatherForecastDto.to().setValue(to);
        weatherForecastDto.temperature().setValue(forecast.getTemperature().getTemperatureValue());

        if ("F".equals(forecast.getTemperature().getUnit())) {
            weatherForecastDto.temperatureUnit().setValue(TemperatureUnit.Fahrenheit);
        } else if ("C".equals(forecast.getTemperature().getUnit())) {
            weatherForecastDto.temperatureUnit().setValue(TemperatureUnit.Celcius);
        }

        weatherForecastDto.weatherDescription().setValue(forecast.getIconPhrase());

        return weatherForecastDto;
    }
}
