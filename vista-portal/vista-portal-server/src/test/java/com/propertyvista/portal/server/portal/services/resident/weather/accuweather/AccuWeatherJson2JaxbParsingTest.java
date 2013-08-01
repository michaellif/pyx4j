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
package com.propertyvista.portal.server.portal.services.resident.weather.accuweather;

import java.io.IOException;
import java.util.GregorianCalendar;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.beans.AccuWeatherCountry;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.beans.ForecastBundle;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.readers.AccuWeatherCountryReader;
import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.readers.ForecastBundleReader;

public class AccuWeatherJson2JaxbParsingTest extends TestCase {

    public void testAccuWeatherCountryReader() throws WebApplicationException, IOException {
        AccuWeatherCountry accuWeatherCountry = new AccuWeatherCountryReader().readFrom(AccuWeatherCountry.class, null, null, null, null, getClass()
                .getResourceAsStream("countries-search-response.json"));

        assertNotNull(accuWeatherCountry);
        assertEquals("ES", accuWeatherCountry.getCountryCode());
        assertEquals("Spain", accuWeatherCountry.getEnglishName());
        assertEquals("España", accuWeatherCountry.getLocalizedName());

    }

    public void testForecastBundleReader() throws JAXBException, IOException {

        ForecastBundle forecastBundle = new ForecastBundleReader().readFrom(ForecastBundle.class, null, null, null, null,
                getClass().getResourceAsStream("single-forecast-mock.json"));

        assertNotNull(forecastBundle);
        assertEquals(1, forecastBundle.getForecast().size());

        GregorianCalendar actualDateTime = forecastBundle.getForecast().get(0).getDateTime().toGregorianCalendar();
        assertEquals(2013, actualDateTime.get(GregorianCalendar.YEAR));
        assertEquals(GregorianCalendar.JANUARY, actualDateTime.get(GregorianCalendar.MONTH));
        assertEquals(18, actualDateTime.get(GregorianCalendar.DATE));
        assertEquals(16, actualDateTime.get(GregorianCalendar.HOUR_OF_DAY));
        assertEquals(0, actualDateTime.get(GregorianCalendar.MINUTE));
        assertEquals(0, actualDateTime.get(GregorianCalendar.SECOND));
        assertEquals(-5L * 60L * 60L * 1000L, actualDateTime.getTimeZone().getRawOffset());

        assertEquals(1358542800L, forecastBundle.getForecast().get(0).getEpochDateTime());

        assertEquals(1, forecastBundle.getForecast().get(0).getWeatherIcon());

        assertEquals("Sunny", forecastBundle.getForecast().get(0).getIconPhrase());

        assertTrue((forecastBundle.getForecast().get(0).getTemperature().getTemperatureValue() - 25.0) < 0.000000000001);
        assertEquals("F", forecastBundle.getForecast().get(0).getTemperature().getUnit());
        assertEquals(18, forecastBundle.getForecast().get(0).getTemperature().getUnitType());

        assertEquals(0, forecastBundle.getForecast().get(0).getPrecipitationProbability());
        assertEquals("http://m.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?lang=en-us?hbhhour=16", forecastBundle.getForecast()
                .get(0).getMobileLink());
        assertEquals("http://www.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?lang=en-us?hbhhour=16", forecastBundle
                .getForecast().get(0).getLink());
    }

}
