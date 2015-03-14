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
 */
package com.propertyvista.portal.server.portal.resident.services.weather.openweathermap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.misc.VistaTODO;

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
    public Weatherdata getWeatherdata(String cityName, ISOCountry country) {
        if (!VistaTODO.VISTA_6054_OpenWeatherImplmentedProperly) {
            return null;
        }

        Weatherdata weatherData = null;
        if (cityName == null || country == null) {
            return null;
        }
        try {
            ClientConfig clientConfig = new ClientConfig();
            if (enableLogging) {
                clientConfig.register(new LoggerFilter(100000));
            }
            Client c = ClientBuilder.newClient(clientConfig);
            //@formatter:off
            WebTarget r = c.target("http://api.openweathermap.org/data/2.5/forecast")
                             .queryParam("q", cityName + "," + country.name)
                             .queryParam("mode", "xml")
                             .queryParam("units", "metric");
            //@formatter:on

            if (apiKey != null) {
                r = r.queryParam("APPID", apiKey);
            }
            weatherData = r.request(MediaType.APPLICATION_XML).get(Weatherdata.class);
        } catch (Throwable e) {
            log.error("Failed to get weather from OpenWeatherMap", e);
        }

        return weatherData;
    }

    static class LoggerFilter implements ClientResponseFilter {
        final int maxEntitySize;

        public LoggerFilter(int maxEntitySize) {
            this.maxEntitySize = maxEntitySize;
        }

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            InputStream stream = responseContext.getEntityStream();
            if (!stream.markSupported()) {
                stream = new BufferedInputStream(stream);
            }
            stream.mark(maxEntitySize + 1);
            final byte[] entity = new byte[maxEntitySize + 1];
            final int entitySize = stream.read(entity);
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(new String(entity, 0, Math.min(entitySize, maxEntitySize)));
            if (entitySize > maxEntitySize) {
                logBuilder.append("...[trimmed to " + maxEntitySize + " bytes]");
            }
            logBuilder.append('\n');
            log.debug(logBuilder.toString());
            // reset stream position to the initial point
            stream.reset();
            responseContext.setEntityStream(stream);
        }
    }
}
