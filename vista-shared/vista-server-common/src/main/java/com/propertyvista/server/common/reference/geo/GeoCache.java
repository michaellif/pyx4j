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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.server.common.reference.geo.bean.GeoPair;
import com.propertyvista.server.common.reference.geo.bean.GeoPairs;

public class GeoCache {

    private static final Logger log = LoggerFactory.getLogger(GeoCache.class);

    public static final String FILENAME = "geoCache.xml";

    private final Map<String, GeoPoint> map = new HashMap<String, GeoPoint>();

    public void load() throws JAXBException, IOException {
        String xml = IOUtils.getTextResource(FILENAME, getClass());
        log.debug("Loading {}", xml);
        GeoPairs pairs = MarshallUtil.unmarshal(GeoPairs.class, xml);

        for (GeoPair pair : pairs.getPairs()) {
            GeoPoint gp = GeoPoint.valueOf(pair.getGeoPoint());
            map.put(pair.getAddress(), gp);
        }
        log.info("Loaded " + map.size() + " geo points");
    }

    public GeoPoint findPoint(String address) {
        return map.get(address);
    }

    public void update(String address, GeoPoint gp) {
        map.put(address, gp);
    }

    public void print() throws JAXBException {
        GeoPairs pairs = new GeoPairs();

        for (String address : map.keySet()) {
            GeoPoint gp = map.get(address);
            pairs.getPairs().add(new GeoPair(address, gp.toString()));
        }
        MarshallUtil.marshal(pairs, System.out);
    }
}
