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

import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.shared.config.VistaFeatures.VistaFeaturesCustomization;

public class VistaFeaturesCustomizationClient implements VistaFeaturesCustomization {

    private static PmcVistaFeatures features = EntityFactory.create(PmcVistaFeatures.class);

    @Override
    public boolean occupancyModel() {
        return features.occupancyModel().getValue(Boolean.FALSE);
    }

    @Override
    public boolean productCatalog() {
        return features.productCatalog().getValue(Boolean.FALSE);
    }

    @Override
    public boolean xmlSiteExport() {
        return features.xmlSiteExport().getValue(Boolean.FALSE);
    }

    public static void setVistaFeatures(PmcVistaFeatures features) {
        VistaFeaturesCustomizationClient.features = features;
    }

}
