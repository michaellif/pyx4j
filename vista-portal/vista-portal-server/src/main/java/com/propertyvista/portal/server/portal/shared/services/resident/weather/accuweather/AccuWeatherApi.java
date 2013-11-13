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

import com.propertyvista.portal.server.portal.services.resident.weather.accuweather.beans.ForecastBundle;

public interface AccuWeatherApi {

    String getCountryCode(String countryName);

    String getLocationKeyForCity(String countryCode, String city);

    String getLocationKeyForPostalCode(String countryCode, String postalCode);

    ForecastBundle getForecast(String locationKey);
}
