/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.test.mock.MockDataModel;

public class ARCodeDataModel extends MockDataModel<ARCode> {

    public ARCodeDataModel() {
    }

    @Override
    protected void generate() {

        List<ARCode> serviceItemTypes = new ArrayList<ARCode>();

        serviceItemTypes.add(generateChargeItemType("Regular Residential Unit", ARCode.Type.Residential, true));
        serviceItemTypes.add(generateChargeItemType("Ocean View Residential Unit", ARCode.Type.Residential, false));
        serviceItemTypes.add(generateChargeItemType("Regular Short Term Residential Unit", ARCode.Type.ResidentialShortTerm, false));
        serviceItemTypes.add(generateChargeItemType("Regular Commercial Unit", ARCode.Type.Commercial, false));

        serviceItemTypes.add(generateChargeItemType("Regular Parking", ARCode.Type.Parking, false));
        serviceItemTypes.add(generateChargeItemType("Wide Parking", ARCode.Type.Parking, false));
        serviceItemTypes.add(generateChargeItemType("Narrow Parking", ARCode.Type.Parking, false));
        serviceItemTypes.add(generateChargeItemType("Disabled Parking", ARCode.Type.Parking, false));
        serviceItemTypes.add(generateChargeItemType("Cat", ARCode.Type.Pet, false));
        serviceItemTypes.add(generateChargeItemType("Dog", ARCode.Type.Pet, false));
        serviceItemTypes.add(generateChargeItemType("Small Locker", ARCode.Type.Locker, false));
        serviceItemTypes.add(generateChargeItemType("Medium Locker", ARCode.Type.Locker, false));
        serviceItemTypes.add(generateChargeItemType("Large Locker", ARCode.Type.Locker, false));
        serviceItemTypes.add(generateChargeItemType("Fitness", ARCode.Type.AddOn, false));
        serviceItemTypes.add(generateChargeItemType("Pool", ARCode.Type.AddOn, false));
        serviceItemTypes.add(generateChargeItemType("Furnished", ARCode.Type.AddOn, false));
        serviceItemTypes.add(generateChargeItemType("Key", ARCode.Type.AddOn, false));
        serviceItemTypes.add(generateChargeItemType("Access Card", ARCode.Type.AddOn, false));
        serviceItemTypes.add(generateChargeItemType("Cable", ARCode.Type.AddOn, false));
        serviceItemTypes.add(generateChargeItemType("Water", ARCode.Type.Utility, false));
        serviceItemTypes.add(generateChargeItemType("Gas", ARCode.Type.Utility, false));
        serviceItemTypes.add(generateChargeItemType("Hydro", ARCode.Type.Utility, false));
        serviceItemTypes.add(generateChargeItemType("Booking", ARCode.Type.OneTime, false));

        serviceItemTypes.add(generateChargeItemType("Deposit", ARCode.Type.Deposit, true));
        serviceItemTypes.add(generateChargeItemType("LatePayment", ARCode.Type.LatePayment, true));
        serviceItemTypes.add(generateChargeItemType("NSF", ARCode.Type.NSF, true));

        serviceItemTypes.add(generateChargeItemType("Account Credit", ARCode.Type.AccountCredit, true));
        serviceItemTypes.add(generateChargeItemType("Account Charge", ARCode.Type.AccountCharge, true));

        serviceItemTypes.add(generateChargeItemType("Carry Forward Charge", ARCode.Type.CarryForwardCharge, true));

        Persistence.service().persist(serviceItemTypes);
    }

    private ARCode generateChargeItemType(String name, ARCode.Type serviceType, boolean defaultCode) {
        ARCode type = EntityFactory.create(ARCode.class);
        type.name().setValue(name);
        type.type().setValue(serviceType);
        type.type().setValue(serviceType);
        type.defaultCode().setValue(defaultCode);
        addItem(type);
        return type;
    }

}
