/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2011
 * @author vadym
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.unit.VistaBaseSeleniumTestCase;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.site.rpc.AppPlaceInfo;

public class PreloadedUsersTest extends VistaBaseSeleniumTestCase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.portal);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
    }

    public void testFullFlow() throws Exception {
        VistaDataGenerator generator = new VistaDataGenerator(DemoData.PT_GENERATION_SEED);
        User user = generator.createUser(1);
        Application application = generator.createApplication(user);
        Summary summary = generator.createSummary(application, null);
        //TODO UnitSelection unitSel = generator.createUnitSelection(application, null);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class)));

        doTestAptPage(summary);
        doTestTenantsPage(summary);
        doTestInfoPage(summary);

    }

    protected void doTestAptPage(Summary summary) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class));

        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(ApartmentUnit.class).unitType()));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, "leaseTerm_12"));

        selenium.click(CrudDebugId.Crud_Save);
    }

    protected void doTestTenantsPage(Summary summary) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class));

        //TODO validate summary/tenants

        selenium.click(CrudDebugId.Crud_Save);
    }

    protected void doTestInfoPage(Summary summary) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Info.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Info.class));

        PotentialTenantInfo tenant = summary.tenantList().tenants().get(0).detach();

        assertValueOnForm(tenant.firstName());
        assertValueOnForm(tenant.lastName());
        assertValueOnForm(tenant.middleName());
        assertValueOnForm(tenant.email());
        assertValueOnForm(tenant.homePhone());
        assertValueOnForm(tenant.mobilePhone());
        assertValueOnForm(tenant.driversLicense());
        assertValueOnForm(tenant.secureIdentifier());

        assertValueOnForm(tenant.notCanadianCitizen());

        assertValueOnForm(tenant.driversLicenseState());

        assertAddressForm(tenant.currentAddress().getPath(), detach(tenant.currentAddress()));
        assertAddressForm(tenant.previousAddress().getPath(), detach(tenant.previousAddress()));

        //Vehicles
        int num = 0;
        for (Vehicle vehicle : tenant.vehicles()) {
            assertVehiclesForm(D.id(tenant.vehicles(), num), detach(vehicle));
            num++;
        }
        //TODO Vadym, verify size (e.g. no next row exists)

        //Legal Questions
        assertValueOnForm(tenant.legalQuestions().everEvicted());
        assertValueOnForm(tenant.legalQuestions().defaultedOnLease());
        //TODO Add all...

        //Emergency Contacts
    }

    private void assertAddressForm(IDebugId fromDebugId, Address address) {
        assertValueOnForm(fromDebugId, address.street1());
        assertValueOnForm(fromDebugId, address.street2());
        assertValueOnForm(fromDebugId, address.city());
        assertValueOnForm(fromDebugId, address.phone());
        assertValueOnForm(fromDebugId, address.postalCode());
        assertValueOnForm(fromDebugId, address.moveInDate());
        assertValueOnForm(fromDebugId, address.moveOutDate());

        assertValueOnForm(fromDebugId, address.rented());
        assertValueOnForm(fromDebugId, address.country());
        assertValueOnForm(fromDebugId, address.province());

        if (OwnedRented.Owned == address.rented().getValue()) {
            assertNotVisible(D.id(fromDebugId, address.payment()));
            assertNotVisible(D.id(fromDebugId, address.managerName()));
        } else {
            assertVisible(D.id(fromDebugId, address.payment()));
            assertVisible(D.id(fromDebugId, address.managerName()));
            assertValueOnForm(fromDebugId, address.payment());
            assertValueOnForm(fromDebugId, address.managerName());
        }

    }

    private void assertVehiclesForm(IDebugId fromDebugId, Vehicle vehicle) {
        assertValueOnForm(fromDebugId, vehicle.year());

        assertValueOnForm(fromDebugId, vehicle.make());
        assertValueOnForm(fromDebugId, vehicle.model());
        assertValueOnForm(fromDebugId, vehicle.province());
        // TODO Vadym, Add all fields
    }

}
