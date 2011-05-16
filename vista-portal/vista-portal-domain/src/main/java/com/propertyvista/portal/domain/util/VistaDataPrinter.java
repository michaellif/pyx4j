/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.domain.util;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.domain.User;
import com.propertyvista.common.domain.marketing.MarketRent;
import com.propertyvista.portal.domain.ptapp.Address;
import com.propertyvista.portal.domain.ptapp.ApartmentUnit;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.ChargeLineSelectable;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.Pet;
import com.propertyvista.portal.domain.ptapp.Pets;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.TenantAsset;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantGuarantor;
import com.propertyvista.portal.domain.ptapp.TenantIncome;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.Vehicle;

public class VistaDataPrinter {

    public static String print(Summary summary) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n---------------------------- APPLICATION -----------------------------\n");
        sb.append(print(summary.application()));

        //        loadApplication(sb);
        //        loadApplicationProgress(sb);
        //
        sb.append("\n\n---------------------------- UNIT SELECTION --------------------------\n");
        sb.append(print(summary.unitSelection()));

        sb.append("\n\n---------------------------- TENANTS ---------------------------------\n");
        sb.append(print(summary.tenantList()));

        sb.append("\n\n---------------------------- FINANCIALS ---------------------------------\n");
        sb.append(print(summary.tenantFinancials()));

        sb.append("\n\n---------------------------- PETS ------------------------------------\n");
        sb.append(print(summary.pets()));

        sb.append("\n\n---------------------------- CHARGES ---------------------------------\n");
        sb.append(print(summary.charges()));

        sb.append("\n\n\n");

