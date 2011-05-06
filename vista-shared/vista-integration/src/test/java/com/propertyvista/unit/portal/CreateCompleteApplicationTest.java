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

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.IncomeInfoEmployer;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.BusinessRules;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.pt.services.ApplicationServiceImpl;

public class CreateCompleteApplicationTest extends PortalVerificationTestCase {

    private static final Logger log = LoggerFactory.getLogger(CreateCompleteApplicationTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
        // This is just to make the test more visual
        selenium.setFocusOnGetValue(true);
    }

    public void testFullFlow() {
        long seed = 101;

        //TODO VladS
        //This breaks Date validation
        //seed = -3341811257066812540L;

        exectuteFlow(seed);
    }

    // Whiled goose chase
    public void OFF_testRandomizedData() {
        for (int n = 1; n <= 10; n++) {
            Random random = new Random();
            exectuteFlow(random.nextLong());
        }
    }

    public void exectuteFlow(long seed) {
        log.info("execute flow with seed {}", seed);
        VistaDataGenerator generator = new VistaDataGenerator(seed);
        User user = createTestUser();
        Application application = generator.createApplication(user);
        Summary summary = generator.createSummary(application, null);

        createAccount(user);
        enterUnitSelection();
        enterTenantsPage(summary);
        enterTestInfoPages(summary);
        enterFinancialPages(summary);
        enterPetsPage(summary);
        enterChargesPage(summary);

        enterSummaryPage(summary);

        selenium.click(VistaFormsDebugId.Auth_LogOutTop);

        // verify entered data

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class)));

        verifyTenantsPage(summary, false);
        verifyInfoPages(summary, false);
        verifyFinancialPages(summary, false);

        //TODO Leon

        // End test so we can start again
        selenium.click(VistaFormsDebugId.Auth_LogOutTop);
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
            enterTenantRow(D.id(proto(PotentialTenantList.class).tenants(), num), detach(tenant), (num != 0));
            num++;
        }
        saveAndContinue();
    }

    private void enterTenantRow(IDebugId fromDebugId, PotentialTenantInfo tenant, boolean fullInfo) {
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
        setValueOnForm(tenant.notCanadianCitizen());
        if (tenant.notCanadianCitizen().getValue() != Boolean.TRUE) {
            setValueOnForm(tenant.secureIdentifier());
        }

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
            enterVehicleRow(D.id(tenant.vehicles(), num), detach(vehicle));
            num++;
        }
        //verify size (e.g. no next row exists)
        assertFalse(selenium.isElementPresent(D.id(proto(PotentialTenantInfo.class).vehicles(), num, proto(Vehicle.class).plateNumber())));

        //Legal Questions
        setValueOnForm(tenant.legalQuestions().suedForRent());
        setValueOnForm(tenant.legalQuestions().suedForDamages());
        setValueOnForm(tenant.legalQuestions().everEvicted());
        setValueOnForm(tenant.legalQuestions().defaultedOnLease());
        setValueOnForm(tenant.legalQuestions().convictedOfFelony());
        setValueOnForm(tenant.legalQuestions().legalTroubles());
        setValueOnForm(tenant.legalQuestions().filedBankruptcy());

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

    private void enterIAddressForm(IDebugId formDebugId, IAddress address) {
        setValueOnForm(formDebugId, address.street1());
        setValueOnForm(formDebugId, address.street2());
        setValueOnForm(formDebugId, address.city());
        setValueOnForm(formDebugId, address.country());
        setValueOnForm(formDebugId, address.postalCode());
        setValueOnForm(formDebugId, address.province());
    }

    private void enterAddressForm(IDebugId formDebugId, Address address) {
        enterIAddressForm(formDebugId, address);

        setValueOnForm(formDebugId, address.phone());
        setValueOnForm(formDebugId, address.moveInDate());
        setValueOnForm(formDebugId, address.moveOutDate());

        setValueOnForm(formDebugId, address.rented());
        if (OwnedRented.Owned == address.rented().getValue()) {
            assertNotVisible(D.id(formDebugId, address.payment()));
            assertNotVisible(D.id(formDebugId, address.managerName()));
        } else {
            assertVisible(D.id(formDebugId, address.payment()));
            assertVisible(D.id(formDebugId, address.managerName()));
            setValueOnForm(formDebugId, address.payment());
            setValueOnForm(formDebugId, address.managerName());
        }

    }

    private void enterVehicleRow(IDebugId formDebugId, Vehicle vehicle) {
        setValueOnForm(formDebugId, vehicle.plateNumber());
        setValueOnForm(formDebugId, vehicle.year());
        setValueOnForm(formDebugId, vehicle.make());
        setValueOnForm(formDebugId, vehicle.model());
        setValueOnForm(formDebugId, vehicle.country());
        setValueOnForm(formDebugId, vehicle.province());
    }

    private void enterEmContactsForm(IDebugId fomrDebugId, EmergencyContact contact) {
        setValueOnForm(fomrDebugId, contact.firstName());
        setValueOnForm(fomrDebugId, contact.middleName());
        setValueOnForm(fomrDebugId, contact.lastName());
        setValueOnForm(fomrDebugId, contact.homePhone());
        setValueOnForm(fomrDebugId, contact.mobilePhone());
        setValueOnForm(fomrDebugId, contact.workPhone());
        setValueOnForm(fomrDebugId, contact.address().street1());
        setValueOnForm(fomrDebugId, contact.address().street2());
        setValueOnForm(fomrDebugId, contact.address().city());
        setValueOnForm(fomrDebugId, contact.address().country());
        setValueOnForm(fomrDebugId, contact.address().postalCode());
        setValueOnForm(fomrDebugId, contact.address().province());
    }

    private void enterFinancialPages(Summary summary) {
        int i = 0;
        for (SummaryPotentialTenantFinancial tenantFin : summary.tenantFinancials()) {
            // tenants and tenantFinancials are mapped one to one in created data
            PotentialTenantInfo tenant = summary.tenantList().tenants().get(i);
            i++;

            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                enterFinancialForm(detach(tenantFin.tenantFinancial()));
                saveAndContinue();
            }
        }
    }

    private void enterFinancialForm(PotentialTenantFinancial tenantFin) {
        //incomes
        int num = 0;
        for (TenantIncome income : tenantFin.incomes()) {
            selenium.click(D.id(tenantFin.incomes(), FormNavigationDebugId.Form_Add));
            enterIncomeRow(D.id(tenantFin.incomes(), num), detach(income));
            num++;
        }

        num = 0;
        for (TenantAsset asset : tenantFin.assets()) {
            selenium.click(D.id(tenantFin.assets(), FormNavigationDebugId.Form_Add));
            enterAssetRow(D.id(tenantFin.assets(), num), detach(asset));
            num++;
        }

        num = 0;
        for (TenantGuarantor guarantor : tenantFin.guarantors()) {
            selenium.click(D.id(tenantFin.guarantors(), FormNavigationDebugId.Form_Add));
            enterGuarantorRow(D.id(tenantFin.guarantors(), num), detach(guarantor));
            num++;
        }

    }

    private void enterIncomeRow(IDebugId formDebugId, TenantIncome income) {
        setValueOnForm(formDebugId, income.incomeSource());

        switch (income.incomeSource().getValue()) {
        case fulltime:
        case parttime:
            enterEmployerForm(D.id(formDebugId, income.employer()), detach(income.employer()));
            break;
        case selfemployed:
            // TODO Leon
            // income.selfEmployed();
            break;
        case seasonallyEmployed:
            // TODO Leon
            // income.seasonallyEmployed()
            break;
        case socialServices:
            // TODO Leon
            // income.socialServices()
            break;
        case student:
            // TODO Leon
            // income.studentIncome()
            break;
        default:
            // TODO Leon
            // income.otherIncomeInfo());
        }

    }

    private void enterEmployerForm(IDebugId formDebugId, IncomeInfoEmployer employer) {
        setValueOnForm(formDebugId, employer.name());
        setValueOnForm(formDebugId, employer.employedForYears());
        enterIAddressForm(formDebugId, employer);
        setValueOnForm(formDebugId, employer.monthlyAmount());

        setValueOnForm(formDebugId, employer.supervisorName());
        setValueOnForm(formDebugId, employer.supervisorPhone());
        setValueOnForm(formDebugId, employer.position());
        setValueOnForm(formDebugId, employer.starts());
        setValueOnForm(formDebugId, employer.ends());
    }

    private void enterAssetRow(IDebugId formDebugId, TenantAsset asset) {
        setValueOnForm(formDebugId, asset.assetType());
        setValueOnForm(formDebugId, asset.percent());
        setValueOnForm(formDebugId, asset.assetValue());
    }

    private void enterGuarantorRow(IDebugId fromDebugId, TenantGuarantor guarantor) {
        setValueOnForm(fromDebugId, guarantor.firstName());
        setValueOnForm(fromDebugId, guarantor.middleName());
        setValueOnForm(fromDebugId, guarantor.lastName());

        // TODO Leon

        setValueOnForm(fromDebugId, guarantor.birthDate());
        setValueOnForm(fromDebugId, guarantor.email());
    }

    private void enterPetsPage(Summary summary) {
        // TODO Leon

        saveAndContinue();
    }

    private void enterChargesPage(Summary summary) {
        // TODO Leon

        saveAndContinue();
    }

    @SuppressWarnings("unchecked")
    private void enterSummaryPage(Summary summary) {
        // This data is not generated
        // Forge the  Digital Signature
        summary.agree().setValue(Boolean.TRUE);

        PotentialTenantInfo mainTenant = summary.tenantList().tenants().get(0);
        summary.fullName().setValue(EntityFromatUtils.nvl_concat(" ", mainTenant.firstName(), mainTenant.lastName()));

        setValueOnForm(summary.agree());
        setValueOnForm(summary.fullName());

        saveAndContinue();

    }
}
