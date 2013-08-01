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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.rpc.portal.dto.WeatherForecastDTO;

public class OpenWeatherMapWeatherForecasterTest extends TestCase {

    @Override
    public void setUp() {
    }

    public void testSanity() {
        OpenWeatherMapWeatherForecaster forecaster = new OpenWeatherMapWeatherForecaster(new MockupOpenWeatherMapApi("mock-forecast-1.xml"));
        List<WeatherForecastDTO> forecast = forecaster.forecastWeather(EntityFactory.create(AddressStructured.class));
        assertFalse(forecast.isEmpty());
    }
}
