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

import com.pyx4j.config.shared.ClientVersionMismatchError;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.shared.config.VistaFeatures.VistaFeaturesCustomization;

public class VistaFeatures {

    private static final ThreadLocal<PmcVistaFeatures> threadLocale = new ThreadLocal<PmcVistaFeatures>() {
        @Override
        protected PmcVistaFeatures initialValue() {
            //TODO This is wrong!  Move to UnitTests Mock
            Pmc pmc = VistaDeployment.getCurrentPmc();
            if (pmc != null) {

                // Fail safe on to terminate session on Feature changes
                Visit visit = Context.getVisit();
                if ((visit != null) && (visit.isUserLoggedIn())) {
                    Object hashCode = visit.getAttribute(PmcVistaFeatures.class.getName());
                    if (hashCode == null) {
                        visit.setAttribute(PmcVistaFeatures.class.getName(), Integer.valueOf(pmc.features().valueHashCode()));
                    } else if (!Integer.valueOf(pmc.features().valueHashCode()).equals(hashCode)) {
                        Lifecycle.endSession();
                        throw new ClientVersionMismatchError("Vista Features for your Company changed, please login again");
                    }
                }

                return pmc.features();
            } else {
                return EntityFactory.create(PmcVistaFeatures.class);
            }
        }
    };

    public static final class VistaFeaturesCustomizationImpl implements VistaFeaturesCustomization {

        public static PmcVistaFeatures getCurrentVistaFeatures() {
            PmcVistaFeatures curFeatures = threadLocale.get();
            // add calculated values
            if (curFeatures.yardiIntegration().isBooleanTrue()) {
                curFeatures.yardiInterfaces().setValue(VistaDeployment.getPmcYardiCredentials().size());
            }

            return curFeatures;
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
        public boolean onlineApplication() {
            return threadLocale.get().onlineApplication().getValue(Boolean.FALSE);
        }

        @Override
        public boolean defaultProductCatalog() {
            return threadLocale.get().defaultProductCatalog().getValue(Boolean.FALSE);
        }

        @Override
        public boolean yardiIntegration() {
            return threadLocale.get().yardiIntegration().getValue(Boolean.FALSE);
        }

        @Override
        public boolean yardiMaintenance() {
            return threadLocale.get().yardiMaintenance().getValue(Boolean.FALSE);
        }

        @Override
        public int yardiInterfaces() {
            return getCurrentVistaFeatures().yardiInterfaces().getValue(0);
        }

        @Override
        public CountryOfOperation countryOfOperation() {
            return threadLocale.get().countryOfOperation().getValue();
        }

        @Override
        public boolean tenantSure() {
            return threadLocale.get().tenantSureIntegration().getValue(Boolean.FALSE);
        }

    }

    public static void removeThreadLocale() {
        threadLocale.remove();
    }
}
