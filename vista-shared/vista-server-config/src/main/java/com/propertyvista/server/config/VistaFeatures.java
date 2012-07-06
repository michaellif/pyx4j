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
package com.propertyvista.server.config;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.shared.config.VistaFeatures.VistaFeaturesCustomization;

public class VistaFeatures {

    private static final ThreadLocal<PmcVistaFeatures> threadLocale = new ThreadLocal<PmcVistaFeatures>() {
        @Override
        protected PmcVistaFeatures initialValue() {
            return VistaDeployment.getCurrentPmc().features();
        }
    };

    public static final class VistaFeaturesCustomizationImpl implements VistaFeaturesCustomization {

        public static PmcVistaFeatures getCurrentVistaFeatures() {
            return threadLocale.get();
        }

        @Override
        public boolean occupancyModel() {
            return threadLocale.get().occupancyModel().getValue(Boolean.FALSE);
        }

        @Override
        public boolean productCatalog() {
            return threadLocale.get().productCatalog().getValue(Boolean.FALSE);
        }

        @Override
        public boolean leases() {
            return threadLocale.get().leases().getValue(Boolean.FALSE);
        }

        @Override
        public boolean xmlSiteExport() {
            return threadLocale.get().xmlSiteExport().getValue(Boolean.FALSE);
        }

    }

    static void removeThreadLocale() {
        threadLocale.remove();
    }
}
