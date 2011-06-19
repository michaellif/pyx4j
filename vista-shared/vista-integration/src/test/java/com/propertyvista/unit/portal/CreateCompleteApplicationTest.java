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

import com.propertyvista.common.domain.IAddress;
import com.propertyvista.common.domain.IAddressFull;
import com.propertyvista.common.domain.User;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.Address;
import com.propertyvista.portal.domain.ptapp.Address.OwnedRented;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.EmergencyContact;
import com.propertyvista.portal.domain.ptapp.IEmploymentInfo;
import com.propertyvista.portal.domain.ptapp.IIncomeInfo;
import com.propertyvista.portal.domain.ptapp.IncomeInfoEmployer;
import com.propertyvista.portal.domain.ptapp.IncomeInfoSelfEmployed;
import com.propertyvista.portal.domain.ptapp.IncomeInfoStudentIncome;
import com.propertyvista.portal.domain.ptapp.Pet;
import com.propertyvista.portal.domain.ptapp.Pets;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.TenantAsset;
import com.propertyvista.portal.domain.ptapp.TenantGuarantor;
import com.propertyvista.portal.domain.ptapp.TenantIncome;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.Vehicle;
import com.propertyvista.portal.rpc.ptapp.AccountCreationRequest;
import com.propertyvista.portal.rpc.ptapp.BusinessRules;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.PTGenerator;
import com.propertyvista.portal.server.ptapp.services.ApplicationServiceImpl;
import com.propertyvista.server.common.reference.SharedData;

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
        PTGenerator generator = new PTGenerator(seed);
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
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class)));

        verifyTenantsPage(summary, false);
        verifyInfoPages(summary, false);
        verifyFinancialPages(summary, false);
        verifyPetsPages(summary, false);

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
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Apartment.class));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 0, proto(AptUnitDTO.class).unitType()));
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
        setValueOnForm(fromDebugId, tenant.name().firstName());
        setValueOnForm(fromDebugId, tenant.name().lastName());
        setValueOnForm(fromDebugId, tenant.name().middleName());
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
        assertValueOnForm(tenant.name().firstName());
        assertValueOnForm(tenant.name().lastName());
        //setValueOnForm(tenant.name().firstName());
        //setValueOnForm(tenant.name().lastName());
        setValueOnForm(tenant.name().middleName());

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
            assertNotVisible(D.id(tenant.previousAddress().getPath(), detach(tenant.previousAddress()).streetName()));
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

    private void enterIAddressForm(IDebugId formDebugId, IAddressFull address) {
        setValueOnForm(formDebugId, address.unitNumber());
        setValueOnForm(formDebugId, address.streetNumber());
        setValueOnForm(formDebugId, address.streetNumberSuffix());
        setValueOnForm(formDebugId, address.streetName());
        setValueOnForm(formDebugId, address.streetType());
        setValueOnForm(formDebugId, address.streetDirection());
        setValueOnForm(formDebugId, address.city());
        setValueOnForm(formDebugId, address.country());
        setValueOnForm(formDebugId, address.postalCode());
        setValueOnForm(formDebugId, address.province());
        setValueOnForm(formDebugId, address.county());
    }

    private void enterAddressForm(IDebugId formDebugId, Address address) {
        enterIAddressForm(formDebugId, address);

        setValueOnForm(formDebugId, address.phone());
        setValueOnForm(formDebugId, address.moveInDate());
        setValueOnForm(formDebugId, address.moveOutDate());

        setValueOnForm(formDebugId, address.rented());
        if (OwnedRented.owned == address.rented().getValue()) {
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
        setValueOnForm(fomrDebugId, contact.name().firstName());
        setValueOnForm(fomrDebugId, contact.name().middleName());
        setValueOnForm(fomrDebugId, contact.name().lastName());
        setValueOnForm(fomrDebugId, contact.homePhone());
        setValueOnForm(fomrDebugId, contact.mobilePhone());
        setValueOnForm(fomrDebugId, contact.workPhone());
        enterIAddressForm(fomrDebugId, contact.address());
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
            enterEmployerForm(D.id(formDebugId, income.employer()), detach(income.employer()));
            break;
        case parttime:
            enterEmployerForm(D.id(formDebugId, income.employer()), detach(income.employer()));
            break;
        case selfemployed:
            enterSelfEmployedForm(D.id(formDebugId, income.selfEmployed()), detach(income.selfEmployed()));
            break;
        case seasonallyEmployed:
            enterEmployedForm(D.id(formDebugId, income.seasonallyEmployed()), detach(income.seasonallyEmployed()));
            break;
        case socialServices:
            enterEmployedForm(D.id(formDebugId, income.socialServices()), detach(income.socialServices()));
            break;
        case student:
            enterStudentForm(D.id(formDebugId, income.studentIncome()), detach(income.studentIncome()));
            break;
        default:
            enterIncomeForm(D.id(formDebugId, income.otherIncomeInfo()), detach(income.otherIncomeInfo()));
            break;
        }
    }

    private void enterEmployerForm(IDebugId formDebugId, IncomeInfoEmployer employer) {
        enterEmployedForm(formDebugId, employer);
        setValueOnForm(formDebugId, employer.employedForYears());
        setValueOnForm(formDebugId, employer.starts());
        setValueOnForm(formDebugId, employer.ends());
    }

    private void enterSelfEmployedForm(IDebugId formDebugId, IncomeInfoSelfEmployed employer) {
        enterEmployedForm(formDebugId, employer);
        setValueOnForm(formDebugId, employer.fullyOwned());
        setValueOnForm(formDebugId, employer.monthlyRevenue());
        setValueOnForm(formDebugId, employer.numberOfEmployees());
    }

    private void enterStudentForm(IDebugId formDebugId, IncomeInfoStudentIncome student) {
        enterIncomeForm(formDebugId, student);
        enterIAddressForm(formDebugId, student);
        setValueOnForm(formDebugId, student.program());
        setValueOnForm(formDebugId, student.fieldOfStudy());
        setValueOnForm(formDebugId, student.fundingChoices());
    }

    private void enterEmployedForm(IDebugId formDebugId, IEmploymentInfo employer) {
        enterIncomeForm(formDebugId, employer);
        enterIAddressForm(formDebugId, employer);
        setValueOnForm(formDebugId, employer.supervisorName());
        setValueOnForm(formDebugId, employer.supervisorPhone());
        setValueOnForm(formDebugId, employer.position());
    }

    private void enterIncomeForm(IDebugId formDebugId, IIncomeInfo income) {
        setValueOnForm(formDebugId, income.name());
        setValueOnForm(formDebugId, income.monthlyAmount());
        setValueOnForm(formDebugId, income.starts());
        setValueOnForm(formDebugId, income.ends());
    }

    private void enterAssetRow(IDebugId formDebugId, TenantAsset asset) {
        setValueOnForm(formDebugId, asset.assetType());
        setValueOnForm(formDebugId, asset.percent());
        setValueOnForm(formDebugId, asset.assetValue());
    }

    private void enterGuarantorRow(IDebugId fromDebugId, TenantGuarantor guarantor) {
        setValueOnForm(fromDebugId, guarantor.name().firstName());
        setValueOnForm(fromDebugId, guarantor.name().middleName());
        setValueOnForm(fromDebugId, guarantor.name().lastName());
        setValueOnForm(fromDebugId, guarantor.homePhone());
        setValueOnForm(fromDebugId, guarantor.mobilePhone());
        setValueOnForm(fromDebugId, guarantor.workPhone());
        setValueOnForm(fromDebugId, guarantor.birthDate());
        setValueOnForm(fromDebugId, guarantor.email());
    }

    private void enterPetsPage(Summary summary) {
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Pets.class));
        int num = 0;
        for (Pet pet : summary.pets().pets()) {
            selenium.click(D.id(proto(Pets.class).pets(), FormNavigationDebugId.Form_Add));
            enterPetRow(D.id(proto(Pets.class).pets(), num), detach(pet));
            num++;
        }

        saveAndContinue();
    }

    private void enterPetRow(IDebugId debugID, Pet pet) {
        setValueOnForm(debugID, pet.type());
        setValueOnForm(debugID, pet.name());
        setValueOnForm(debugID, pet.color());
        setValueOnForm(debugID, pet.breed());
        setValueOnForm(debugID, pet.weight());

        setValueOnForm(debugID, pet.weightUnit());
        setValueOnForm(debugID, pet.birthDate());

        //TODO VladS
        //This is being rendered as a div instead of input    
        //assertValueOnForm(debugID, pet.chargeLine());
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
        summary.fullName().setValue(EntityFromatUtils.nvl_concat(" ", mainTenant.name().firstName(), mainTenant.name().lastName()));

        setValueOnForm(summary.agree());
        setValueOnForm(summary.fullName());

        saveAndContinue();

    }
}
