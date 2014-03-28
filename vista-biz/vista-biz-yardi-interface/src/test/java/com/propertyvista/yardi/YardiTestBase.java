/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.AgreementLegalPolicyDataModel;
import com.propertyvista.test.mock.models.AutoPayPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.RestrictionsPolicyDataModel;
import com.propertyvista.test.mock.security.PasswordEncryptorFacadeMock;
import com.propertyvista.yardi.mock.YardiMockServer;
import com.propertyvista.yardi.mock.stub.YardiMockGuestManagementStubImpl;
import com.propertyvista.yardi.mock.stub.YardiMockILSGuestCardStubImpl;
import com.propertyvista.yardi.mock.stub.YardiMockMaintenanceRequestsStubImpl;
import com.propertyvista.yardi.mock.stub.YardiMockResidentTransactionsStubImpl;
import com.propertyvista.yardi.mock.stub.YardiMockSystemBatchesStubImpl;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;
import com.propertyvista.yardi.stubs.YardiGuestManagementStub;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiMaintenanceRequestsStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;
import com.propertyvista.yardi.stubs.YardiSystemBatchesStub;

public class YardiTestBase extends IntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(YardiTestBase.class);

    @Override
    protected void setUp() throws Exception {
        registerFacadeMock(YardiResidentTransactionsStub.class, YardiMockResidentTransactionsStubImpl.class);
        registerFacadeMock(YardiSystemBatchesStub.class, YardiMockSystemBatchesStubImpl.class);
        registerFacadeMock(YardiMaintenanceRequestsStub.class, YardiMockMaintenanceRequestsStubImpl.class);
        registerFacadeMock(YardiGuestManagementStub.class, YardiMockGuestManagementStubImpl.class);
        registerFacadeMock(YardiILSGuestCardStub.class, YardiMockILSGuestCardStubImpl.class);
        registerFacadeMock(PasswordEncryptorFacade.class, PasswordEncryptorFacadeMock.class);

        //Init YardiMockServer
        YardiMockServer.instance().cleanup();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected MockConfig createMockConfig() {
        MockConfig config = super.createMockConfig();
        config.yardiIntegration = true;
        return config;
    }

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(ARPolicyDataModel.class);
        models.add(AutoPayPolicyDataModel.class);
        models.add(AgreementLegalPolicyDataModel.class);
        models.add(RestrictionsPolicyDataModel.class);
        return models;
    }

    protected ExecutionMonitor yardiImportAll(PmcYardiCredential yardiCredential) throws RemoteException, YardiServiceException {
        return yardiImportAll(yardiCredential, 0, 0);
    }

    protected ExecutionMonitor yardiImportAll(PmcYardiCredential yardiCredential, int expectedErred, int expectedFailed) throws RemoteException,
            YardiServiceException {
        ExecutionMonitor executionMonitor = new ExecutionMonitor();
        YardiResidentTransactionsService.getInstance().updateAll(yardiCredential, executionMonitor);
        assertEquals("Import Erred", Long.valueOf(expectedErred), executionMonitor.getErred());
        assertEquals("Import Failed", Long.valueOf(expectedFailed), executionMonitor.getFailed());
        return executionMonitor;
    }

    protected Building getBuilding(String propertyCode) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), propertyCode));
        return Persistence.service().retrieve(criteria);
    }

    protected AptUnit getUnit(Building building, String unitId) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
        criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), unitId));
        return Persistence.service().retrieve(criteria);
    }

    protected Lease getCurrentLease(AptUnit unit) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.Active));
        return Persistence.service().retrieve(criteria);
    }

    protected Lease getLeaseById(String leaseId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), leaseId));
        Lease lease = Persistence.service().retrieve(criteria);
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        return lease;
    }

    protected PmcYardiCredential getYardiCredential(String propertyCode) {
        final String namespace = NamespaceManager.getNamespace();
        assert (!namespace.equals(VistaNamespace.operationsNamespace)) : "Function not available when running in operations namespace";
        try {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            EntityQueryCriteria<PmcYardiCredential> criteria = EntityQueryCriteria.create(PmcYardiCredential.class);
            criteria.like(criteria.proto().propertyListCodes(), "*" + propertyCode + "*");
            criteria.eq(criteria.proto().pmc().namespace(), namespace);
            PmcYardiCredential yc = Persistence.service().retrieve(criteria);
            if (yc == null) {
                yc = EntityFactory.create(PmcYardiCredential.class);
                yc.pmc().set(getDataModel(PmcDataModel.class).getItem(0));
                yc.propertyListCodes().setValue(propertyCode);
                Persistence.service().persist(yc);
                log.info("Created Yardi interface {} for Property codes {}", yc.getPrimaryKey(), propertyCode);
            }
            return yc;
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

}
