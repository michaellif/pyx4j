/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 27, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.yardi.YardiDebit;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.test.integration.InvoiceLineItemTester;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel.Code;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;
import com.propertyvista.yardi.mock.model.manager.YardiConfigurationManager;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockILSGuestCardStubImpl;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockResidentTransactionsStubImpl;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiLeaseApplicationTest extends YardiTestBase {

    final String BuildingID = YardiBuildingManager.DEFAULT_PROPERTY_CODE;

    final String UnitID = YardiBuildingManager.DEFAULT_UNIT_NO;

    final String TenantID = "t200";

    final String CoTenantID = "r201";

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = super.getMockModelTypes();
        models.add(CustomerDataModel.class);
        models.add(LeaseDataModel.class);
        return models;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        YardiMock.server().reset();
        // managers
        YardiMock.server().addManager(YardiBuildingManager.class);
        YardiMock.server().addManager(YardiLeaseManager.class);
        YardiMock.server().addManager(YardiGuestManager.class);
        YardiMock.server().addManager(YardiConfigurationManager.class);
        // stubs
        YardiMock.server().addStub(YardiResidentTransactionsStub.class, YardiMockResidentTransactionsStubImpl.class);
        YardiMock.server().addStub(YardiILSGuestCardStub.class, YardiMockILSGuestCardStubImpl.class);
    }

    /*
     * - Create yardi building with a unit
     * - Do import, check product catalog, add deposits for service and features
     * - Create and approve Lease Application
     * - Do import, check lease, ensure deposit charges
     */
    public void testLeaseApplication() throws Exception {
        // 1. Setup
        // --------
        final String lmrChargeCode = "rlmr";

        setSysDate("25-May-2013");

        YardiMock.server().getManager(YardiBuildingManager.class).addDefaultBuilding();

        // 2. Execution: first import
        // -------------------------------
        yardiImportAll(getYardiCredential(BuildingID));

        // 3. Assertion: building, unit, product catalog
        // ---------------------------------------------
        Building building = getBuilding(BuildingID);
        assertNotNull(building);

        AptUnit unit = getUnit(building, UnitID);
        assertNotNull(unit);

        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        ProductCatalog catalog = building.productCatalog();
        assertEquals(1, catalog.services().size());

        // 4. Execution: enable LMR
        // ------------------------
        Persistence.ensureRetrieve(catalog.services(), AttachLevel.Attached);
        catalog.services().get(0).version().depositLMR().enabled().setValue(true);
        Persistence.service().persist(catalog.services().get(0));
        // configure yardi deposit code
        ARCode deposit = getDataModel(ARCodeDataModel.class).getARCode(Code.depositLMR);
        getDataModel(ARCodeDataModel.class).addYardiCode(deposit, lmrChargeCode);
        // in yardi
        YardiMock.server().getManager(YardiConfigurationManager.class).addChargeCode(YardiILSGuestCardStub.class, lmrChargeCode);

        // 5. Execution: create and approve lease application
        // --------------------------------------------------
        // create tenant and co-tenant
        getDataModel(CustomerDataModel.class).addCustomer();
        getDataModel(CustomerDataModel.class).addCustomer();
        // create lease application
        List<Customer> customers = getDataModel(CustomerDataModel.class).getAllItems();
        Persistence.ensureRetrieve(catalog.services().get(0).version().items(), AttachLevel.Attached);
        ProductItem serviceItem = catalog.services().get(0).version().items().get(0);
        Lease lease = getDataModel(LeaseDataModel.class).addLease(building, "01-Jun-2012", "31-Jul-2014", null, null, customers, serviceItem);
        // assert application status
        assertNotNull(lease);
        assertEquals(Lease.Status.Application, lease.status().getValue());

        // approve lease application
        ServerSideFactory.create(LeaseFacade.class).approve(lease, null, null);
        Persistence.service().commit();

        // do second import
        yardiImportAll(getYardiCredential(BuildingID));

        // 6. Assertion: status approved, participantId have been set
        lease = ServerSideFactory.create(LeaseFacade.class).load(lease, false);

        // assert application status
        assertEquals(Lease.Status.Approved, lease.status().getValue());
        // assert participants
        assertEquals(customers.size(), lease.currentTerm().version().tenants().size());
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            LeaseTermTenant tenant = lease.currentTerm().version().tenants().get(i);
            assertEquals(customer.getPrimaryKey(), tenant.leaseParticipant().customer().getPrimaryKey());
            assertTrue(tenant.leaseParticipant().participantId().getValue().matches("^[rt].*"));
        }

        // assert deposit charges: ensure one charge equals to rent amount
        new InvoiceLineItemTester(lease) //
                .count(YardiDebit.class, 1) //
                .lastRecordAmount(YardiDebit.class, YardiBuildingManager.DEFAULT_UNIT_RENT);
    }
}
