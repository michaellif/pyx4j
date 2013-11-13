/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-01
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident.weather;

import com.pyx4j.config.server.FacadeFactory;

import com.propertyvista.portal.server.portal.services.resident.weather.openweathermap.OpenWeatherMapApiImpl;
import com.propertyvista.portal.server.portal.services.resident.weather.openweathermap.OpenWeatherMapWeatherForecaster;

public class WeatherForecasterFactory implements FacadeFactory<WeatherForecaster> {

    @Override
    public WeatherForecaster getFacade() {
        return new OpenWeatherMapWeatherForecaster(new OpenWeatherMapApiImpl(null));
    }

}
