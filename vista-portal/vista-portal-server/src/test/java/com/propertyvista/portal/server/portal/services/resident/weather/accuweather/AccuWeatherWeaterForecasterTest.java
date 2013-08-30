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

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.rpc.portal.web.dto.WeatherForecastDTO;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.beans.ForecastBundle;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.readers.ForecastBundleReader;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.utils.MockAccuWeatherApi;

public class AccuWeatherWeaterForecasterTest extends TestCase {

    public void testNoAddress() throws IOException {
        AccuWeatherWeatherForecaster forecaster = new AccuWeatherWeatherForecaster(mockAccuWeatherApiForWeatherForcast("single-forecast-mock.json"));
        List<WeatherForecastDTO> forecast = forecaster.forecastWeather(EntityFactory.create(AddressStructured.class));
        assertTrue(forecast.isEmpty());
    }

    public void testNoPostalCode() throws JAXBException, IOException {
        AccuWeatherWeatherForecaster forecaster = new AccuWeatherWeatherForecaster(mockAccuWeatherApiForWeatherForcast("single-forecast-mock.json"));

        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.country().name().setValue("Canada");
        address.city().setValue(null);
        address.postalCode().setValue(null);

        List<WeatherForecastDTO> forecast = forecaster.forecastWeather(address);
        assertTrue(forecast.isEmpty());
    }

    public void testUnknownCityName() throws JAXBException, IOException {
        AccuWeatherWeatherForecaster forecaster = new AccuWeatherWeatherForecaster(mockAccuWeatherApiForWeatherForcast("single-forecast-mock.json"));

        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.country().name().setValue("Canada");
        address.city().setValue("Abracadabra");

        List<WeatherForecastDTO> forecast = forecaster.forecastWeather(address);
        assertTrue(forecast.isEmpty());
    }

    public void testForecast() throws JAXBException, IOException {
        AccuWeatherWeatherForecaster forecaster = new AccuWeatherWeatherForecaster(mockAccuWeatherApiForWeatherForcast("single-forecast-mock.json"));

        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.country().name().setValue("Canada");
        address.city().setValue("Toronto");
        address.postalCode().setValue("A0A 0A0");

        List<WeatherForecastDTO> forecast = forecaster.forecastWeather(address);
        assertEquals(1, forecast.size());

        assertEquals(DateUtils.detectDateformat("2013-01-18 16:00:00"), forecast.get(0).from().getValue());
        assertEquals(DateUtils.detectDateformat("2013-01-18 17:00:00"), forecast.get(0).to().getValue());
        assertTrue(Math.abs(forecast.get(0).temperature().getValue() - 25d) < 0.001);
        assertEquals("F", forecast.get(0).temperatureUnit().getValue());
    }

    private MockAccuWeatherApi mockAccuWeatherApiForWeatherForcast(String resourceName) throws IOException {
        return new MockAccuWeatherApi(new ForecastBundleReader().readFrom(ForecastBundle.class, null, null, null, null,
                getClass().getResourceAsStream("single-forecast-mock.json")));
    }

}
