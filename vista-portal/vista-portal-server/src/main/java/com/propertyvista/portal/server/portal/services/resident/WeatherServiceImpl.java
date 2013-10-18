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
package com.propertyvista.portal.server.portal.services.resident;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.services.resident.WeatherService;
import com.propertyvista.portal.rpc.portal.web.dto.WeatherForecastDTO;
import com.propertyvista.portal.server.portal.services.resident.weather.WeatherForecaster;
import com.propertyvista.portal.server.security.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class WeatherServiceImpl implements WeatherService {

    private static final String CACHE_KEY_PREFIX = "WEATHER_FOR_BUILDING_";

    /**
     * this is max time that weather in cache is allowed to live without renewal in milliseconds;
     */
    private static final long MAX_WEATHER_FORECAST_AGE = 3L * 60L * 60L * 1000L;

    @Override
    public void getForecast(AsyncCallback<Vector<WeatherForecastDTO>> callback) {
        Building contextBuilding = getContextTenantsBuilding();
        Vector<WeatherForecastDTO> weather = getWeatherFromCache(contextBuilding);
        if (weather == null) {
            AddressStructured address = AddressRetriever.getLeaseParticipantCurrentAddress(TenantAppContext.getCurrentUserTenantInLease());
            weather = new Vector<WeatherForecastDTO>(ServerSideFactory.create(WeatherForecaster.class).forecastWeather(address));
            putWeatherToCache(contextBuilding, weather);
        }

        callback.onSuccess(weather);
    }

    private Building getContextTenantsBuilding() {
        Lease lease = TenantAppContext.getCurrentUserLease();
        return lease.unit().building().<Building> createIdentityStub();
    }

    private void putWeatherToCache(Building contextBuilding, Vector<WeatherForecastDTO> weather) {
        CacheService.put(CACHE_KEY_PREFIX + contextBuilding.getPrimaryKey().toString(), new WeatherForecastHolder(weather, new Date()));
    }

    private Vector<WeatherForecastDTO> getWeatherFromCache(Building contextBuilding) {
        WeatherForecastHolder forecastHolder = CacheService.get(CACHE_KEY_PREFIX + contextBuilding.getPrimaryKey().toString());
        if (forecastHolder != null && (forecastHolder.timestamp.getTime() + MAX_WEATHER_FORECAST_AGE) > new Date().getTime()) {
            return forecastHolder.weather;
        } else {
            return null;
        }
    }

    private static class WeatherForecastHolder {

        private final Vector<WeatherForecastDTO> weather;

        private final Date timestamp;

        public WeatherForecastHolder(Vector<WeatherForecastDTO> weather, Date timestamp) {
            this.weather = weather;
            this.timestamp = timestamp;
        }
    }

}
