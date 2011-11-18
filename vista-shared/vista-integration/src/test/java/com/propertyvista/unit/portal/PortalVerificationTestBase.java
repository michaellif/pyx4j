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

import java.util.List;

import com.propertvista.generator.gdo.TenantSummaryGDO;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoStudentIncome;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.misc.BusinessRules;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationListDTO;
import com.propertyvista.portal.server.ptapp.services.ApplicationProgressMgr;
import com.propertyvista.server.common.util.TenantConverter;

abstract class PortalVerificationTestBase extends WizardSeleniumTestBase {

    protected static boolean shouldEnterInformation(TenantSummaryGDO tenantSummary) {
        return ApplicationProgressMgr.shouldEnterInformation(tenantSummary.tenantInLease(), tenantSummary.tenant().person().birthDate().getValue());
    }

    protected void assertAptUnitForm(IDebugId formDebugId, AptUnitDTO aUnit) {
        assertValueOnForm(formDebugId, aUnit.unitType());
        assertValueOnForm(formDebugId, aUnit.unitRent());
        assertValueOnForm(formDebugId, aUnit.requiredDeposit());
        assertValueOnForm(formDebugId, aUnit.bedrooms());
        assertValueOnForm(formDebugId, aUnit.bathrooms());
        assertValueOnForm(formDebugId, aUnit.area());
        assertValueOnForm(formDebugId, aUnit.availableForRent());
    }

    protected void verifyTenantsPage(List<TenantSummaryGDO> tenantsSummaryList, boolean doSave) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Tenants.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Tenants.class));

        TenantInApplicationListDTO tenants = TenantTestAdapter.getTenantListEditorDTO(tenantsSummaryList);
        int num = 0;
        for (TenantInLeaseDTO tenant : tenants.tenants()) {
            assertTenantRow(D.id(proto(TenantInApplicationListDTO.class).tenants(), num), detach(tenant), (num != 0));
            num++;
        }

        assertNotPresent(D.id(proto(TenantInApplicationListDTO.class).tenants(), num, proto(TenantInLeaseDTO.class).person().name().firstName()));

        if (doSave) {
            saveAndContinue();
        }
    }

    protected void assertTenantRow(IDebugId formDebugId, TenantInLeaseDTO tenant, boolean fullInfo) {
        assertValueOnForm(formDebugId, tenant.person().name().firstName());
        assertValueOnForm(formDebugId, tenant.person().name().lastName());
        assertValueOnForm(formDebugId, tenant.person().name().middleName());

        assertValueOnForm(formDebugId, tenant.person().birthDate());

        if (fullInfo) {
            assertValueOnForm(formDebugId, tenant.person().email());
            assertValueOnForm(formDebugId, tenant.relationship());
            assertValueOnForm(formDebugId, tenant.status());
            assertValueOnForm(formDebugId, tenant.takeOwnership());
        }
    }

    protected void verifyInfoPages(List<TenantSummaryGDO> tenants, boolean doSave) {
        int id = 0;
        for (TenantSummaryGDO tenantSummary : tenants) {
            if (shouldEnterInformation(tenantSummary)) {

                TenantInfoDTO dto = new TenantConverter.Tenant2TenantInfo().createDTO(tenantSummary.tenant());
                new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tenantSummary.tenantScreening(), dto);

                verifyInfoPage(dto, id);
                if (doSave) {
                    saveAndContinue();
                }
                id++;
            }
        }
        // Asset no next page
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Info.class));
        assertNotPresent(D.id(VistaFormsDebugId.SecondNavigation_Prefix, PtSiteMap.Info.class, id));
    }

    protected void verifyInfoPage(TenantInfoDTO tenant, int id) {
        assertVisible(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Info.class));
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Info.class));
        selenium.click(D.id(VistaFormsDebugId.SecondNavigation_Prefix, PtSiteMap.Info.class, id));
        assertInfoPage(tenant);
    }

    protected void assertInfoPage(TenantInfoDTO tenant) {
        assertValueOnForm(tenant.person().name().firstName());
        assertValueOnForm(tenant.person().name().lastName());
        assertValueOnForm(tenant.person().name().middleName());
        assertValueOnForm(tenant.person().email());
        assertValueOnForm(tenant.person().homePhone());
        assertValueOnForm(tenant.person().mobilePhone());
        assertValueOnForm(tenant.person().workPhone());
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
            assertNotVisible(D.id(tenant.previousAddress().getPath(), detach(tenant.previousAddress()).streetName()));
        }

        //Vehicles
        int row = 0;
