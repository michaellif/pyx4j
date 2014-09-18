/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 18/09/2014
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.yardi.entity.mits.Customerinfo;

import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.updater.CoTenantUpdateEvent;
import com.propertyvista.yardi.mock.updater.CoTenantUpdater;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;

@Category(FunctionalTests.class)
public class YardiImportIdsProcessingTest extends YardiTestBase {

    private final String propertyId = "Prop123";

    private final String unitId = "Unit #10";

    private final String tenantId = "T000111";

    private final String coTenantId = "R000222";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater(propertyId).
            set(PropertyUpdater.ADDRESS.Address1, "11 prop123 str").
            set(PropertyUpdater.ADDRESS.PostalCode, "A1B 2C3").
            set(PropertyUpdater.ADDRESS.State, "ON").
            set(PropertyUpdater.ADDRESS.Country, "Canada");        
            // @formatter:on
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        // Lease
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater(propertyId, tenantId).
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, tenantId).
            set(RtCustomerUpdater.YCUSTOMER.Description, "1").
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "John").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Smith").
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "John@Smith.ca").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1001.00")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2014-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).     
            set(RtCustomerUpdater.UNITINFO.UnitID, unitId).
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1001.00")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        // co-tenant:
        {
            // @formatter:off
            CoTenantUpdater updater = new CoTenantUpdater(propertyId, tenantId, coTenantId).
            set(CoTenantUpdater.YCUSTOMER.Type, Customerinfo.CUSTOMER).
            set(CoTenantUpdater.YCUSTOMER.CustomerID, coTenantId).
            set(CoTenantUpdater.YCUSTOMERNAME.FirstName, "Jane").
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Doe").
            set(CoTenantUpdater.YLEASE.ResponsibleForLease, true);
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

    }

    public void testImportIds() throws Exception {
        setSysDate("2010-11-01");

        // Initial Import 
        yardiImportAll(getYardiCredential(propertyId));

        Building building = getBuilding(propertyId);
        assertNull("Building " + propertyId, building);

        building = getBuilding(propertyId.toLowerCase());
        assertNotNull("Building " + propertyId.toLowerCase(), building);

        AptUnit unit = getUnit(building, unitId);
        assertNull("Unit " + unitId, unit);

        unit = getUnit(building, unitId.toUpperCase());
        assertNotNull("Unit  " + unitId.toUpperCase(), unit);

        Lease lease = getLeaseById(tenantId);
        assertNull("Lease " + tenantId, lease);

        lease = getLeaseById(tenantId.toLowerCase());
        assertNotNull("Lease " + tenantId.toLowerCase(), lease);

        // tenants:
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            switch (tenant.role().getValue()) {
            case Applicant:
                assertEquals("Tenant", tenantId.toLowerCase(), tenant.leaseParticipant().participantId().getValue());
                break;
            case CoApplicant:
                assertEquals("Co-Tenant", coTenantId.toLowerCase(), tenant.leaseParticipant().participantId().getValue());
                break;
            case Dependent:
                break;
            case Guarantor:
                break;
            default:
                break;
            }
        }
    }
}
