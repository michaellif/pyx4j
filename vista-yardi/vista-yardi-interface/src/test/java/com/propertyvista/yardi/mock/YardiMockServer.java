/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;

public class YardiMockServer {

    private static final Logger log = LoggerFactory.getLogger(YardiMockServer.class);

    private final Map<String, PropertyManager> propertyManagers;

    private static class SingletonHolder {
        public static final YardiMockServer INSTANCE = new YardiMockServer();
    }

    public static YardiMockServer instance() {
        return SingletonHolder.INSTANCE;
    }

    private YardiMockServer() {
        propertyManagers = new HashMap<String, PropertyManager>();
    }

    public ResidentTransactions getAllResidentTransactions(String propertyId) {
        if (!propertyManagers.containsKey(propertyId)) {
            propertyManagers.put(propertyId, createProperty(propertyId));
        }
        return propertyManagers.get(propertyId).getAllResidentTransactions();
    }

    private PropertyManager createProperty(String propertyId) {
        PropertyManager propertyManager = new PropertyManager(propertyId);
        return propertyManager;
    }
}
