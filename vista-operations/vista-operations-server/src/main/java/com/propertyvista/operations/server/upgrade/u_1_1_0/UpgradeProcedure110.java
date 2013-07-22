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
package com.propertyvista.operations.server.upgrade.u_1_1_0;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.dashboard.GadgetStorageFacade;
import com.propertyvista.biz.financial.ar.ARArreasManagerUtils;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.operations.server.upgrade.UpgradeProcedure;
import com.propertyvista.portal.server.preloader.ReferenceDataPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.AutoPayChangePolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.YardiInterfacePolicyPreloader;
import com.propertyvista.server.common.gadgets.GadgetMetadataRepository;
import com.propertyvista.server.jobs.TaskRunner;

public class UpgradeProcedure110 implements UpgradeProcedure {

    private final static Logger log = LoggerFactory.getLogger(UpgradeProcedure110.class);

    @Override
    public int getUpgradeStepsCount() {
        return 6;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runDefaultProductCatalogGeneration();
            break;
        case 2:
            createInternalMaintenancePreloadInNotExists();
            break;
        case 3:
            runAutoPayChangePolicyGeneration();
            break;
        case 4:
            upgradeOldArrearsGadget();
            break;
        case 5:
            addYardiInterfacePolicy();
            break;
        case 6:
            removeRedundantArrearsSnapshots();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void runDefaultProductCatalogGeneration() {
        EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
        ICursorIterator<Building> cursor = Persistence.service().query(null, criteria, AttachLevel.Attached);
        boolean setDefaultCatalog = true;
        try {
            while (cursor.hasNext()) {
                Building building = cursor.next();
                Persistence.ensureRetrieve(building, AttachLevel.Attached);
                Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
                Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);
                Persistence.ensureRetrieve(building.productCatalog().features(), AttachLevel.Attached);
                if (building.productCatalog().services().size() > 0) {
                    setDefaultCatalog = false;
                    continue;
                }
                if (building.productCatalog().features().size() > 0) {
                    setDefaultCatalog = false;
                    continue;
                }
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
        if (setDefaultCatalog) {
            final Pmc pmc = VistaDeployment.getCurrentPmc();
            if (pmc.features().defaultProductCatalog().getValue(false)) {
                pmc.features().defaultProductCatalog().setValue(true);
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        Persistence.service().persist(pmc.features());
                        return null;
                    }
                });
                log.info("defaultProductCatalog enabled for PMC {}", pmc.dnsName().getValue());
            }
        }
    }

    private void addUnitsToDefaultCatalog(Building building) {
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.eq(criteria.proto().building(), building);

        for (AptUnit unit : Persistence.service().query(criteria)) {
            ServerSideFactory.create(DefaultProductCatalogFacade.class).addUnit(building, unit);
        }
    }

    private void runAutoPayChangePolicyGeneration() {
        log.info("Creating AutoPayChangePolicy and setting its scope to 'Organization'");
        AutoPayChangePolicyPreloader policyPreloader = new AutoPayChangePolicyPreloader();
        OrganizationPoliciesNode organizationNode = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        if (organizationNode == null) {
            throw new UserRuntimeException("Organizational Policy Was not found");
        }
        policyPreloader.setTopNode(organizationNode);
        String policyCreationLog = policyPreloader.create();
        log.info("Finished policy creation: " + policyCreationLog);

    }

    private void createInternalMaintenancePreloadInNotExists() {
        EntityQueryCriteria<MaintenanceRequestCategory> criteria = new EntityQueryCriteria<MaintenanceRequestCategory>(MaintenanceRequestCategory.class);
        if (Persistence.service().count(criteria) == 0) {
            new ReferenceDataPreloader().createInternalMaintenancePreload();
        }
    }

    private void upgradeOldArrearsGadget() {
        EntityQueryCriteria<GadgetMetadataHolder> criteria = EntityQueryCriteria.create(GadgetMetadataHolder.class);
        criteria.eq(criteria.proto().className(), ArrearsStatusGadgetMetadata.class.getSimpleName());
        List<GadgetMetadataHolder> gadgets = Persistence.service().query(criteria);

        for (GadgetMetadataHolder rawGadgetMetadata : gadgets) {
            ArrearsStatusGadgetMetadata oldGadgetMetadata = (ArrearsStatusGadgetMetadata) ServerSideFactory.create(GadgetStorageFacade.class).load(
                    rawGadgetMetadata.identifierKey().getValue());
            ArrearsSummaryGadgetMetadata upgradedGadgetMetadata = (ArrearsSummaryGadgetMetadata) GadgetMetadataRepository.get().createGadgetMetadata(
                    EntityFactory.getEntityPrototype(ArrearsSummaryGadgetMetadata.class));

            upgradedGadgetMetadata.gadgetId().setValue(rawGadgetMetadata.identifierKey().getValue());
            if (!oldGadgetMetadata.category().isNull()) {
                upgradedGadgetMetadata.customizeCategory().setValue(true);
                upgradedGadgetMetadata.category().setValue(oldGadgetMetadata.category().getValue());
            }
            if (oldGadgetMetadata.customizeDate().isBooleanTrue()) {
                upgradedGadgetMetadata.customizeDate().setValue(true);
                upgradedGadgetMetadata.asOf().setValue(oldGadgetMetadata.asOf().getValue());
            }
            ServerSideFactory.create(GadgetStorageFacade.class).delete(rawGadgetMetadata.identifierKey().getValue());
            ServerSideFactory.create(GadgetStorageFacade.class).save(upgradedGadgetMetadata, true);
        }
    }

    private void addYardiInterfacePolicy() {
        YardiInterfacePolicyPreloader policyPreloader = new YardiInterfacePolicyPreloader();
        OrganizationPoliciesNode organizationNode = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        if (organizationNode == null) {
            throw new UserRuntimeException("Organizational Policy Was not found");
        }
        policyPreloader.setTopNode(organizationNode);
        String policyCreationLog = policyPreloader.create();
        log.info("Added Yardi Interface Policy: " + policyCreationLog);
    }

    public static void removeRedundantArrearsSnapshots() {
        int totalRemovedCounter = 0;
        log.info("Removing redundant lease arrears snapshots");
        ICursorIterator<BillingAccount> billingAccountIterator = null;
        try {
            billingAccountIterator = Persistence.secureQuery(null, EntityQueryCriteria.create(BillingAccount.class), AttachLevel.IdOnly);
            while (billingAccountIterator.hasNext()) {
                BillingAccount billingAccount = billingAccountIterator.next();
                log.info("Removing redundant lease arrears snapshots for billing account" + billingAccount.getPrimaryKey().toString());
                EntityQueryCriteria<LeaseArrearsSnapshot> snapshotCriteria = EntityQueryCriteria.create(LeaseArrearsSnapshot.class);
                snapshotCriteria.eq(snapshotCriteria.proto().billingAccount(), billingAccount);
                snapshotCriteria.asc(snapshotCriteria.proto().fromDate());
                int removedCounter = removeRedundantArrearsSnapshots(Persistence.service().query(snapshotCriteria).iterator());
                totalRemovedCounter += removedCounter;
                log.info("Removed " + removedCounter + " redundant lease arrears snapshots for billing account" + billingAccount.getPrimaryKey().toString());
            }
        } finally {
            IOUtils.closeQuietly(billingAccountIterator);
        }

        log.info("Removing redundant building arrears snapshots");
        ICursorIterator<Building> buildingIterator = null;
        try {
            buildingIterator = Persistence.secureQuery(null, EntityQueryCriteria.create(Building.class), AttachLevel.ToStringMembers);
            while (buildingIterator.hasNext()) {
                Building building = buildingIterator.next();
                log.info("Removing redundant building arrears snapshots for building " + building.getStringView());
                EntityQueryCriteria<BuildingArrearsSnapshot> snapshotCriteria = EntityQueryCriteria.create(BuildingArrearsSnapshot.class);
                snapshotCriteria.eq(snapshotCriteria.proto().building(), building);
                snapshotCriteria.asc(snapshotCriteria.proto().fromDate());
                int counter = removeRedundantArrearsSnapshots(Persistence.service().query(snapshotCriteria).iterator());
                log.info("Removed " + counter + " redundant building arrears snapshots for building " + building.getStringView());
            }
        } finally {
            IOUtils.closeQuietly(buildingIterator);
        }

        log.info("Finished removing redundant arrears snapshots, total number of removed snapshots is: " + totalRemovedCounter);

    }

    private static int removeRedundantArrearsSnapshots(Iterator<? extends ArrearsSnapshot<?>> snapshots) {
        int removedCounter = 0;
        if (!snapshots.hasNext()) {
            return removedCounter;
        }
        ArrearsSnapshot<?> previousSnapshot = snapshots.next();
        boolean mergeSnapshots = false;
        while (snapshots.hasNext()) {
            ArrearsSnapshot<?> snapshot = snapshots.next();
            if (!ARArreasManagerUtils.haveDifferentBucketValues(previousSnapshot, snapshot)) {
                mergeSnapshots = true;
                previousSnapshot.toDate().setValue(snapshot.toDate().getValue());
                Persistence.service().delete(snapshot);
                removedCounter += 1;
            } else {
                if (mergeSnapshots) {
                    Persistence.service().persist(previousSnapshot);
                }
                mergeSnapshots = false;
                previousSnapshot = snapshot;
            }
        }
        if (mergeSnapshots) {
            Persistence.service().persist(previousSnapshot);
        }

        return removedCounter;
    }
}
