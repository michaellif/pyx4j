/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Dec 11, 2015
 * @author vlads
 */
package com.pyx4j.config.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;

public class BuildVersion {

    private static final Logger log = LoggerFactory.getLogger(BuildVersion.class);

    private static final String PROJECT_VERSION = "project.version";

    private static final String POM_VERSION = "pom.version";

    private static final String BUILD_NUMBER = "build.number";

    private static final String PRODUCT_BUILD = "product.build";

    private static final String BUILD_TIME = "build.time";

    private static final String PATCH_NUMBER = "patch.number";

    private static final String BUILD_TIME_FORMAT = "yyyy-MM-dd HH:mm z";

    private String productVersion;

    private String patchNumber;

    private String buildNumber;

    private String productBuild = null;

    private Date buildTimestamp;

    private String scmRevision;

    public String getProductVersion() {
        return productVersion;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public String getPatchNumber() {
        return patchNumber;
    }

    public String getProductBuild() {
        return productBuild;
    }

    public String getScmRevision() {
        return scmRevision;
    }

    public Date getPyxBuildDate() {
        return buildTimestamp;
    }

    public String getBuildTime() {
        if (buildTimestamp != null) {
            DateFormat df;
            if (TimeUtils.isSameDay(buildTimestamp, new Date())) {
                df = new SimpleDateFormat("HH:mm");
            } else {
                df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            }
            return df.format(buildTimestamp);
        } else {
            return "";
        }
    }

    public void loadVersionInfo(URL url) {
        Properties properties = new Properties();

        InputStream is = null;
        try {
            is = url.openStream();
            properties.load(is);
        } catch (IOException e) {
            log.error("error reading build info", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                    is = null;
                }
            }
        }

        buildNumber = properties.getProperty(BUILD_NUMBER, "n/a");
        if (buildNumber.startsWith("${")) {
            buildNumber = "n/a";
        }

        productVersion = properties.getProperty(PROJECT_VERSION);
        if (productVersion == null) {
            productVersion = properties.getProperty(POM_VERSION);
        }
        if ((productVersion != null) && productVersion.endsWith("-SNAPSHOT")) {
            productVersion = productVersion.substring(0, productVersion.indexOf("-SNAPSHOT"));
        }

        productBuild = properties.getProperty(PRODUCT_BUILD);
        if ((productBuild != null) && productBuild.startsWith("${")) {
            productBuild = productVersion + "." + buildNumber;
        }

        patchNumber = properties.getProperty(PATCH_NUMBER);
        scmRevision = properties.getProperty("scm.revision", "");
        try {
            String bildTimeString = properties.getProperty(BUILD_TIME);
            if ((bildTimeString == null) || (bildTimeString.startsWith("${"))) {
                buildTimestamp = null;
            } else {
                buildTimestamp = new SimpleDateFormat(BUILD_TIME_FORMAT).parse(bildTimeString);
            }
        } catch (ParseException e) {
            log.error("build timestamp error", e);
            buildTimestamp = null;
        }
    }

}