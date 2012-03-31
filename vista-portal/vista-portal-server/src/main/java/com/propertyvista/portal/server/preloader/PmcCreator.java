/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 23, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.misc.VistaDataPreloaderParameter;

public class PmcCreator {

    private final static Logger log = LoggerFactory.getLogger(PmcCreator.class);

    public static void preloadPmc(PmcDTO pmc) {
        final String namespace = NamespaceManager.getNamespace();
        NamespaceManager.setNamespace(pmc.dnsName().getValue());
        try {

            AbstractDataPreloader preloader = VistaDataPreloaders.productionPmcPreloaders();
            preloader.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());
            log.info("Preload {}", preloader.create());

            CrmRole defaultRole = CrmRolesPreloader.getDefaultRole();
            UserPreloader.createCrmEmployee(pmc.person().name().firstName().getValue(), pmc.person().name().lastName().getValue(), pmc.email().getValue(), pmc
                    .password().getValue(), true, defaultRole);

            // Create support account by default
            createVistaSupportUsers();

            if (ApplicationMode.isDevelopment()) {
                for (int i = 1; i <= DemoData.UserType.PM.getDefaultMax(); i++) {
                    String email = DemoData.UserType.PM.getEmail(i);
                    UserPreloader.createCrmEmployee(email, email, email, email, false, defaultRole);
                }
            }
            Persistence.service().commit();
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    public static void createVistaSupportUsers() {
        UserPreloader.createCrmEmployee("Support", "PropertyVista", "support@propertyvista.com", "Vista2012", false, CrmRolesPreloader.getDefaultRole(),
                CrmRolesPreloader.getSupportRole());
    }

}
