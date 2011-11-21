/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 28, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.reference.geo;

import com.pyx4j.commons.Consts;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;

/**
 * This stores the global GeoCache in file system.
 * The files is updated every 2 minutes.
 */
public class SharedGeoLocator {

    private static GeoLocator geoLocator = new GeoLocator(Mode.updateCache);

    private static long cachePersistIntervals = 2 * Consts.MIN2MSEC;

    private static long nextCachePersistTime = System.currentTimeMillis() + cachePersistIntervals;

    public static void setMode(Mode mode) {
        save();
        if ((geoLocator == null) || (geoLocator.getMode() != mode)) {
            geoLocator = new GeoLocator(mode);
        }
    }

    public static boolean populateGeo(AddressStructured address) {
        boolean found = geoLocator.populateGeo(address);

        if ((geoLocator.getCache().getUpdateCount() > 0) && (nextCachePersistTime < System.currentTimeMillis())) {
            nextCachePersistTime = System.currentTimeMillis() + cachePersistIntervals;
            geoLocator.getCache().save();
        }

        return found;
    }

    public static void save() {
        geoLocator.getCache().save();
    }
}
