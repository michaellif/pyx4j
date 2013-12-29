/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.server.upgrade.u_1_0_6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.operations.server.upgrade.UpgradeProcedure;
import com.propertyvista.operations.server.upgrade.u_1_0_5.UpgradeProcedure105;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.TenantInsurancePolicyPreloader;

public class UpgradeProcedure106 implements UpgradeProcedure {

    private final static Logger log = LoggerFactory.getLogger(UpgradeProcedure105.class);

    @Override
    public int getUpgradeStepsCount() {
        return 2;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runDefaultProductCatalogGeneration();
            break;
        case 2:
            runTenantInsurancePolicyGeneration();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void runDefaultProductCatalogGeneration() {
        EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
        ICursorIterator<Building> cursor = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (cursor.hasNext()) {
                Building building = cursor.next();
                try {
                    ServerSideFactory.create(DefaultProductCatalogFacade.class).createFor(building);
                    ServerSideFactory.create(DefaultProductCatalogFacade.class).persistFor(building);
                    addUnitsToDefaultCatalog(building);
                } catch (Throwable e) {
                    log.error("Error migrating building {}", building, e);
                    throw new UserRuntimeException("Error in building " + NamespaceManager.getNamespace() + "." + building.getPrimaryKey() + "; "
                            + e.getClass() + " " + e.getMessage());
                }
            }
        } finally {
            cursor.close();
        }
    }

    private void addUnitsToDefaultCatalog(Building building) {
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.eq(criteria.proto().building(), building);

        for (AptUnit unit : Persistence.service().query(criteria)) {
            ServerSideFactory.create(DefaultProductCatalogFacade.class).addUnit(building, unit);
        }
    }

    private void runTenantInsurancePolicyGeneration() {
        log.info("Creating TenantInsurancePolicy and setting its scope to 'Organization'");
        TenantInsurancePolicyPreloader policyPreloader = new TenantInsurancePolicyPreloader();
        OrganizationPoliciesNode organizationNode = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        if (organizationNode == null) {
            throw new UserRuntimeException("Organizational Policy Was not found");
        }
        policyPreloader.setTopNode(organizationNode);
        String policyCreationLog = policyPreloader.create();
        log.info("Finished policy creation: " + policyCreationLog);
    }
}
