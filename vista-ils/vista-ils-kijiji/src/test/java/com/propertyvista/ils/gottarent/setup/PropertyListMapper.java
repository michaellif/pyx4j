/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 10, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.setup;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

public class PropertyListMapper {
    public static Logger logger = LoggerFactory.getLogger(PropertyListMapper.class);

    public static void main(String[] args) {
        List<PropertyId> gr_data = EntityCSVReciver.create(PropertyId.class).loadResourceFile(
                IOUtils.resourceFileName("greenwin/gr_list.csv", PropertyListMapper.class));
        List<PropertyId> pv_data = EntityCSVReciver.create(PropertyId.class).loadResourceFile(
                IOUtils.resourceFileName("greenwin/pv_list.csv", PropertyListMapper.class));

        Map<String, String> grMap = new HashMap<String, String>();
        for (PropertyId pid : gr_data) {
            String key = getKey(pid);
            if (grMap.containsKey(key)) {
                logger.info("duplicate key: {} for {}; prev {}", key, pid.identificator().getValue(), grMap.get(key));
                continue;
            }
            logger.info("adding key: {} for {}", key, pid.identificator().getValue());
            grMap.put(key, pid.identificator().getValue());
        }

        int matched = 0, unmatched = 0;
        Collections.sort(pv_data, new Comparator<PropertyId>() {
            @Override
            public int compare(PropertyId o1, PropertyId o2) {
                return o1.postalCode().compareTo(o2.postalCode());
            }
        });
        for (PropertyId pid : pv_data) {
            String key = getKey(pid);
            if (grMap.containsKey(key)) {
                matched++;
            } else {
                logger.info("unmatched key: {}", key);
                unmatched++;
            }
        }
        logger.info("matched = {}; unmatched = {}", matched, unmatched);
    }

    private static String getKey(PropertyId pid) {
        return new String(pid.postalCode().getValue() + "_" + pid.streetNumber().getValue()).replaceAll(" ", "").toLowerCase();
    }
}
