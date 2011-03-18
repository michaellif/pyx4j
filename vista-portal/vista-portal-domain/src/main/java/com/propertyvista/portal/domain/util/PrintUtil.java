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

import com.propertyvista.portal.domain.AddOn;
import com.propertyvista.portal.domain.Amenity;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Concession;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.UnitInfoItem;
import com.propertyvista.portal.domain.Utility;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.pt.UnitSelection;

public class PrintUtil {

    public static String print(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(address.street1().getValue());
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
        for (ApptUnit unit : unitSelection.availableUnits().units()) {
            sb.append("\t");
            sb.append(unit.suiteNumber().getStringView());
            sb.append(" ");
            sb.append(unit.bedrooms().getValue()).append(" beds, ");
            sb.append(unit.bathrooms().getValue()).append(" baths,");
            sb.append(" ");
            sb.append(unit.area().getValue()).append(", sq ft");

            sb.append(" available on ");
            sb.append(unit.avalableForRent().getStringView());

//            log.info("Available {}", unit.building());

            sb.append(", status: ").append(unit.status().getStringView());

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

        // selected unit
        ApptUnit unit = unitSelection.selectedUnit();
        sb.append("\n\n");
        sb.append("Selected: ").append(unit.suiteNumber().getStringView());
        sb.append("\n");

        // building
        Building building = unit.building();
        sb.append("Building: ").append(building).append("\n");
        sb.append("Property: ").append(building.propertyProfile()).append("\n");

        // amenities
        sb.append("\tAmenities:\n");
        for (Amenity amenity : unit.amenities()) {
            sb.append("\t\t");
            sb.append(amenity.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tUtilities:\n");
        for (Utility utility : unit.utilities()) {
            sb.append("\t\t");
            sb.append(utility.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tUnitInfoItem:\n");
        for (UnitInfoItem info : unit.infoDetails()) {
            sb.append("\t\t");
            sb.append(info.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tConcessions:\n");
        for (Concession concession : unit.concessions()) {
            sb.append("\t\t");
            sb.append(concession.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tAdd-ons:\n");
        for (AddOn addOn : unit.addOns()) {
            sb.append("\t\t");
            sb.append(addOn.name().getStringView());
            sb.append(" $").append(addOn.monthlyCost().getValue());
            sb.append("\n");
        }

        // rent
        sb.append("\nStart rent:").append(unitSelection.rentStart().getStringView());
        sb.append(", Lease: ").append(unitSelection.markerRent().leaseTerm().getValue()).append(" months, $");
        sb.append(unitSelection.markerRent().rent().amount().getValue());

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
