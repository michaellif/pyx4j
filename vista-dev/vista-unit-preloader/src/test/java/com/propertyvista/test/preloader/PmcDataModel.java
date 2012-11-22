/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.preloader;

import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.VistaNamespace;

public class PmcDataModel {

    public void generate() {
        if (VistaDeployment.getCurrentPmc() != null) {
            return;
        }

        final Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.namespace().setValue(NamespaceManager.getNamespace());

        pmc.features().occupancyModel().setValue(Boolean.TRUE);
        pmc.features().productCatalog().setValue(Boolean.TRUE);
        pmc.features().leases().setValue(Boolean.TRUE);

        pmc.status().setValue(PmcStatus.Active);

        NamespaceManager.runInTargetNamespace(VistaNamespace.adminNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(pmc);
                Persistence.service().commit();
                return null;
            }
        });

    }
}
