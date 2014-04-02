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
package com.propertyvista.test.mock.models;

import java.util.concurrent.Callable;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.unit.shared.UniqueLong;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.server.config.DevYardiCredentials;
import com.propertyvista.test.mock.MockDataModel;

public class PmcDataModel extends MockDataModel<Pmc> {

    private OrganizationPoliciesNode orgNode;

    public PmcDataModel() {
    }

    private static String namespaceBase = String.valueOf(System.currentTimeMillis());

    public static String uniqueNamespaceId() {
        return "t" + UniqueLong.getInstance("Pmc").nextAsString() + "_" + namespaceBase;
    }

    @Override
    protected void generate() {
        if (VistaDeployment.getCurrentPmc() != null) {
            return;
        }

        if (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType() == DatabaseType.PostgreSQL) {
            String namespace = NamespaceManager.getNamespace();
            try {
                NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
                RDBUtils.ensureNamespace();
            } finally {
                NamespaceManager.setNamespace(namespace);
            }
            NamespaceManager.setNamespace(NamespaceManager.getNamespace());
            RDBUtils.ensureNamespace();
            RDBUtils.initAllEntityTables();
        }

        final Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.namespace().setValue(NamespaceManager.getNamespace());
        pmc.dnsName().setValue(pmc.namespace().getValue());

        pmc.features().countryOfOperation().setValue(CountryOfOperation.Canada);

        pmc.status().setValue(PmcStatus.Active);

        if (getConfig().yardiIntegration) {
            pmc.yardiCredentials().add(DevYardiCredentials.getTestPmcYardiCredential());
            pmc.features().yardiIntegration().setValue(Boolean.TRUE);
        }

        orgNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().persist(orgNode);

        NamespaceManager.runInTargetNamespace(VistaNamespace.operationsNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(pmc);
                return null;
            }
        });

        addItem(pmc);
    }

    public OrganizationPoliciesNode getOrgNode() {
        return orgNode;
    }
}
