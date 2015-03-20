/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 19, 2015
 * @author vlads
 */
package com.propertyvista.config.deployment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.propertyvista.domain.security.common.VistaApplication;

public class VistaApplicationDeploymentMap {

    private final Map<String, List<VistaApplication>> applicationsByDnsNameFragment;

    private final Map<String, VistaApplication> applicationsBySubApplicationPath;

    private static class SingletonHolder {
        public static final VistaApplicationDeploymentMap INSTANCE = new VistaApplicationDeploymentMap();
    }

    public static VistaApplicationDeploymentMap instance() {
        return SingletonHolder.INSTANCE;
    }

    private VistaApplicationDeploymentMap() {
        applicationsByDnsNameFragment = new HashMap<>();
        applicationsBySubApplicationPath = new HashMap<>();
        for (VistaApplication a : EnumSet.allOf(VistaApplication.class)) {
            List<VistaApplication> applications = applicationsByDnsNameFragment.get(a.getDnsNameFragment());
            if (applications == null) {
                applications = new ArrayList<>();
            }
            applications.add(a);
            applicationsByDnsNameFragment.put(a.getDnsNameFragment(), applications);

            if (a.getSubApplicationPath() != null) {
                applicationsBySubApplicationPath.put(a.getDnsNameFragment() + "/" + a.getSubApplicationPath(), a);
            }
        }
    }

    public static VistaApplication getVistaApplicationByDnsNameFragment(String dnsNameFragment) {
        List<VistaApplication> applications = instance().applicationsByDnsNameFragment.get(dnsNameFragment);
        if (applications != null) {
            return applications.get(0);
        } else {
            return null;
        }
    }

    public static List<VistaApplication> getSubApplications(VistaApplication application) {
        return instance().applicationsByDnsNameFragment.get(application.getDnsNameFragment());
    }

    public static VistaApplication getVistaApplicationByDnsNameFragment(String dnsNameFragment, HttpServletRequest httpRequest) {
        VistaApplication app = instance().applicationsBySubApplicationPath.get(dnsNameFragment + "/" + HttpRequestUtils.getRootServletPath(httpRequest));
        if (app != null) {
            return app;
        } else {
            return getVistaApplicationByDnsNameFragment(dnsNameFragment);
        }
    }

    public static VistaApplication getVistaSubApplication(VistaApplication defaultApplication, HttpServletRequest httpRequest) {
        VistaApplication app = instance().applicationsBySubApplicationPath.get(defaultApplication.getDnsNameFragment() + "/"
                + HttpRequestUtils.getRootServletPath(httpRequest));
        if (app != null) {
            return app;
        } else {
            return defaultApplication;
        }
    }
}
