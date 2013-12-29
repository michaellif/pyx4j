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
package com.propertyvista.portal.server.portal.resident.services.weather.openweathermap;

import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherForecastDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherForecastDTO.TemperatureUnit;
import com.propertyvista.portal.server.portal.resident.services.weather.WeatherForecaster;
import com.propertyvista.portal.server.portal.resident.services.weather.openweathermap.Weatherdata.Forecast.Time;

public class OpenWeatherMapWeatherForecaster implements WeatherForecaster {

    private final OpenWeatherMapApi openWeatherApi;

    public OpenWeatherMapWeatherForecaster(OpenWeatherMapApi openWeatherApi) {
        this.openWeatherApi = openWeatherApi;
    }

    @Override
    public List<WeatherForecastDTO> forecastWeather(AddressStructured address) {
        Weatherdata wd = openWeatherApi.getWeatherdata(address.city().getValue(), address.country().name().getValue());
        List<WeatherForecastDTO> forecast = new LinkedList<WeatherForecastDTO>();
        if (wd != null) {
            for (Weatherdata.Forecast.Time weatherDataForecastPerTime : wd.getForecast().getTime()) {
                forecast.add(weatherDataForcastPerTime2WeatherForecastDto(weatherDataForecastPerTime));
            }
        }
        return forecast;
    }

    //@formatter:off
    /**
     * Based on the following:<br />
     * <a href="http://bugs.openweathermap.org/projects/api/wiki/Weather_Data">http://bugs.openweathermap.org/projects/api/wiki/Weather_Data</a><br/>
     * <a href="http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes">http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes</a><br />
     */
    //@formatter:on
    private WeatherForecastDTO weatherDataForcastPerTime2WeatherForecastDto(Time weatherDataForecastPerTime) {
        WeatherForecastDTO dto = EntityFactory.create(WeatherForecastDTO.class);
        dto.providerName().setValue("OpenWeatherMap");
        dto.linkToProvidersWebstite().setValue("http://openweathermap.org"); // TODO make this link point to city information
        dto.from().setValue(weatherDataForecastPerTime.getFrom().toGregorianCalendar().getTime());
        dto.to().setValue(weatherDataForecastPerTime.getTo().toGregorianCalendar().getTime());
        dto.temperature().setValue((double) weatherDataForecastPerTime.getTemperature().getValueAttribute());
        if ("celsius".equals(weatherDataForecastPerTime.getTemperature().getUnit())) {
            dto.temperatureUnit().setValue(TemperatureUnit.Celcius);
        } else if ("fahrenheit".equals(weatherDataForecastPerTime.getTemperature().getUnit())) {
            dto.temperatureUnit().setValue(TemperatureUnit.Fahrenheit);
        }

        dto.weatherDescription().setValue(weatherDataForecastPerTime.getSymbol().getName());
        dto.weatherIconUrl().setValue("http://openweathermap.org/img/w/" + weatherDataForecastPerTime.getSymbol().getVar() + ".png");

        return dto;
    }
}
