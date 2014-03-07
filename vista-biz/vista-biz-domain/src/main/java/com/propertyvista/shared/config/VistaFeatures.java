/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.shared.config;

import com.google.gwt.core.client.GWT;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.domain.customizations.CountryOfOperation;

public class VistaFeatures {

    public static interface VistaFeaturesCustomization {

        boolean tenantSure();

        boolean leases();

        boolean onlineApplication();

        CountryOfOperation countryOfOperation();

        boolean yardiIntegration();

        boolean yardiMaintenance();

        int yardiInterfaces();
    }

    private final static VistaFeaturesCustomization impl;

    static {
        if (ApplicationMode.hasGWT()) {
            if (GWT.isClient()) {
                impl = GWT.create(VistaFeaturesCustomization.class);
            } else {
                impl = ServerSideFactory.create(VistaFeaturesCustomization.class);
            }
        } else {
            impl = ServerSideFactory.create(VistaFeaturesCustomization.class);
        }
    }

    public static final VistaFeaturesCustomization instance() {
        return impl;
    }

}
