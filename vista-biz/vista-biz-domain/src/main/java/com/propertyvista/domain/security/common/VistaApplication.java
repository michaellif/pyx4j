/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 28, 2012
 * @author Artyom
 */
package com.propertyvista.domain.security.common;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pyx4j.security.shared.Behavior;

import com.propertyvista.domain.VistaNamespace;

/**
 * No not assign this Behavior to any permissions.
 * This is used for Application identification and namespace integrity validations.
 */
public enum VistaApplication implements Behavior {

    interfaces,

    staticContext("static"),

    // CI specific URs and
    env,

    @Deprecated
    noApp,

    operations,

    crm,

    site,

    resident("portal"),

    prospect("portal"),

    onboarding("start");

    private final String dnsNameFragment;

    VistaApplication() {
        this(null);
    }

    VistaApplication(String dnsNameFragment) {
        if (dnsNameFragment == null) {
            this.dnsNameFragment = name();
        } else {
            this.dnsNameFragment = dnsNameFragment;
        }
    }

    public String getDnsNameFragment() {
        return dnsNameFragment;
    }

    public String getInternalMappingName() {
        if (this == staticContext) {
            return "static";
        } else {
            return name();
        }
    }

    private static final Collection<VistaApplication> pmcApplications = EnumSet.of(crm, site, resident, prospect);

    public boolean requirePmcResolution() {
        return pmcApplications.contains(this);
    }

    private static final Collection<VistaApplication> applicationsNeedHttps = EnumSet.of(operations, onboarding, crm, resident, prospect);

    public boolean requireHttps() {
        return applicationsNeedHttps.contains(this);
    }

    public String getFixedNamespace() {
        switch (this) {
        case operations:
            return VistaNamespace.operationsNamespace;
        case env:
        case interfaces:
        case onboarding:
        case staticContext:
            return VistaNamespace.noNamespace;
        default:
            return null;
        }
    }

    private static final Map<String, VistaApplication> applicationsByDnsNameFragment = createApplicationsByDnsNameFragment();

    private static Map<String, VistaApplication> createApplicationsByDnsNameFragment() {
        Map<String, VistaApplication> m = new HashMap<>();
        for (VistaApplication a : EnumSet.allOf(VistaApplication.class)) {
            if (!m.containsKey(a.getDnsNameFragment())) {
                m.put(a.getDnsNameFragment(), a);
            }
        }
        return m;
    }

    public static VistaApplication getVistaApplicationByDnsNameFragment(String dnsNameFragment) {
        return applicationsByDnsNameFragment.get(dnsNameFragment);
    }

    public static VistaApplication getVistaApplication(Set<Behavior> behaviours) {
        for (VistaApplication behaviour : VistaApplication.values()) {
            if (behaviours.contains(behaviour)) {
                return behaviour;
            }
        }
        return null;
    }

}