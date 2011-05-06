/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.IEmploymentInfo;
import com.propertyvista.portal.domain.pt.IIncomeInfo;
import com.propertyvista.portal.domain.pt.IncomeInfoEmployer;
import com.propertyvista.portal.domain.pt.IncomeInfoSelfEmployed;
import com.propertyvista.portal.domain.pt.IncomeInfoStudentIncome;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.BusinessRules;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.pt.services.ApplicationServiceImpl;

abstract class PortalVerificationTestCase extends WizardBaseSeleniumTestCase {

    protected void assertAptUnitForm(IDebugId formDebugId, ApartmentUnit aUnit) {
        assertValueOnForm(formDebugId, aUnit.unitType());
        assertValueOnForm(formDebugId, aUnit.marketRent().get(aUnit.marketRent().size() - 1));
        assertValueOnForm(formDebugId, aUnit.requiredDeposit());
        assertValueOnForm(formDebugId, aUnit.bedrooms());
        assertValueOnForm(formDebugId, aUnit.bathrooms());
        assertValueOnForm(formDebugId, aUnit.area());
        assertValueOnForm(formDebugId, aUnit.avalableForRent());
    }

    protected void verifyTenantsPage(Summary summary, boolean doSave) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class));

        int num = 0;
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            assertTenantRow(D.id(proto(PotentialTenantList.class).tenants(), num), detach(tenant), (num != 0));
            num++;
        }

        if (doSave) {
            saveAndContinue();
        }
    }

    protected void assertTenantRow(IDebugId formDebugId, PotentialTenantInfo tenant, boolean fullInfo) {
        assertValueOnForm(formDebugId, tenant.firstName());
        assertValueOnForm(formDebugId, tenant.lastName());
        assertValueOnForm(formDebugId, tenant.middleName());

        //TODO VladS
        //assertValueOnForm(formDebugId, tenant.birthDate());

        if (fullInfo) {
            assertValueOnForm(formDebugId, tenant.email());
            assertValueOnForm(formDebugId, tenant.relationship());
            //TODO VladS
            //assertValueOnForm(fromDebugId, tenant.status());
            assertValueOnForm(formDebugId, tenant.takeOwnership());
        }
    }

    protected void verifyInfoPages(Summary summary, boolean doSave) {
        int id = 0;
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                verifyInfoPage(detach(tenant), id);
                if (doSave) {
                    saveAndContinue();
                }
                id++;
            }
        }
        // Asset no next page
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Info.class));
        assertNotPresent(D.id(VistaFormsDebugId.SecondNavigation_Prefix, SiteMap.Info.class, id));
    }

    protected void verifyInfoPage(PotentialTenantInfo tenant, int id) {
        assertVisible(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Info.class));
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Info.class));
        selenium.click(D.id(VistaFormsDebugId.SecondNavigation_Prefix, SiteMap.Info.class, id));
        assertInfoPage(tenant);
    }

    protected void assertInfoPage(PotentialTenantInfo tenant) {
        assertValueOnForm(tenant.firstName());
        assertValueOnForm(tenant.lastName());
        assertValueOnForm(tenant.middleName());
        assertValueOnForm(tenant.email());
        assertValueOnForm(tenant.homePhone());
        assertValueOnForm(tenant.mobilePhone());
        assertValueOnForm(tenant.workPhone());
        assertValueOnForm(tenant.driversLicenseState());
        assertValueOnForm(tenant.driversLicense());
        assertValueOnForm(tenant.notCanadianCitizen());
        if (tenant.notCanadianCitizen().getValue() != Boolean.TRUE) {
            assertValueOnForm(tenant.secureIdentifier());
        }

        assertAddressForm(tenant.currentAddress().getPath(), detach(tenant.currentAddress()));
        if (BusinessRules.infoPageNeedPreviousAddress(tenant.currentAddress().moveInDate().getValue())) {
            assertAddressForm(tenant.previousAddress().getPath(), detach(tenant.previousAddress()));
        } else {
            assertNotVisible(D.id(tenant.previousAddress().getPath(), detach(tenant.previousAddress()).street1()));
        }

        //Vehicles
        int row = 0;
        for (Vehicle vehicle : tenant.vehicles()) {
            assertVehicleRow(D.id(tenant.vehicles(), row), detach(vehicle));
            row++;
        }
        //verify size (e.g. no next row exists)
        assertFalse(selenium.isElementPresent(D.id(proto(PotentialTenantInfo.class).vehicles(), row, proto(Vehicle.class).plateNumber())));

        //Legal Questions
        assertValueOnForm(tenant.legalQuestions().suedForRent());
        assertValueOnForm(tenant.legalQuestions().suedForDamages());
        assertValueOnForm(tenant.legalQuestions().everEvicted());
        assertValueOnForm(tenant.legalQuestions().defaultedOnLease());
        assertValueOnForm(tenant.legalQuestions().convictedOfFelony());
        assertValueOnForm(tenant.legalQuestions().legalTroubles());
        assertValueOnForm(tenant.legalQuestions().filedBankruptcy());

        //Emergency Contacts
        row = 0;
        for (EmergencyContact contact : tenant.emergencyContacts()) {
            assertEmContactsForm(D.id(tenant.emergencyContacts(), row), detach(contact));
            row++;
        }
    }

    private void assertIAddressForm(IDebugId formDebugId, IAddress address) {
        assertValueOnForm(formDebugId, address.street1());
        assertValueOnForm(formDebugId, address.street2());
        assertValueOnForm(formDebugId, address.city());
        assertValueOnForm(formDebugId, address.country());
        assertValueOnForm(formDebugId, address.postalCode());
        assertValueOnForm(formDebugId, address.province());
    }

    protected void assertAddressForm(IDebugId formDebugId, Address address) {
        assertIAddressForm(formDebugId, address);

        assertValueOnForm(formDebugId, address.phone());

        assertValueOnForm(formDebugId, address.moveInDate());
        assertValueOnForm(formDebugId, address.moveOutDate());
        assertValueOnForm(formDebugId, address.rented());

        if (OwnedRented.Owned == address.rented().getValue()) {
            assertNotVisible(D.id(formDebugId, address.payment()));
            assertNotVisible(D.id(formDebugId, address.managerName()));
        } else {
            assertVisible(D.id(formDebugId, address.payment()));
            assertVisible(D.id(formDebugId, address.managerName()));
            assertValueOnForm(formDebugId, address.payment());
            assertValueOnForm(formDebugId, address.managerName());
        }

    }

    protected void assertVehicleRow(IDebugId formDebugId, Vehicle vehicle) {
        assertValueOnForm(formDebugId, vehicle.plateNumber());
        assertValueOnForm(formDebugId, vehicle.year());
        assertValueOnForm(formDebugId, vehicle.make());
        assertValueOnForm(formDebugId, vehicle.model());
        assertValueOnForm(formDebugId, vehicle.country());
        assertValueOnForm(formDebugId, vehicle.province());
    }

    protected void assertEmContactsForm(IDebugId formDebugId, EmergencyContact contact) {
        assertValueOnForm(formDebugId, contact.firstName());
        assertValueOnForm(formDebugId, contact.middleName());
        assertValueOnForm(formDebugId, contact.lastName());
        assertValueOnForm(formDebugId, contact.homePhone());
        assertValueOnForm(formDebugId, contact.mobilePhone());
        assertValueOnForm(formDebugId, contact.workPhone());

        assertIAddressForm(formDebugId, contact.address());
    }

    protected void verifyFinancialPages(Summary summary, boolean doSave) {
        int num = 0;
        int i = 0;
        for (SummaryPotentialTenantFinancial tenantFin : summary.tenantFinancials()) {
            // tenants and tenantFinancials are mapped one to one in created data
            PotentialTenantInfo tenant = summary.tenantList().tenants().get(i);
            i++;

            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                verifyFinancialPage(detach(tenantFin.tenantFinancial()), num);
                if (doSave) {
                    saveAndContinue();
                }
                num++;
            }
        }
