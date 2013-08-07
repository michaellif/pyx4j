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
package com.propertyvista.common.client.config;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.shared.config.VistaFeatures.VistaFeaturesCustomization;

public class VistaFeaturesCustomizationClient implements VistaFeaturesCustomization {

    private static PmcVistaFeatures features = EntityFactory.create(PmcVistaFeatures.class);

    private static boolean isGoogleAnalyticDisableForEmployee;

    public static boolean enviromentTitleVisible;

    @Override
    public boolean occupancyModel() {
        return features.occupancyModel().getValue(Boolean.FALSE);
    }

    @Override
    public boolean productCatalog() {
        return features.productCatalog().getValue(Boolean.FALSE);
    }

    @Override
    public boolean leases() {
        return features.leases().getValue(Boolean.FALSE);
    }

    @Override
    public boolean onlineApplication() {
        return features.onlineApplication().getValue(Boolean.FALSE);
    }

    @Override
    public boolean defaultProductCatalog() {
        return features.defaultProductCatalog().getValue(Boolean.FALSE);
    }

    @Override
    public boolean yardiIntegration() {
        return features.yardiIntegration().getValue(Boolean.FALSE);
    }

    @Override
    public boolean yardiMaintenance() {
        return features.yardiMaintenance().getValue(Boolean.FALSE);
    }

    @Override
    public int yardiInterfaces() {
        return features.yardiInterfaces().getValue(0);
    }

    @Override
    public CountryOfOperation countryOfOperation() {
        return features.countryOfOperation().getValue();
    }

    @Override
    public boolean tenantSure() {
        return features.tenantSureIntegration().getValue(Boolean.FALSE);
    }

    public static void setVistaFeatures(PmcVistaFeatures features) {
        VistaFeaturesCustomizationClient.features = features;
    }

    public static boolean isGoogleAnalyticDisableForEmployee() {
        return isGoogleAnalyticDisableForEmployee;
    }

    public static void setGoogleAnalyticDisableForEmployee(boolean isGoogleAnalyticDisableForEmployee) {
        VistaFeaturesCustomizationClient.isGoogleAnalyticDisableForEmployee = isGoogleAnalyticDisableForEmployee;
    }

}
