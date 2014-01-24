/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 8, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.beans.Messages;

public class YardiLicense {

    private final static Logger log = LoggerFactory.getLogger(YardiLicense.class);

    static String[] errorMessage_License_Fragements = new String[] { "License invalid for Vendor", "Interface License expired" };

    private static boolean configLoaded = false;

    private final static Set<String> licenseLessSystemsUrl = new HashSet<String>();

    private static Map<YardiInterface, String> licenseBody = new HashMap<YardiInterface, String>();

    static {
        //licenseLessSystemsUrl.add("http://yardi.birchwoodsoftwaregroup.com/");
        // Starlight is old, can be disabled in file "config.properties"  yardi.licenseLessSystemsUrl 
        licenseLessSystemsUrl.add("https://yardi.starlightinvest.com/");
    }

    static String getInterfaceLicense(YardiInterface yardiInterface, PmcYardiCredential yc) {
        loadLicenseLessSystemsUrlConfiguration();

        if (isLicenseLessSystemUrl(yardiInterface, yc)) {
            return null;
        } else {
            if (!licenseBody.containsKey(yardiInterface)) {
                String body;

                String fileName = "yardi-license/YSIInstall-" + yardiInterface.name() + ".lic";
                File licenseFile = new File(ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getConfigDirectory(), fileName);
                log.debug("LicenseFile {} exists:{}", licenseFile.getAbsolutePath(), licenseFile.exists());
                if (licenseFile.canRead()) {
                    if (licenseFile.length() == 0) {
                        return null;
                    } else {
                        try {
                            body = FileUtils.readFileToString(licenseFile, StandardCharsets.US_ASCII);
                        } catch (IOException e) {
                            throw new Error("Failed to load yardi interface " + yardiInterface.name() + " license", e);
                        }
                    }
                } else {
                    try {
                        body = IOUtils.getTextResource(fileName);
                    } catch (IOException e) {
                        throw new Error("Failed to load yardi interface " + yardiInterface.name() + " license", e);
                    }
                    if (body == null) {
                        throw new Error("Yardi interface " + yardiInterface.name() + " license not found");
                    }
                }

                licenseBody.put(yardiInterface, body.replace("\n", ""));
            }
            return licenseBody.get(yardiInterface);
        }
    }

    public static void restLicenseCache() {
        configLoaded = false;
        licenseBody.clear();
    }

    private static boolean isLicenseLessSystemUrl(YardiInterface yardiInterface, PmcYardiCredential yc) {
        return isLicenseLessSystemUrl(yc.serviceURLBase().getValue()) //
                || isLicenseLessSystemUrl(yc.residentTransactionsServiceURL().getValue()) //
                || isLicenseLessSystemUrl(yc.sysBatchServiceURL().getValue());
    }

    private static boolean isLicenseLessSystemUrl(String url) {
        if (CommonsStringUtils.isEmpty(url)) {
            return false;
        }
        for (String urlBase : licenseLessSystemsUrl) {
            if (url.startsWith(urlBase)) {
                return true;
            }
        }
        return false;
    }

    private static void loadLicenseLessSystemsUrlConfiguration() {
        if (configLoaded) {
            return;
        }
        String configUrls = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getConfigProperties()
                .getValue("yardi.licenseLessSystemsUrl");
        if (configUrls != null) {
            synchronized (licenseLessSystemsUrl) {
                licenseLessSystemsUrl.clear();
                licenseLessSystemsUrl.addAll(Arrays.asList(configUrls.split(";")));
            }
        }
        configLoaded = true;
    }

    static void handleVendorLicenseError(Messages messages) {
        if (messages.hasErrorMessage(errorMessage_License_Fragements)) {
            restLicenseCache();
        }
    }

}
