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

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.geo.GeoPoint;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.building.Building;

public class GeoLocator {

    private static final Logger log = LoggerFactory.getLogger(GeoLocator.class);

    public enum Mode {
        bypassCache, useCacheOnly, updateCache
    };

    private final Mode mode;

    private final GeoCache cache;

    public GeoLocator(Mode mode) {
        this.mode = mode;
        cache = new GeoCache();
        if (mode != Mode.bypassCache) {
            cache.loadResource();
        }
        this.cache.load(new File(GeoCache.FILENAME));
    }

    public GeoCache getCache() {
        return cache;
    }

    public void populateGeo(List<Building> buildings) {
        log.debug("Populating geo data with mode [{}]", mode);
        try {
            int missed = 0;
            for (Building building : buildings) {
                if (!populateGeo(building.info().address())) {
                    missed++;
                }
            }
            // print this to System.out
            if (mode != Mode.useCacheOnly) {
                cache.save();
            }
            if (missed != 0) {
                log.info("Not found geo for {} locations, cache missed {}", missed, cache.getMissedCount());
            }
        } catch (Exception e) {
            log.error("Failed to retrieve geo info", e);
        }
    }

    public boolean populateGeo(AddressStructured address) {
        String geoAddress = constructGeo(address);
        GeoPoint gp = null;
        switch (mode) {
        case useCacheOnly:
            gp = cache.findPoint(geoAddress);
            break;
        case bypassCache:
            gp = GeoDataEnhancer.getLatLng(geoAddress);
            break;
        case updateCache:
            gp = cache.findPoint(geoAddress);
            if (gp == null) {
                gp = GeoDataEnhancer.getLatLng(geoAddress);
                if (gp != null) {
                    cache.update(geoAddress, gp);
                }
            }
            break;
        }
        if (gp != null) {
            address.location().setValue(gp);
            return true;
        } else {
            log.debug("Location not fround for: {}", geoAddress);
            return false;
        }
    }

    public static String constructGeo(AddressStructured address) {
        StringBuilder sb = new StringBuilder();

        sb.append(address.streetNumber().getStringView());
        sb.append(" ");
        sb.append(address.streetName().getStringView());
        sb.append(", ");
        sb.append(address.city().getStringView());
        sb.append(", ");
        sb.append(address.province().code().getStringView());
        sb.append(" ");
        sb.append(address.postalCode().getStringView());
        if (!address.province().country().name().isNull()) {
            sb.append(", ");
            sb.append(address.province().country().name().getStringView());
        }

        return sb.toString();
    }
}
