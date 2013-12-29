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
 * @version $Id$
 */
package com.propertyvista.server.common.reference.geo.googleapis;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * See https://developers.google.com/maps/documentation/geocoding/
 */
public class GoogleMapRestService {

    protected Client client;

    protected WebResource webResource;

    private GoogleMapRestService() {
        client = Client.create();
        webResource = client.resource("http://maps.googleapis.com/maps/api/geocode/");
    }

    private void destroy() {
        client.destroy();
    }

    public static GeocodeResponse getGeocode(String address) {
        GoogleMapRestService srv = new GoogleMapRestService();
        try {
            ClientResponse response = srv.webResource.path("xml").queryParam("address", address).queryParam("sensor", "false").type("application/xml")
                    .get(ClientResponse.class);
            if (response.getClientResponseStatus() != ClientResponse.Status.OK) {
                throw new Error("HTTP Error " + response.getStatus());
            }
            return response.getEntity(GeocodeResponse.class);
        } finally {
            srv.destroy();
        }
    }
}