        return sb.toString();
    }

    public static String print(PotentialTenantList tenantList) {
        StringBuilder sb = new StringBuilder();

        sb.append(tenantList.tenants().size()).append(" potential tenants");
        sb.append("\n");

        for (PotentialTenantInfo tenant : tenantList.tenants()) {

            sb.append("\n--- tenant ---\n");
            sb.append(print(tenant));
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String print(PotentialTenantInfo tenant) {
        StringBuilder sb = new StringBuilder();

        sb.append(tenant.status().getStringView());
        sb.append(", ");

        sb.append(tenant.firstName().getStringView());
        sb.append(" ");
        if (tenant.middleName().getStringView().length() > 0) {
            sb.append(tenant.middleName().getStringView());
            sb.append(" ");
        }
        sb.append(tenant.lastName().getStringView());

        sb.append("\t\t Born on ");
        sb.append(tenant.birthDate().getValue());

        sb.append("\t");
        sb.append(tenant.homePhone().getStringView()).append(" | ").append(tenant.mobilePhone().getStringView());

        sb.append("\t");
        sb.append(tenant.email().getStringView());

        sb.append("\t");

        sb.append("\t Payment $").append(tenant.payment().getStringView());

        sb.append("\n\t");

        sb.append(tenant.driversLicense().getStringView()).append(" ").append(tenant.driversLicenseState().getStringView());

        sb.append("\t").append(tenant.secureIdentifier().getStringView());

        sb.append("Current address");
        sb.append(VistaDataPrinter.print(tenant.currentAddress()));
        sb.append("Previous address");
        sb.append(VistaDataPrinter.print(tenant.previousAddress()));

        sb.append("\nVehicles\n");
        // vehicles
        for (Vehicle vehicle : tenant.vehicles()) {
            sb.append("\n\t");
            sb.append(vehicle.year().getStringView()).append(" ");
            sb.append(vehicle.province().getStringView()).append(" ");
            sb.append(vehicle.make().getStringView()).append(" ").append(vehicle.model().getStringView()).append(" ");
            sb.append(vehicle.plateNumber().getStringView()).append(" ");
        }

        return sb.toString();
    }

    public static String print(IList<SummaryPotentialTenantFinancial> tenantFinancials) {
        StringBuilder sb = new StringBuilder();

        for (SummaryPotentialTenantFinancial summaryFinancial : tenantFinancials) {
            sb.append(print(summaryFinancial.tenantFinancial()));
        }

        return sb.toString();
    }

    public static String print(PotentialTenantFinancial financial) {
        StringBuilder sb = new StringBuilder();

        if (financial == null) {
            sb.append("No financial data\n");
            return sb.toString();
        }

        sb.append("\nFinancial Info\n");

        sb.append("Incomes\n");
        for (TenantIncome income : financial.incomes()) {
            sb.append("\t");
            sb.append(income.incomeSource().getValue());
            sb.append(" $");
            // sb.append(income.monthlyAmount().getValue());

            //            loadEmployer(income.employer(), sb);

            //sb.append(" Active: ").append(income.active().getValue());

            sb.append("\n");

        }

        sb.append("Assets\n");
        for (TenantAsset asset : financial.assets()) {
            sb.append("\t");
            sb.append(asset.assetType().getValue());
            sb.append(" $");
            sb.append(asset.assetValue().getValue());
            sb.append("\n");
        }

        sb.append("Guarantor\n");
        for (TenantGuarantor guarantor : financial.guarantors()) {
            sb.append("\t");
            sb.append(guarantor.relationship().getValue());
            sb.append(", ");
            sb.append(guarantor.firstName().getStringView());
            sb.append(" ");
            sb.append(guarantor.lastName().getStringView());
            sb.append("\n");
        }

        sb.append("\n\n");

        return sb.toString();
    }

    public static String print(Pets pets) {
        StringBuilder sb = new StringBuilder();
        sb.append("Pets\n");

        for (Pet pet : pets.pets()) {
            sb.append("\t");
            sb.append(pet.type().getValue());

            sb.append(" \t");
            sb.append(pet.name().getStringView());

            sb.append(" \t");
            sb.append(pet.color().getStringView());

            sb.append(" \t");
            sb.append(pet.breed().getStringView());

            sb.append(" \t");
            sb.append(pet.weight().getValue()).append(" ").append(pet.weightUnit().getValue());

            sb.append(" $");
            sb.append(pet.chargeLine().charge().amount().getValue());

            sb.append("\n");
        }
        return sb.toString();
    }

    public static String print(User user) {
        StringBuilder sb = new StringBuilder();

        sb.append(user);

        return sb.toString();
    }

    public static String print(Application application) {
        StringBuilder sb = new StringBuilder();
        sb.append("Application :").append(application.rent().amount()).append("\n");
        sb.append("User: ").append(application.user()).append("\n");
        return sb.toString();
    }

    public static String print(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(address.unitNumber().getValue());
        sb.append(" ");
        sb.append(address.streetNumber().getValue());
        sb.append(" ");
        sb.append(address.streetName().getValue());
        sb.append(" ");
        sb.append(address.streetType().getStringView());
        sb.append(", ");
        sb.append(address.streetDirection().getStringView());
        sb.append(", ").append(address.city().getStringView());
        sb.append(", ").append(address.province().getStringView());
        sb.append(" ").append(address.postalCode().getStringView());

        sb.append("\n");
        sb.append(address.moveInDate().getStringView()).append(" - ");
        sb.append(address.moveOutDate().getStringView());
        sb.append(" $").append(address.payment().getValue());

        return sb.toString();
    }

    public static String print(UnitSelection unitSelection) {
        StringBuilder sb = new StringBuilder();
        sb.append("Criteria\n\t");
        sb.append(unitSelection.selectionCriteria());
        sb.append("\n\n");

        sb.append(unitSelection.availableUnits().units().size());
        sb.append(" available units\n");
        for (ApartmentUnit unit : unitSelection.availableUnits().units()) {
            sb.append("\t");
            //            sb.append(unit.suiteNumber().getStringView());
            //            sb.append(" ");
            sb.append(unit.bedrooms().getValue()).append(" beds, ");
            sb.append(unit.bathrooms().getValue()).append(" baths,");
            sb.append(" ");
            sb.append(unit.area().getValue()).append(", sq ft");

            sb.append(" available on ");
            sb.append(unit.avalableForRent().getStringView());

            //            log.info("Available {}", unit.building());

            //sb.append(", status: ").append(unit.status().getStringView());

            sb.append("\n");

            // show rent
            for (MarketRent rent : unit.marketRent()) {
                sb.append("\t\t");
                sb.append(rent.leaseTerm().getValue()).append(" months $");
                sb.append(rent.rent().amount().getValue()).append("");
                sb.append("\n");
            }

            sb.append("\t\tDeposit: $").append(unit.requiredDeposit().getValue()).append("\n");
        }

        return sb.toString();
    }

    public static String print(Charges charges) {
        StringBuilder sb = new StringBuilder();
        sb.append("Monthly\n");
        for (ChargeLine line : charges.monthlyCharges().charges()) {
            sb.append("\t$");
            sb.append(line.charge().amount().getStringView());
            sb.append(" \t");
            sb.append(line.type().getStringView());
            sb.append("\n");
        }

        sb.append("Upgrades\n");
        for (ChargeLineSelectable line : charges.monthlyCharges().upgradeCharges()) {
            sb.append("\t$");
            sb.append(line.charge().amount().getStringView());
            sb.append(" \t");
            sb.append(line.type().getStringView());
            if (line.selected().getValue()) {
                sb.append(" YES");
            }
            sb.append("\n");
        }

        sb.append("Monthly + Upgrades Total \n\t$");
        sb.append(charges.monthlyCharges().total().amount().getStringView());
        sb.append("\n");

        sb.append("\nPro-Rated ").append(charges.proRatedCharges().total().amount().getStringView()).append("\n");
        for (ChargeLine line : charges.proRatedCharges().charges()) {
            sb.append("\t$");
            sb.append(line.charge().amount().getStringView());
            sb.append(" \t").append(line.label().getStringView());
            sb.append("\n");
        }

        sb.append("\nApplication Charges ").append(charges.applicationCharges().total().amount().getStringView()).append("\n");
        for (ChargeLine line : charges.applicationCharges().charges()) {
            sb.append("\t$");
            sb.append(line.charge().amount().getStringView());
            sb.append(" \t");
            sb.append(line.type().getStringView());
            sb.append("\n");
        }

        sb.append("\nTenants Payment Split ").append(charges.paymentSplitCharges().total().amount().getStringView()).append("\n");
        for (TenantCharge line : charges.paymentSplitCharges().charges()) {
            sb.append("\t").append(line.tenant().relationship().getStringView());
            sb.append(" ").append(line.tenant().firstName().getStringView()).append(" ").append(line.tenant().lastName().getStringView());
            sb.append(" \t").append(line.percentage().getValue()).append("% $");
            sb.append(line.charge().amount().getValue());
            sb.append("\n");
        }

        //            sb.append("\t").append(charges.monthlyCharges()).append("\n");
        //            sb.append("\t").append(charges.proRatedCharges()).append("\n");
        //            sb.append("\t").append(charges.applicationCharges()).append("\n");
        //            sb.append("\t").append(charges.paymentSplitCharges()).append("\n");
        sb.append("\n");

        return sb.toString();
    }

}
