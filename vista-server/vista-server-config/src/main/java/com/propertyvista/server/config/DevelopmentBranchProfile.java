/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2015
 * @author vlads
 */
package com.propertyvista.server.config;

import java.io.File;
import java.util.Collections;

import com.pyx4j.config.server.PropertiesConfiguration;

public class DevelopmentBranchProfile {

    private PropertiesConfiguration configProperties;

    private DevelopmentBranchProfile() {
        File file = new File("..\\..", "development-branch.profile");
        if (file.canRead()) {
            configProperties = new PropertiesConfiguration(null, PropertiesConfiguration.loadProperties(file));
        } else {
            configProperties = new PropertiesConfiguration(Collections.<String, String> emptyMap());
        }
    }

    private static class SingletonHolder {
        public static final DevelopmentBranchProfile INSTANCE = new DevelopmentBranchProfile();
    }

    static DevelopmentBranchProfile instance() {
        return SingletonHolder.INSTANCE;
    }

    public static String dbNameOffset() {
        return instance().configProperties.getValue("dbNameOffset", "");
    }

    public static int jettyServerPortOffset() {
        return instance().configProperties.getIntegerValue("jettyServerPortOffset", 0);
    }
}
