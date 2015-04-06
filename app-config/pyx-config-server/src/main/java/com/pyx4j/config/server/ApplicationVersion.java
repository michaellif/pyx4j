/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Dec 13, 2011
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

public class ApplicationVersion {

    private static final Logger log = LoggerFactory.getLogger(ApplicationVersion.class);

    private static final String BUILD_PROPERTIES_FILE = "generated/build.version.properties";

    private static final String PYX_BUILD_PROPERTIES_FILE = "com/pyx4j/config/generated/pyx-build.version.properties";

    private static final String PROJECT_VERSION = "project.version";

    private static final String POM_VERSION = "pom.version";

    private static final String BUILD_NUMBER = "build.number";

    private static final String PRODUCT_BUILD = "product.build";

    private static final String BUILD_TIME = "build.time";

    private static final String PATCH_NUMBER = "patch.number";

    private static final String BUILD_TIME_FORMAT = "yyyy-MM-dd HH:mm z";

    private static String productVersion = null;

    private static String patchNumber;

    private static String buildNumber;

    private static String productBuild = null;

    private static Date buildTimestamp;

    private static String scmRevision;

    private static String pyxProductVersion;

    private static String pyxBuildNumber;

    private static String pyxProductBuild;

    private static String pyxScmRevision;

    private static Date pyxBuildTimestamp;

    public static void initVersionInfo() {
        if (productVersion != null) {
            return;
        }
        Properties properties = new Properties();

        // find the resource in the classPath
        URL url = Thread.currentThread().getContextClassLoader().getResource(BUILD_PROPERTIES_FILE);
        if (url == null) {
            url = ApplicationVersion.class.getResource(BUILD_PROPERTIES_FILE);
        }

        if (url != null) {
            log.debug("Load build version from {}", url);
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

        patchNumber = properties.getProperty(PATCH_NUMBER, "");
        if (patchNumber.startsWith("${")) {
            patchNumber = "";
        }

        productBuild = properties.getProperty(PRODUCT_BUILD);
        if ((productBuild != null) && productBuild.startsWith("${")) {
            productBuild = productVersion + patchNumber + "." + buildNumber;
        }

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
        initPyxVersionInfo();
    }

    public static void initPyxVersionInfo() {
        Properties properties = new Properties();

        // find the resource in the classPath
        URL url = Thread.currentThread().getContextClassLoader().getResource(PYX_BUILD_PROPERTIES_FILE);
        if (url == null) {
            url = ApplicationVersion.class.getResource(PYX_BUILD_PROPERTIES_FILE);
        }

        if (url != null) {
            log.debug("Load pyx build version from {}", url);
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
        }

        pyxProductVersion = properties.getProperty(PROJECT_VERSION);
        if (pyxProductVersion == null) {
            pyxProductVersion = properties.getProperty(POM_VERSION);
        }
        if ((pyxProductVersion != null) && pyxProductVersion.endsWith("-SNAPSHOT")) {
            pyxProductVersion = pyxProductVersion.substring(0, pyxProductVersion.indexOf("-SNAPSHOT"));
        }

        pyxBuildNumber = properties.getProperty(BUILD_NUMBER, "n/a");
        if ((pyxBuildNumber != null) && pyxBuildNumber.startsWith("${") || buildNumber.endsWith("-SNAPSHOT")) {
            pyxBuildNumber = "n/a";
        }

        pyxProductBuild = properties.getProperty(PRODUCT_BUILD);
        if ((pyxProductBuild != null) && pyxProductBuild.startsWith("${")) {
            pyxProductBuild = pyxProductVersion + "." + pyxBuildNumber;
        }

        pyxScmRevision = properties.getProperty("scm.revision", "");
        try {
            String bildTimeString = properties.getProperty(BUILD_TIME);
            if ((bildTimeString == null) || (bildTimeString.startsWith("${"))) {
                pyxBuildTimestamp = null;
            } else {
                pyxBuildTimestamp = new SimpleDateFormat(BUILD_TIME_FORMAT).parse(bildTimeString);
            }
        } catch (ParseException e) {
            log.error("build timestamp error", e);
            pyxBuildTimestamp = null;
        }
    }

    public static String getProductVersion() {
        initVersionInfo();
        if (patchNumber != null) {
            return productVersion + patchNumber;
        } else {
            return productVersion;
        }
    }

    /**
     * Extract major part of version
     *
     * @param version
     *            String "1.2.3" or "1.2.3.4"
     * @return 1.2.3
     */
    public static String extractVersionMajor(String version) {
        String[] vparts = version.split("\\.");
        if (vparts.length > 3) {
            return vparts[0] + "." + vparts[1] + "." + vparts[2];
        } else {
            return version;
        }
    }

    public static String getProductVersionMajor() {
        initVersionInfo();
        return productVersion;
    }

    public static String getProductBuild() {
        initVersionInfo();
        return productBuild;
    }

    public static String getPatchNumber() {
        return patchNumber;
    }

    public static String getBuildLabel() {
        return getProductBuild();
    }

    public static String getBuildNumber() {
        initVersionInfo();
        return buildNumber;
    }

    public static String getBuildTime() {
        initVersionInfo();
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

    public static Date getBuildDate() {
        initVersionInfo();
        return buildTimestamp;
    }

    public static String getScmRevision() {
        initVersionInfo();
        return scmRevision;
    }

    public static String getPyxProductVersion() {
        initVersionInfo();
        return pyxProductVersion;
    }

    public static String getPyxProductBuild() {
        initVersionInfo();
        return pyxProductBuild;
    }

    public static String getPyxBuildLabel() {
        return getPyxProductBuild();
    }

    public static String getPyxScmRevision() {
        initVersionInfo();
        return pyxScmRevision;
    }

    public static Date getPyxBuildDate() {
        initVersionInfo();
        return pyxBuildTimestamp;
    }

    public static String getPyxBuildTime() {
        initVersionInfo();
        if (pyxBuildTimestamp != null) {
            DateFormat df;
            if (TimeUtils.isSameDay(buildTimestamp, new Date())) {
                df = new SimpleDateFormat("HH:mm");
            } else {
                df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            }
            return df.format(pyxBuildTimestamp);
        } else {
            return "";
        }
    }
}