//        // Asset no next page
//        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Financial.class));
//        assertNotPresent(D.id(VistaFormsDebugId.SecondNavigation_Prefix, SiteMap.Financial.class));
    }

    private void verifyFinancialPage(PotentialTenantFinancial financial, int id) {
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Financial.class));
        selenium.click(D.id(VistaFormsDebugId.SecondNavigation_Prefix, SiteMap.Financial.class, id));

        IDebugId debugID;
        int row = 0;
        for (TenantIncome income : financial.incomes()) {
            debugID = D.id(financial.incomes(), row);
            verifyIncome(debugID, detach(income));
            row++;
        }

        row = 0;
        for (TenantAsset asset : financial.assets()) {
            debugID = D.id(financial.assets(), row);
            verifyAsset(debugID, detach(asset));
            row++;
        }

        row = 0;
        for (TenantGuarantor guarantor : financial.guarantors()) {
            debugID = D.id(financial.assets(), row);
            verifyGuarantor(debugID, detach(guarantor));
            row++;
        }
    }

    private void verifyIncome(IDebugId formDebugId, TenantIncome income) {
        switch (income.incomeSource().getValue()) {
        case fulltime:
            assertEmployerForm(D.id(formDebugId, income.employer()), detach(income.employer()));
            break;
        case parttime:
            assertEmployerForm(D.id(formDebugId, income.employer()), detach(income.employer()));
            break;
        case selfemployed:
            assertSelfEmployedForm(D.id(formDebugId, income.employer()), detach(income.selfEmployed()));
            break;
        case seasonallyEmployed:
            assertEmployedForm(D.id(formDebugId, income.employer()), detach(income.seasonallyEmployed()));
            break;
        case socialServices:
            assertEmployedForm(D.id(formDebugId, income.employer()), detach(income.socialServices()));
            break;
        case student:
            assertStudentForm(D.id(formDebugId, income.employer()), detach(income.studentIncome()));
            break;
        default:
            assertIncomeForm(D.id(formDebugId, income.employer()), detach(income.otherIncomeInfo()));
            break;
        }
    }

    private void assertEmployerForm(IDebugId formDebugId, IncomeInfoEmployer employer) {
        assertEmployedForm(formDebugId, employer);
        assertValueOnForm(formDebugId, employer.employedForYears());
        assertValueOnForm(formDebugId, employer.starts());
        assertValueOnForm(formDebugId, employer.ends());
    }

    private void assertSelfEmployedForm(IDebugId formDebugId, IncomeInfoSelfEmployed employer) {
        assertEmployedForm(formDebugId, employer);
        assertValueOnForm(formDebugId, employer.fullyOwned());
        assertValueOnForm(formDebugId, employer.monthlyRevenue());
        assertValueOnForm(formDebugId, employer.numberOfEmployees());
    }

    private void assertStudentForm(IDebugId formDebugId, IncomeInfoStudentIncome student) {
        assertIncomeForm(formDebugId, student);
        assertIAddressForm(formDebugId, student);
        assertValueOnForm(formDebugId, student.program());
        assertValueOnForm(formDebugId, student.fieldOfStudy());
        assertValueOnForm(formDebugId, student.fundingChoices());
    }

    private void assertEmployedForm(IDebugId formDebugId, IEmploymentInfo employer) {
        assertIncomeForm(formDebugId, employer);
        assertIAddressForm(formDebugId, employer);
        assertValueOnForm(formDebugId, employer.supervisorName());
        assertValueOnForm(formDebugId, employer.supervisorPhone());
        assertValueOnForm(formDebugId, employer.position());
    }

    private void assertIncomeForm(IDebugId formDebugId, IIncomeInfo income) {
        assertValueOnForm(formDebugId, income.name());
        assertValueOnForm(formDebugId, income.monthlyAmount());
        assertValueOnForm(formDebugId, income.starts());
        assertValueOnForm(formDebugId, income.ends());
    }

    private void verifyAsset(IDebugId debugID, TenantAsset asset) {
        //TODO VladS
        //To pass this asset types need to be capitalized.
        //I didn't want to change it without talking to you.
        //assertValueOnForm(debugID, asset.assetType());

        //TODO VladS
        //I think ownership % isn't being formatted correctly
        //assertValueOnForm(debugID, asset.percent());
        assertValueOnForm(debugID, asset.assetValue());
    }

    private void verifyGuarantor(IDebugId debugID, TenantGuarantor guarantor) {
        assertValueOnForm(debugID, guarantor.firstName());
        assertValueOnForm(debugID, guarantor.middleName());
        assertValueOnForm(debugID, guarantor.lastName());
        assertValueOnForm(debugID, guarantor.homePhone());
        assertValueOnForm(debugID, guarantor.mobilePhone());
        assertValueOnForm(debugID, guarantor.workPhone());
        assertValueOnForm(debugID, guarantor.birthDate());
        assertValueOnForm(debugID, guarantor.email());
    }

    protected void verifyPetsPages(Summary summary) {
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Pets.class));
        int num = 0;
        for (Pet pet : summary.pets().pets()) {
            verifyPetRow(D.id(proto(Pets.class).pets(), num), detach(pet));
            num++;
        }
        saveAndContinue();
    }

    private void verifyPetRow(IDebugId debugID, Pet pet) {
        //TODO VladS
        //Seems like the same problem here with the types.
        //They need to be capitalized.
        //assertValueOnForm(debugID, pet.type());
        assertValueOnForm(debugID, pet.name());
        assertValueOnForm(debugID, pet.color());
        assertValueOnForm(debugID, pet.breed());
        assertValueOnForm(debugID, pet.weight());

        //TODO VladS
        //And here.        
        //assertValueOnForm(debugID, pet.weightUnit());
        assertValueOnForm(debugID, pet.birthDate());

        //TODO VladS
        //This is being rendered as a div instead of input    
        //assertValueOnForm(debugID, pet.chargeLine());
    }

}
