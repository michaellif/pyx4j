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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.BusinessRules;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.pt.services.ApplicationServiceImpl;

public class PreloadedUsersTest extends WizardBaseSeleniumTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // This is just to make the test more visual
        selenium.setFocusOnGetValue(true);

        SharedData.init();
    }

    public void testFullFlow() throws Exception {
        VistaDataGenerator generator = new VistaDataGenerator(DemoData.PT_GENERATION_SEED);
        User user = generator.createUser(1);
        Application application = generator.createApplication(user);
        Summary summary = generator.createSummary(application, null);
        UnitSelection unitSel = generator.createUnitSelection(application, null);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class)));

        doTestAptPage(unitSel);
        doTestTenantsPage(summary);
        doTestInfoPages(summary);

    }

    private void assertAptUnitForm(IDebugId fromDebugId, ApartmentUnit aUnit) {
        assertValueOnForm(fromDebugId, aUnit.unitType());
        assertValueOnForm(fromDebugId, aUnit.marketRent().get(aUnit.marketRent().size() - 1));
        assertValueOnForm(fromDebugId, aUnit.requiredDeposit());
        assertValueOnForm(fromDebugId, aUnit.bedrooms());
        assertValueOnForm(fromDebugId, aUnit.bathrooms());
        assertValueOnForm(fromDebugId, aUnit.area());
        assertValueOnForm(fromDebugId, aUnit.avalableForRent());
    }

    protected void doTestAptPage(UnitSelection unitSel) {
        assertVisible(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Apartment.class));
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Apartment.class));

        //verify all of them
        int num = 0;
        //TODO VLAD:: why this always returns empty list? How to do that properly? 
        int size2 = unitSel.availableUnits().units().size();
        for (ApartmentUnit aUnit : unitSel.availableUnits().units()) {
            assertAptUnitForm(D.id(unitSel.availableUnits().units(), num), detach(aUnit));
            num++;
        }

        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(ApartmentUnit.class).unitType()));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, "leaseTerm_12"));
        String avlDate = selenium.getText(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(ApartmentUnit.class).avalableForRent()));
        selenium.setValue(D.id(proto(UnitSelection.class).rentStart()), avlDate);
        saveAndContinue();
    }

    protected void doTestTenantsPage(Summary summary) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class));

        //TODO validate summary/tenants
        saveAndContinue();
    }

    protected void doTestInfoPages(Summary summary) {
        int id = 0;
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                doTestInfoPage(detach(tenant), id);
                saveAndContinue();
                id++;
            }
        }
        // Asset no next page
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Info.class));
        assertNotPresent(D.id(VistaFormsDebugId.SecondNavigation_Prefix, SiteMap.Info.class, id));
    }

    protected void doTestInfoPage(PotentialTenantInfo tenant, int id) {
        assertVisible(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Info.class));
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Info.class));
        selenium.click(D.id(VistaFormsDebugId.SecondNavigation_Prefix, SiteMap.Info.class, id));

        assertValueOnForm(tenant.firstName());
        assertValueOnForm(tenant.lastName());
        assertValueOnForm(tenant.middleName());
        assertValueOnForm(tenant.email());
        assertValueOnForm(tenant.homePhone());
        assertValueOnForm(tenant.mobilePhone());
        assertValueOnForm(tenant.workPhone());
        assertValueOnForm(tenant.driversLicenseState());
        assertValueOnForm(tenant.driversLicense());
        assertValueOnForm(tenant.secureIdentifier());
        assertValueOnForm(tenant.notCanadianCitizen());

        assertAddressForm(tenant.currentAddress().getPath(), detach(tenant.currentAddress()));
        if (BusinessRules.infoPageNeedPreviousAddress(tenant.currentAddress().moveInDate().getValue())) {
            assertAddressForm(tenant.previousAddress().getPath(), detach(tenant.previousAddress()));
        } else {
            assertNotVisible(D.id(tenant.previousAddress().getPath(), detach(tenant.previousAddress()).street1()));
        }

        //Vehicles
        int num = 0;
        for (Vehicle vehicle : tenant.vehicles()) {
            assertVehiclesForm(D.id(tenant.vehicles(), num), detach(vehicle));
            num++;
        }
        //verify size (e.g. no next row exists)
        assertFalse(selenium.isElementPresent(D.id(proto(PotentialTenantInfo.class).vehicles(), num, proto(Vehicle.class).plateNumber())));

        //Legal Questions
        assertValueOnForm(tenant.legalQuestions().suedForRent());
        assertValueOnForm(tenant.legalQuestions().suedForDamages());
        assertValueOnForm(tenant.legalQuestions().everEvicted());
        assertValueOnForm(tenant.legalQuestions().defaultedOnLease());
        assertValueOnForm(tenant.legalQuestions().convictedOfFelony());
        assertValueOnForm(tenant.legalQuestions().legalTroubles());
        assertValueOnForm(tenant.legalQuestions().filedBankruptcy());

        //Emergency Contacts
        num = 0;
        for (EmergencyContact contact : tenant.emergencyContacts()) {
            assertEmContactsForm(D.id(tenant.emergencyContacts(), num), detach(contact));
            num++;
        }
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
        assertValueOnForm(fromDebugId, vehicle.plateNumber());
        assertValueOnForm(fromDebugId, vehicle.year());
        assertValueOnForm(fromDebugId, vehicle.make());
        assertValueOnForm(fromDebugId, vehicle.model());
        assertValueOnForm(fromDebugId, vehicle.country());
        assertValueOnForm(fromDebugId, vehicle.province());
    }

    private void assertEmContactsForm(IDebugId fromDebugId, EmergencyContact contact) {
        assertValueOnForm(fromDebugId, contact.firstName());
        assertValueOnForm(fromDebugId, contact.middleName());
        assertValueOnForm(fromDebugId, contact.lastName());
        assertValueOnForm(fromDebugId, contact.homePhone());
        assertValueOnForm(fromDebugId, contact.mobilePhone());
        assertValueOnForm(fromDebugId, contact.workPhone());
        assertValueOnForm(fromDebugId, contact.address().street1());
        assertValueOnForm(fromDebugId, contact.address().street2());
        assertValueOnForm(fromDebugId, contact.address().city());
        assertValueOnForm(fromDebugId, contact.address().province());
        assertValueOnForm(fromDebugId, contact.address().country());
        assertValueOnForm(fromDebugId, contact.address().postalCode());
    }

}
