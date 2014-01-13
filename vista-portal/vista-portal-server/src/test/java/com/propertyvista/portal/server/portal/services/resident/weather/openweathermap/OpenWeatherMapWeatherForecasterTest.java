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

import java.util.List;

import junit.framework.TestCase;

import com.google.gwt.editor.client.Editor.Ignore;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.server.portal.resident.services.weather.openweathermap.OpenWeatherMapApiImpl;
import com.propertyvista.portal.server.portal.resident.services.weather.openweathermap.OpenWeatherMapWeatherForecaster;

public class OpenWeatherMapWeatherForecasterTest extends TestCase {

    public void testOpenWeatherMapWeatherData2WeatherForecastDTOMapping() {
        OpenWeatherMapWeatherForecaster forecaster = new OpenWeatherMapWeatherForecaster(new MockupOpenWeatherMapApiImpl("mock-forecast-1.xml"));
        List<WeatherGadgetDTO> forecast = forecaster.forecastWeather(EntityFactory.create(AddressStructured.class));
        assertFalse("Forecast data should be present.", forecast.isEmpty());

        assertTrue(Math.abs(22.32 - forecast.get(0).temperature().getValue()) < 0.001);

        // TODO Make linkTopProvidersWebsite point to a URL of a page that displays the relevant information for the city
        // CITY_ID can be fetched using city search API call, more details are at:
        //      http://bugs.openweathermap.org/projects/api/wiki/Api_2_5_searhing
        // assertEquals("http://openweathermap.org/city/{CITY_ID}", forecast.get(0).linkToProvidersWebstite().getValue());
    }

    @Ignore
    public void testRealOpenWeatherMapApiSanity() {
        OpenWeatherMapWeatherForecaster forecaster = new OpenWeatherMapWeatherForecaster(new OpenWeatherMapApiImpl(null));
        AddressStructured address = EntityFactory.create(AddressStructured.class);
        address.country().name().setValue("Canada");
        address.city().setValue("Toronto");

        List<WeatherGadgetDTO> forecast = forecaster.forecastWeather(address);

        assertFalse(forecast.isEmpty());
    }
}
