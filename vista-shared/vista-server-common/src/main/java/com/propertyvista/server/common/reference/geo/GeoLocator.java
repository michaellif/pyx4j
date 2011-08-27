/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.server.common.reference.geo;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.geo.GeoPoint;

import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.property.asset.building.Building;

public class GeoLocator {
    private static final Logger log = LoggerFactory.getLogger(GeoLocator.class);

    public enum Mode {
        bypassCache, useCache, updateCache
    };

    private final Mode mode;

    private final GeoCache cache;

    public GeoLocator(Mode mode) throws JAXBException, IOException {
        this.mode = mode;

        cache = new GeoCache();
        if (mode == Mode.useCache) {
            cache.load();
        }
    }

    public void populateGeo(List<Building> buildings) {

        log.debug("Populating geo data with mode [" + mode + "]");

        try {
            for (Building building : buildings) {

                Address address = building.info().address();
                String geoAddress = constructGeo(address);

                GeoPoint gp = null;

                if (mode == Mode.useCache) {
                    gp = cache.findPoint(geoAddress);
                } else if (mode == Mode.bypassCache) {
                    gp = GeoDataEnhancer.getLatLng(geoAddress);
                } else if (mode == Mode.updateCache) {
                    gp = GeoDataEnhancer.getLatLng(geoAddress);
                    cache.update(geoAddress, gp);
                }

                log.debug("[{}] -> [{}]", geoAddress, gp.toString());
                address.location().setValue(gp);
            }

            // print this to System.out
            if (mode == Mode.updateCache) {
                cache.print();
            }
        } catch (Exception e) {
            log.error("Failed to retrieve geo info", e);
        }
    }

    public static String constructGeo(Address address) {
        StringBuilder sb = new StringBuilder();

        sb.append(address.streetNumber().getValue());
        sb.append(" ");
        sb.append(address.streetName().getValue());
        sb.append(", ");
        sb.append(address.city().getValue());
        sb.append(", ");
        sb.append(address.province().code().getValue());
        sb.append(" ");
        sb.append(address.postalCode().getValue());
        sb.append(", ");
        sb.append(address.province().country().name().getValue());

        return sb.toString();
    }
}
