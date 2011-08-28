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

    private int updateCount = 0;

    private int missedCount = 0;

    private File cacheFile;

    public void loadResource() {
        try {
            String xml = IOUtils.getTextResource(FILENAME, getClass());
            log.debug("Loading {}", xml);
            GeoPairs pairs = MarshallUtil.unmarshal(GeoPairs.class, xml);
            for (GeoPair pair : pairs.getPairs()) {
                GeoPoint gp = GeoPoint.valueOf(pair.getGeoPoint());
                map.put(pair.getAddress(), gp);
            }
        } catch (JAXBException e) {
            log.error("GeoCache resource pars error", e);
        } catch (IOException e) {
            log.error("GeoCache resource read error", e);
        }
        log.info("Loaded {} geo points", map.size());
        updateCount = 0;
    }

    public void load(File cacheFile) {
        this.cacheFile = cacheFile;
        if (cacheFile.canRead()) {
            int b4 = map.size();
            try {
                GeoPairs pairs = MarshallUtil.unmarshal(GeoPairs.class, cacheFile);
                for (GeoPair pair : pairs.getPairs()) {
                    GeoPoint gp = GeoPoint.valueOf(pair.getGeoPoint());
                    map.put(pair.getAddress(), gp);
                }
            } catch (JAXBException e) {
                log.error("GeoCache file read error", e);
            }
            if (b4 != map.size()) {
                log.info("Loaded {} geo points", map.size());
            }
            updateCount = 0;
        }
    }

    public GeoPoint findPoint(String address) {
        GeoPoint gp = map.get(address);
        if (gp == null) {
            missedCount++;
        }
        return gp;
    }

    public void update(String address, GeoPoint gp) {
        map.put(address, gp);
        updateCount++;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public int getMissedCount() {
        return missedCount;
    }

    public void save() {
        if (updateCount != 0) {
            log.info("Updated {} geo points", updateCount);
            GeoPairs pairs = new GeoPairs();
            for (String address : map.keySet()) {
                GeoPoint gp = map.get(address);
                pairs.getPairs().add(new GeoPair(address, gp.toString()));
            }
            try {
                if (cacheFile == null) {
                    MarshallUtil.marshal(pairs, System.out);
                } else {
                    MarshallUtil.marshal(pairs, cacheFile);
                }
                updateCount = 0;
            } catch (JAXBException e) {
                log.error("GeoCache xml creatiuon error", e);
            }
        }
    }

}
