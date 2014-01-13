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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO.WeatherType;
import com.propertyvista.portal.server.portal.resident.services.weather.WeatherForecaster;
import com.propertyvista.portal.server.portal.resident.services.weather.openweathermap.Weatherdata.Forecast.Time;

public class OpenWeatherMapWeatherForecaster implements WeatherForecaster {

    private final OpenWeatherMapApi openWeatherApi;

    private static class WheatherCacheKey {
        static String getCacheKey(String city, String country) {
            return String.format("%s_%s_%s", OpenWeatherMapWeatherForecaster.class.getName(), city, country);
        }
    }

    public OpenWeatherMapWeatherForecaster(OpenWeatherMapApi openWeatherApi) {
        this.openWeatherApi = openWeatherApi;
    }

    @Override
    public List<WeatherGadgetDTO> forecastWeather(AddressStructured address) {
        Weatherdata wd = openWeatherApi.getWeatherdata(address.city().getValue(), address.country().name().getValue());
        List<WeatherGadgetDTO> forecast = new LinkedList<WeatherGadgetDTO>();
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
    private WeatherGadgetDTO weatherDataForcastPerTime2WeatherForecastDto(Time weatherDataForecastPerTime) {
        WeatherGadgetDTO dto = EntityFactory.create(WeatherGadgetDTO.class);
        dto.temperature().setValue(Math.round(weatherDataForecastPerTime.getTemperature().getValueAttribute()));
        convertWeatherTypeFromVendor2Vista(weatherDataForecastPerTime.getSymbol().getVar(), dto);

        return dto;
    }

    private void convertWeatherTypeFromVendor2Vista(String wheatherCode, WeatherGadgetDTO dto) {
        if (wheatherCode.equalsIgnoreCase("01d") || wheatherCode.equalsIgnoreCase("01n")) {
            dto.weatherType().setValue(WeatherType.sunny);
        } else if (wheatherCode.equalsIgnoreCase("02d") || wheatherCode.equalsIgnoreCase("02n")) {
            dto.weatherType().setValue(WeatherType.partlyCloudy);
        } else if (wheatherCode.equalsIgnoreCase("03d") || wheatherCode.equalsIgnoreCase("03n")) {
            dto.weatherType().setValue(WeatherType.mostlyCloudy);
        } else if (wheatherCode.equalsIgnoreCase("04d") || wheatherCode.equalsIgnoreCase("04n")) {
            dto.weatherType().setValue(WeatherType.cloudy);
        } else if (wheatherCode.equalsIgnoreCase("09d") || wheatherCode.equalsIgnoreCase("09n")) {
            dto.weatherType().setValue(WeatherType.showers);
        } else if (wheatherCode.equalsIgnoreCase("10d") || wheatherCode.equalsIgnoreCase("10n")) {
            dto.weatherType().setValue(WeatherType.lightShowers);
        } else if (wheatherCode.equalsIgnoreCase("11d") || wheatherCode.equalsIgnoreCase("11n")) {
            dto.weatherType().setValue(WeatherType.thunderShowers);
        } else if (wheatherCode.equalsIgnoreCase("13d") || wheatherCode.equalsIgnoreCase("13n")) {
            dto.weatherType().setValue(WeatherType.snow);
        } else if (wheatherCode.equalsIgnoreCase("50d") || wheatherCode.equalsIgnoreCase("50n")) {
            dto.weatherType().setValue(WeatherType.fog);
        } else
            dto.weatherType().setValue(WeatherType.sunny);

    }

    @Override
    public WeatherGadgetDTO currentWeather(AddressStructured address) {
        Time time = CacheService.get(WheatherCacheKey.getCacheKey(address.city().getValue(), address.country().name().getValue()));
        if (isCachedDataExpired(time)) {
            Weatherdata wd = openWeatherApi.getWeatherdata(address.city().getValue(), address.country().name().getValue());
            if (wd == null || wd.getForecast() == null || wd.getForecast().getTime() == null) {
                return null;
            }
            time = wd.getForecast().getTime().get(0);
            CacheService.put(WheatherCacheKey.getCacheKey(address.city().getValue(), address.country().name().getValue()), time);
        }
        if (time == null) {
            return null;
        }

        return weatherDataForcastPerTime2WeatherForecastDto(time);
    }

    private boolean isCachedDataExpired(Time time) {
        if (time == null || time.getTo() == null) {
            return true;
        }

        return time.getTo().toGregorianCalendar().getTime().before(new Date());
    }
}
