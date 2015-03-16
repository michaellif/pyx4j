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

import java.util.Set;

import com.pyx4j.security.shared.Behavior;

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

    resident,

    prospect,

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

    public static VistaApplication getVistaApplication(Set<Behavior> behaviours) {
        for (VistaApplication behaviour : VistaApplication.values()) {
            if (behaviours.contains(behaviour)) {
                return behaviour;
            }
        }
        return null;
    }
}