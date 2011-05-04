/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.selenium.D;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.BusinessRules;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.pt.services.ApplicationServiceImpl;

public class CreateCompleteApplicationTest extends WizardBaseSeleniumTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
    }

    public void testFullFlow() throws Exception {
        VistaDataGenerator generator = new VistaDataGenerator(DemoData.PT_GENERATION_SEED);
        User user = createTestUser();
        Application application = generator.createApplication(user);
        Summary summary = generator.createSummary(application, null);

        createAccount(user);
        enterUnitSelection();
        enterTenantsPage(summary);
        //TODO
        //enterTestInfoPages(summary);
    }

    private void createAccount(User user) {
        selenium.type(proto(AccountCreationRequest.class).email(), user.email().getValue());
        selenium.type(proto(AccountCreationRequest.class).password(), user.email().getValue());
        selenium.type("id=recaptcha_response_field", "x");
        selenium.click(VistaFormsDebugId.Auth_LetsStart);
    }

    private void enterUnitSelection() {
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Apartment.class));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 0, proto(ApartmentUnit.class).unitType()));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 0, "leaseTerm_12"));
        saveAndContinue();
    }

    private void enterTenantsPage(Summary summary) {
        int num = 0;
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            if (num != 0) {
                selenium.click(D.id(proto(PotentialTenantList.class).tenants(), FormNavigationDebugId.Form_Add));
            }
            enterTenant(D.id(proto(PotentialTenantList.class).tenants(), num), detach(tenant), (num != 0));
            num++;
        }
        saveAndContinue();
    }

    private void enterTenant(IDebugId fromDebugId, PotentialTenantInfo tenant, boolean fullInfo) {
        setValueOnForm(fromDebugId, tenant.firstName());
        setValueOnForm(fromDebugId, tenant.lastName());
        setValueOnForm(fromDebugId, tenant.middleName());
        setValueOnForm(fromDebugId, tenant.birthDate());

        if (fullInfo) {
            setValueOnForm(fromDebugId, tenant.email());
            setValueOnForm(fromDebugId, tenant.relationship());
            setValueOnForm(fromDebugId, tenant.status());
            setValueOnForm(fromDebugId, tenant.takeOwnership());
        }
    }

    private void enterTestInfoPages(Summary summary) {
        int id = 0;
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                enterTestInfo(detach(tenant));
                saveAndContinue();
                id++;
            }
        }
    }

    private void enterTestInfo(PotentialTenantInfo tenant) {
        setValueOnForm(tenant.firstName());
        setValueOnForm(tenant.lastName());
        setValueOnForm(tenant.middleName());
        setValueOnForm(tenant.email());
        setValueOnForm(tenant.homePhone());
        setValueOnForm(tenant.mobilePhone());
        setValueOnForm(tenant.workPhone());
        setValueOnForm(tenant.driversLicenseState());
        setValueOnForm(tenant.driversLicense());
        setValueOnForm(tenant.secureIdentifier());
        setValueOnForm(tenant.notCanadianCitizen());

        enterAddressForm(tenant.currentAddress().getPath(), detach(tenant.currentAddress()));
        if (BusinessRules.infoPageNeedPreviousAddress(tenant.currentAddress().moveInDate().getValue())) {
            enterAddressForm(tenant.previousAddress().getPath(), detach(tenant.previousAddress()));
        } else {
            assertNotVisible(D.id(tenant.previousAddress().getPath(), detach(tenant.previousAddress()).street1()));
        }

        //Vehicles
        int num = 0;
        for (Vehicle vehicle : tenant.vehicles()) {
            selenium.click(D.id(tenant.vehicles(), FormNavigationDebugId.Form_Add));
            enterVehiclesForm(D.id(tenant.vehicles(), num), detach(vehicle));
            num++;
        }
        //verify size (e.g. no next row exists)
        assertFalse(selenium.isElementPresent(D.id(proto(PotentialTenantInfo.class).vehicles(), num, proto(Vehicle.class).plateNumber())));

        //Legal Questions
        //TODO
//        setValueOnForm(tenant.legalQuestions().suedForRent());
//        setValueOnForm(tenant.legalQuestions().suedForDamages());
//        setValueOnForm(tenant.legalQuestions().everEvicted());
//        setValueOnForm(tenant.legalQuestions().defaultedOnLease());
//        setValueOnForm(tenant.legalQuestions().convictedOfFelony());
//        setValueOnForm(tenant.legalQuestions().legalTroubles());
//        setValueOnForm(tenant.legalQuestions().filedBankruptcy());

        //Emergency Contacts
        num = 0;
        for (EmergencyContact contact : tenant.emergencyContacts()) {
            if (num != 0) {
                selenium.click(D.id(tenant.emergencyContacts(), FormNavigationDebugId.Form_Add));
            }
            enterEmContactsForm(D.id(tenant.emergencyContacts(), num), detach(contact));
            num++;
        }
    }

    private void enterAddressForm(IDebugId fromDebugId, Address address) {
        setValueOnForm(fromDebugId, address.street1());
        setValueOnForm(fromDebugId, address.street2());
        setValueOnForm(fromDebugId, address.city());
        setValueOnForm(fromDebugId, address.phone());
        setValueOnForm(fromDebugId, address.moveInDate());
        setValueOnForm(fromDebugId, address.moveOutDate());

        setValueOnForm(fromDebugId, address.country());
        setValueOnForm(fromDebugId, address.postalCode());
        setValueOnForm(fromDebugId, address.province());

        setValueOnForm(fromDebugId, address.rented());
        if (OwnedRented.Owned == address.rented().getValue()) {
            assertNotVisible(D.id(fromDebugId, address.payment()));
            assertNotVisible(D.id(fromDebugId, address.managerName()));
        } else {
            assertVisible(D.id(fromDebugId, address.payment()));
            assertVisible(D.id(fromDebugId, address.managerName()));
            setValueOnForm(fromDebugId, address.payment());
            setValueOnForm(fromDebugId, address.managerName());
        }

    }

    private void enterVehiclesForm(IDebugId fromDebugId, Vehicle vehicle) {
        setValueOnForm(fromDebugId, vehicle.plateNumber());
        //TODO
        //setValueOnForm(fromDebugId, vehicle.year());
        setValueOnForm(fromDebugId, vehicle.make());
        setValueOnForm(fromDebugId, vehicle.model());
        setValueOnForm(fromDebugId, vehicle.country());
        setValueOnForm(fromDebugId, vehicle.province());
    }

    private void enterEmContactsForm(IDebugId fromDebugId, EmergencyContact contact) {
        setValueOnForm(fromDebugId, contact.firstName());
        setValueOnForm(fromDebugId, contact.middleName());
        setValueOnForm(fromDebugId, contact.lastName());
        setValueOnForm(fromDebugId, contact.homePhone());
        setValueOnForm(fromDebugId, contact.mobilePhone());
        setValueOnForm(fromDebugId, contact.workPhone());
        setValueOnForm(fromDebugId, contact.address().street1());
        setValueOnForm(fromDebugId, contact.address().street2());
        setValueOnForm(fromDebugId, contact.address().city());
        setValueOnForm(fromDebugId, contact.address().country());
        setValueOnForm(fromDebugId, contact.address().postalCode());
        setValueOnForm(fromDebugId, contact.address().province());
    }
}
