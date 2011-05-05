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
import com.propertyvista.portal.domain.pt.IncomeInfoEmployer;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.BusinessRules;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.pt.services.ApplicationServiceImpl;

abstract class PortalVerificationTestCase extends WizardBaseSeleniumTestCase {

    protected void assertAptUnitForm(IDebugId fromDebugId, ApartmentUnit aUnit) {
        assertValueOnForm(fromDebugId, aUnit.unitType());
        assertValueOnForm(fromDebugId, aUnit.marketRent().get(aUnit.marketRent().size() - 1));
        assertValueOnForm(fromDebugId, aUnit.requiredDeposit());
        assertValueOnForm(fromDebugId, aUnit.bedrooms());
        assertValueOnForm(fromDebugId, aUnit.bathrooms());
        assertValueOnForm(fromDebugId, aUnit.area());
        assertValueOnForm(fromDebugId, aUnit.avalableForRent());
    }

    protected void verifyTenantsPage(Summary summary) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Tenants.class));

        int num = 0;
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            assertTenantRow(D.id(proto(PotentialTenantList.class).tenants(), num), detach(tenant), (num != 0));
            num++;
        }

        saveAndContinue();
    }

    protected void assertTenantRow(IDebugId fromDebugId, PotentialTenantInfo tenant, boolean fullInfo) {
        assertValueOnForm(fromDebugId, tenant.firstName());
        assertValueOnForm(fromDebugId, tenant.lastName());
        assertValueOnForm(fromDebugId, tenant.middleName());

        //TODO
        //assertValueOnForm(fromDebugId, tenant.birthDate());

        if (fullInfo) {
            assertValueOnForm(fromDebugId, tenant.email());
            assertValueOnForm(fromDebugId, tenant.relationship());
            //TODO
            //assertValueOnForm(fromDebugId, tenant.status());
            assertValueOnForm(fromDebugId, tenant.takeOwnership());
        }
    }

    protected void verifyInfoPages(Summary summary) {
        int id = 0;
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                verifyInfoPage(detach(tenant), id);
                saveAndContinue();
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

    protected void verifyFinancialPages(Summary summary) {
        int num = 0;
        int i = 0;
        for (SummaryPotentialTenantFinancial tenantFin : summary.tenantFinancials()) {
            // tenants and tenantFinancials are mapped one to one in created data
            PotentialTenantInfo tenant = summary.tenantList().tenants().get(i);
            i++;

            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                verifyFinancialPage(detach(tenantFin.tenantFinancial()), num);
                saveAndContinue();
                num++;
            }
        }
        // Asset no next page
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Financial.class));
        assertNotPresent(D.id(VistaFormsDebugId.SecondNavigation_Prefix, SiteMap.Financial.class));
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
    }

    private void verifyIncome(IDebugId formDebugID, TenantIncome income) {
        switch (income.incomeSource().getValue()) {
        case fulltime:
            assertEmployerForm(D.id(formDebugID, income.employer()), detach(income.employer()));
        case parttime:
            assertEmployerForm(D.id(formDebugID, income.employer()), detach(income.employer()));
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

    private void assertEmployerForm(IDebugId formDebugID, IncomeInfoEmployer employer) {
        assertValueOnForm(formDebugID, employer.name());
        assertValueOnForm(formDebugID, employer.employedForYears());
        assertValueOnForm(formDebugID, employer.street1());
        assertValueOnForm(formDebugID, employer.street2());
        assertValueOnForm(formDebugID, employer.city());
        assertValueOnForm(formDebugID, employer.province());
        assertValueOnForm(formDebugID, employer.country());
        assertValueOnForm(formDebugID, employer.postalCode());
        assertValueOnForm(formDebugID, employer.supervisorName());
        assertValueOnForm(formDebugID, employer.supervisorPhone());
        //assertValueOnForm(formDebugID, employer.monthlyAmount().amount());
        assertValueOnForm(formDebugID, employer.position());
        assertValueOnForm(formDebugID, employer.starts());
        assertValueOnForm(formDebugID, employer.ends());
    }

    protected void assertAddressForm(IDebugId fromDebugId, Address address) {
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

    protected void assertVehicleRow(IDebugId fromDebugId, Vehicle vehicle) {
        assertValueOnForm(fromDebugId, vehicle.plateNumber());
        assertValueOnForm(fromDebugId, vehicle.year());
        assertValueOnForm(fromDebugId, vehicle.make());
        assertValueOnForm(fromDebugId, vehicle.model());
        assertValueOnForm(fromDebugId, vehicle.country());
        assertValueOnForm(fromDebugId, vehicle.province());
    }

    protected void assertEmContactsForm(IDebugId fromDebugId, EmergencyContact contact) {
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