//        for (Vehicle vehicle : tenant.vehicles()) {
//            assertVehicleRow(D.id(tenant.vehicles(), row), detach(vehicle));
//            row++;
//        }
//        //verify size (e.g. no next row exists)
//        assertFalse(selenium.isElementPresent(D.id(proto(TenantInfoEditorDTO.class).vehicles(), row, proto(Vehicle.class).plateNumber())));

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
        assertNotPresent(D.id(proto(TenantInfoDTO.class).emergencyContacts(), row, proto(EmergencyContact.class).name().firstName()));
    }

    protected void assertIAddressForm(IDebugId formDebugId, AddressSimple address) {
        assertValueOnForm(formDebugId, address.street1());
        assertValueOnForm(formDebugId, address.street2());
        assertValueOnForm(formDebugId, address.city());
        assertValueOnForm(formDebugId, address.country());
        assertValueOnForm(formDebugId, address.postalCode());
        assertValueOnForm(formDebugId, address.province());
    }

    protected void assertAddressForm(IDebugId formDebugId, AddressStructured address) {
        assertValueOnForm(formDebugId, address.suiteNumber());
        assertValueOnForm(formDebugId, address.streetNumber());
        assertValueOnForm(formDebugId, address.streetNumberSuffix());
        assertValueOnForm(formDebugId, address.streetName());
        assertValueOnForm(formDebugId, address.streetType());
        assertValueOnForm(formDebugId, address.streetDirection());
        assertValueOnForm(formDebugId, address.city());
        assertValueOnForm(formDebugId, address.county());
        assertValueOnForm(formDebugId, address.province());
        assertValueOnForm(formDebugId, address.postalCode());
        assertValueOnForm(formDebugId, address.country());
    }

    protected void assertAddressForm(IDebugId formDebugId, PriorAddress address) {
        assertAddressForm(formDebugId, address);

        assertValueOnForm(formDebugId, address.phone());

        assertValueOnForm(formDebugId, address.moveInDate());
        assertValueOnForm(formDebugId, address.moveOutDate());
        assertValueOnForm(formDebugId, address.rented());

        if (OwnedRented.owned == address.rented().getValue()) {
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
        assertValueOnForm(formDebugId, contact.name().firstName());
        assertValueOnForm(formDebugId, contact.name().middleName());
        assertValueOnForm(formDebugId, contact.name().lastName());
        assertValueOnForm(formDebugId, contact.homePhone());
        assertValueOnForm(formDebugId, contact.mobilePhone());
        assertValueOnForm(formDebugId, contact.workPhone());

        assertAddressForm(formDebugId, contact.address());
    }

    protected void verifyFinancialPages(List<TenantSummaryGDO> tenants, boolean doSave) {
        int num = 0;
        for (TenantSummaryGDO tenantSummary : tenants) {
            if (shouldEnterInformation(tenantSummary)) {
                verifyFinancialPage(new TenantConverter.TenantFinancialEditorConverter().createDTO(tenantSummary.tenantScreening()), num);
                if (doSave) {
                    saveAndContinue();
                }
                num++;
            }
        }
        // Asset no next page
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Financial.class));
        assertNotPresent(D.id(VistaFormsDebugId.SecondNavigation_Prefix, PtSiteMap.Financial.class, num));
    }

    protected void verifyFinancialPage(TenantFinancialDTO financial, int id) {
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Financial.class));
        selenium.click(D.id(VistaFormsDebugId.SecondNavigation_Prefix, PtSiteMap.Financial.class, id));

        IDebugId debugID;
        int row = 0;
        for (PersonalIncome income : financial.incomes()) {
            debugID = D.id(financial.incomes(), row);
            verifyIncome(debugID, detach(income));
            row++;
        }
        //TODO VladS
        //I'm not sure how to test this one
        //assertFalse(selenium.isElementPresent(D.id(proto(PotentialTenantFinancial.class).incomes(), row, proto(TenantIncome.class).())));

        row = 0;
        for (PersonalAsset asset : financial.assets()) {
            debugID = D.id(financial.assets(), row);
            verifyAsset(debugID, detach(asset));
            row++;
        }
        assertFalse(selenium.isElementPresent(D.id(proto(TenantFinancialDTO.class).assets(), row, proto(PersonalAsset.class).assetType())));

        row = 0;
        for (TenantGuarantor guarantor : financial.guarantors()) {
            debugID = D.id(financial.guarantors(), row);
            verifyGuarantor(debugID, detach(guarantor));
            row++;
        }
        assertNotPresent(D.id(proto(TenantFinancialDTO.class).guarantors(), row, proto(TenantGuarantor.class).name().firstName()));
    }

    private void verifyIncome(IDebugId formDebugId, PersonalIncome income) {
        switch (income.incomeSource().getValue()) {
        case fulltime:
            assertEmployerForm(D.id(formDebugId, income.employer()), detach(income.employer()));
            break;
        case parttime:
            assertEmployerForm(D.id(formDebugId, income.employer()), detach(income.employer()));
            break;
        case selfemployed:
            assertSelfEmployedForm(D.id(formDebugId, income.selfEmployed()), detach(income.selfEmployed()));
            break;
        case seasonallyEmployed:
            assertEmployedForm(D.id(formDebugId, income.seasonallyEmployed()), detach(income.seasonallyEmployed()));
            break;
        case socialServices:
            assertEmployedForm(D.id(formDebugId, income.socialServices()), detach(income.socialServices()));
            break;
        case student:
            assertStudentForm(D.id(formDebugId, income.studentIncome()), detach(income.studentIncome()));
            break;
        default:
            assertIncomeForm(D.id(formDebugId, income.otherIncomeInformation()), detach(income.otherIncomeInformation()));
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
        assertAddressForm(formDebugId, student.address());
        assertValueOnForm(formDebugId, student.program());
        assertValueOnForm(formDebugId, student.fieldOfStudy());
        assertValueOnForm(formDebugId, student.fundingChoices());
    }

    private void assertEmployedForm(IDebugId formDebugId, IEmploymentInfo employer) {
        assertIncomeForm(formDebugId, employer);
        assertAddressForm(formDebugId, employer.address());
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

    private void verifyAsset(IDebugId debugID, PersonalAsset asset) {
        assertValueOnForm(debugID, asset.assetType());

        //TODO VladS
        //I think ownership % isn't being formatted correctly
        //assertValueOnForm(debugID, asset.percent());
        assertValueOnForm(debugID, asset.assetValue());
    }

    private void verifyGuarantor(IDebugId debugID, TenantGuarantor guarantor) {
        assertValueOnForm(debugID, guarantor.name().firstName());
        assertValueOnForm(debugID, guarantor.name().middleName());
        assertValueOnForm(debugID, guarantor.name().lastName());
        assertValueOnForm(debugID, guarantor.homePhone());
        assertValueOnForm(debugID, guarantor.mobilePhone());
        assertValueOnForm(debugID, guarantor.workPhone());
        assertValueOnForm(debugID, guarantor.birthDate());
        assertValueOnForm(debugID, guarantor.email());
    }

// TODO it's now should be on Apartment page:     
//    protected void verifyPetsPages(List<Pet> pets, boolean doSave) {
//        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Addons.class));
//        int num = 0;
//        for (Pet pet : pets) {
//            verifyPetRow(D.id(proto(PetsDTO.class).list(), num), detach(pet));
//            num++;
//        }
//        assertNotPresent(D.id(proto(PetsDTO.class).list(), num, proto(Pet.class).type()));
//        if (doSave) {
//            saveAndContinue();
//        }
//    }

    private void verifyPetRow(IDebugId debugID, Pet pet) {
        assertValueOnForm(debugID, pet.name());
        assertValueOnForm(debugID, pet.color());
        assertValueOnForm(debugID, pet.breed());
        assertValueOnForm(debugID, pet.weight());

        assertValueOnForm(debugID, pet.weightUnit());
        assertValueOnForm(debugID, pet.birthDate());

        //TODO VladS
        //This is being rendered as a div instead of input    
        //assertValueOnForm(debugID, pet.chargeLine());
    }

}
