/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author vlads
 */
package com.propertyvista.server.common.reference.geo.googleapis;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * See https://developers.google.com/maps/documentation/geocoding/
 */
public class GoogleMapRestService {

    protected Client client;

    protected WebTarget webResource;

    private GoogleMapRestService() {
        client = ClientBuilder.newClient();
        webResource = client.target("http://maps.googleapis.com/maps/api/geocode/");
    }

    private void destroy() {
        client.close();
    }

    public static GeocodeResponse getGeocode(String address) {
        GoogleMapRestService srv = new GoogleMapRestService();
        try {
            Response response = srv.webResource.path("xml").queryParam("address", address).queryParam("sensor", "false").request(MediaType.APPLICATION_XML)
                    .get();
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new Error("HTTP Error " + response.getStatus());
            }
            return response.readEntity(GeocodeResponse.class);
        } finally {
            srv.destroy();
        }
    }
}
