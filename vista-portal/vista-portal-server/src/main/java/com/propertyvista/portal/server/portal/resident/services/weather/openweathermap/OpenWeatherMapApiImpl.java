/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.weather.openweathermap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.ReaderWriter;

public class OpenWeatherMapApiImpl implements OpenWeatherMapApi {

    private final static Logger log = LoggerFactory.getLogger(OpenWeatherMapApiImpl.class);

    private final String apiKey;

    private final boolean enableLogging;

    public OpenWeatherMapApiImpl(String apiKey, boolean enableLogging) {
        this.apiKey = apiKey;
        this.enableLogging = enableLogging;
    }

    public OpenWeatherMapApiImpl(String apiKey) {
        this(apiKey, false);
    }

    @Override
    public Weatherdata getWeatherdata(String cityName, String countryName) {
        Weatherdata weatherData = null;
        if (cityName == null || countryName == null) {
            return null;
        }
        try {
            Client c = new Client();
            if (enableLogging) {
                c.addFilter(new LoggingFilter());
            }
            //@formatter:off
            WebResource r = c.resource("http://api.openweathermap.org/data/2.5/forecast")
                             .queryParam("q", cityName + "," + countryName)
                             .queryParam("mode", "xml")
                             .queryParam("units", "metric");
            //@formatter:on

            if (apiKey != null) {
                r = r.queryParam("APPID", apiKey);
            }
            weatherData = r.get(Weatherdata.class);
        } catch (Throwable e) {
            log.error("Failed to get weather from OpenWeatherMap", e);
        }

        return weatherData;
    }

    private static class LoggingFilter extends ClientFilter {
        @Override
        public ClientResponse handle(ClientRequest clientRequest) throws ClientHandlerException {
            ClientResponse response = getNext().handle(clientRequest);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = response.getEntityInputStream();
            try {
                ReaderWriter.writeTo(in, out);

                byte[] requestEntity = out.toByteArray();
                StringBuilder logBuilder = new StringBuilder();
                if (requestEntity.length != 0) {
                    logBuilder.append(new String(requestEntity)).append("\n");
                }

                log.debug(logBuilder.toString());

                response.setEntityInputStream(new ByteArrayInputStream(requestEntity));
            } catch (IOException ex) {
                throw new ClientHandlerException(ex);
            }

            return response;
        }
    }

}
