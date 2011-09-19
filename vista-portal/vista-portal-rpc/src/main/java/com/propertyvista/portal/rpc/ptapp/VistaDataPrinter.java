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
package com.propertyvista.portal.rpc.ptapp;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.Pet;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.User;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLineSelectable;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.dto.VehiclesDTO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryTenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;

public class VistaDataPrinter {

    public static String print(SummaryDTO summary) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n---------------------------- APPLICATION -----------------------------\n");
        sb.append(print(summary.application()));

        //        loadApplication(sb);
        //        loadApplicationProgress(sb);
        //
        sb.append("\n\n---------------------------- UNIT SELECTION --------------------------\n");
        sb.append(print(summary.selectedUnit()));

        sb.append("\n\n---------------------------- TENANTS ---------------------------------\n");
        sb.append(print(summary.tenantList()));

        sb.append("\n\n---------------------------- FINANCIALS ------------------------------\n");
        sb.append(printFinancial(summary.tenantFinancials()));

        sb.append("\n\n---------------------------- PETS ------------------------------------\n");
        sb.append(print(summary.addons().pets()));

        sb.append("\n\n---------------------------- VEHICLES --------------------------------\n");
        sb.append(printVehicles(summary.addons().vehicles()));

        sb.append("\n\n---------------------------- CHARGES ---------------------------------\n");
        sb.append(print(summary.charges()));

        sb.append("\n\n\n");

        return sb.toString();
    }

    public static String print(TenantInLeaseListDTO tenantList) {
        StringBuilder sb = new StringBuilder();

        sb.append(tenantList.tenants().size()).append(" potential tenants");
        sb.append("\n");

        for (TenantInLease tenant : tenantList.tenants()) {

            sb.append("\n--- tenant ---\n");
            sb.append(print(tenant));
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String print(TenantInLease tenant) {
        StringBuilder sb = new StringBuilder();

        sb.append(tenant.status().getStringView());
        sb.append(", ");

        sb.append(tenant.tenant().person().name().firstName().getStringView());
        sb.append(" ");
        if (tenant.tenant().person().name().middleName().getStringView().length() > 0) {
            sb.append(tenant.tenant().person().name().middleName().getStringView());
            sb.append(" ");
        }
        sb.append(tenant.tenant().person().name().lastName().getStringView());

        sb.append("\t\t Born on ");
        sb.append(tenant.tenant().person().birthDate().getValue());

        sb.append("\t");
        sb.append(tenant.tenant().person().homePhone().getStringView()).append(" | ").append(tenant.tenant().person().mobilePhone().getStringView());

        sb.append("\t");
        sb.append(tenant.tenant().person().email().getStringView());

        sb.append("\n\t");

//        sb.append(tenant.driversLicense().getStringView()).append(" ").append(tenant.driversLicenseState().getStringView());
//
//        sb.append("\t").append(tenant.secureIdentifier().getStringView());
//
//        sb.append("Current address");
//        sb.append(VistaDataPrinter.print(tenant.currentAddress()));
//        sb.append("Previous address");
//        sb.append(VistaDataPrinter.print(tenant.previousAddress()));

        sb.append("\nVehicles\n");

        return sb.toString();
    }

    public static String printFinancial(IList<SummaryTenantFinancialDTO> tenantFinancials) {
        StringBuilder sb = new StringBuilder();

        for (SummaryTenantFinancialDTO summaryFinancial : tenantFinancials) {
            sb.append(print(summaryFinancial.tenantFinancial()));
        }

        return sb.toString();
    }

    public static String print(TenantFinancialDTO financial) {
        StringBuilder sb = new StringBuilder();

        if (financial == null) {
            sb.append("No financial data\n");
            return sb.toString();
        }

        sb.append("\nFinancial Info\n");

        sb.append("Incomes\n");
        for (PersonalIncome income : financial.incomes()) {
            sb.append("\t");
            sb.append(income.incomeSource().getValue());
            sb.append(" $");
            // sb.append(income.monthlyAmount().getValue());

            //            loadEmployer(income.employer(), sb);

            //sb.append(" Active: ").append(income.active().getValue());

            sb.append("\n");

        }

        sb.append("Assets\n");
        for (PersonalAsset asset : financial.assets()) {
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
            sb.append(guarantor.name().firstName().getStringView());
            sb.append(" ");
            sb.append(guarantor.name().lastName().getStringView());
            sb.append("\n");
        }

        sb.append("\n\n");

        return sb.toString();
    }

    public static String print(PetsDTO pets) {
        StringBuilder sb = new StringBuilder();
        sb.append("Pets\n");

        for (Pet pet : pets.list()) {
            sb.append("\n\t");
            sb.append(pet.type().getValue()).append(" ");
            sb.append(pet.name().getStringView()).append(" ");
            sb.append(pet.color().getStringView()).append(" ");
            sb.append(pet.breed().getStringView()).append(" ");
            sb.append(pet.weight().getValue()).append(" ").append(pet.weightUnit().getValue()).append(" $");
            sb.append(pet.chargeLine().charge().amount().getValue());
        }
        return sb.toString();
    }

    public static String printVehicles(VehiclesDTO vehicles) {
        StringBuilder sb = new StringBuilder();
        sb.append("vehicles\n");

        for (Vehicle vehicle : vehicles.list()) {
            sb.append("\n\t");
            sb.append(vehicle.year().getStringView()).append(" ");
            sb.append(vehicle.province().getStringView()).append(" ");
            sb.append(vehicle.make().getStringView()).append(" ").append(vehicle.model().getStringView()).append(" ");
            sb.append(vehicle.plateNumber().getStringView());
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
        sb.append("Application :").append("\n");
        sb.append("User: ").append(application.user()).append("\n");
        return sb.toString();
    }

    public static String print(PriorAddress address) {
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

    public static String print(ApartmentInfoDTO selectedUnit) {
        StringBuilder sb = new StringBuilder();
        sb.append(selectedUnit.suiteNumber().getValue());
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
            sb.append(" ").append(line.tenant().tenant().person().name().firstName().getStringView()).append(" ")
                    .append(line.tenant().tenant().person().name().lastName().getStringView());
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
