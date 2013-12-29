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
package com.propertyvista.generator;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.shared.config.VistaFeatures;

//TODO move to Proper place
public class ARCodesGenerator {

    private final List<ARCode> codes = new ArrayList<ARCode>();

    public ARCodesGenerator() {
        for (ARCode.Type type : ARCode.Type.services()) {
            createARCode(type.toString(), type, 5110, false);
        }

        createARCode("Regular Parking", ARCode.Type.Parking, 5110, false);
        createARCode("Wide Parking", ARCode.Type.Parking, 5110, false);
        createARCode("Narrow Parking", ARCode.Type.Parking, 5110, false);
        createARCode("Disabled Parking", ARCode.Type.Parking, 5110, false);
        createARCode("Cat", ARCode.Type.Pet, 5930, false);
        createARCode("Dog", ARCode.Type.Pet, 5930, false);
        createARCode("Small Locker", ARCode.Type.Locker, 5110, false);
        createARCode("Medium Locker", ARCode.Type.Locker, 5110, false);
        createARCode("Large Locker", ARCode.Type.Locker, 5110, false);
        createARCode("Fitness", ARCode.Type.AddOn, 5110, false);
        createARCode("Pool", ARCode.Type.AddOn, 5110, false);
        createARCode("Furnished", ARCode.Type.AddOn, 5110, false);
        createARCode("Key", ARCode.Type.OneTime, 6240, false);
        createARCode("Access Card", ARCode.Type.OneTime, 6240, false);
        createARCode("Cable", ARCode.Type.AddOn, 5110, false);
        createARCode("Water", ARCode.Type.Utility, 5999, false);
        createARCode("Gas", ARCode.Type.Utility, 5997, false);
        createARCode("Hydro", ARCode.Type.Utility, 5998, false);
        createARCode("Booking", ARCode.Type.OneTime, 5934, false);

        // TODO define correct GL Code for these unknown external charge and credit
        createARCode("Unknown External Credit", ARCode.Type.ExternalCredit, 0, true);
        createARCode("Unknown External Charge", ARCode.Type.ExternalCharge, 0, true);

        // reserved codes:
        createARCode("Deposit", ARCode.Type.Deposit, 0, true);
        createARCode("DepositRefund", ARCode.Type.DepositRefund, 0, true);
        createARCode("LatePayment", ARCode.Type.LatePayment, 0, true);
        createARCode("NSF", ARCode.Type.NSF, 0, true);

        createARCode("Payment", ARCode.Type.Payment, 0, true);

        createARCode("Carry Forward Credit", ARCode.Type.CarryForwardCredit, 0, true);
        createARCode("Carry Forward Charge", ARCode.Type.CarryForwardCharge, 0, true);

        // create Yardi rrent code if necessary:
        if (VistaFeatures.instance().yardiIntegration()) {
            ARCode code = createARCode("Yardi Residential Rent", ARCode.Type.Residential, 0, false);
            YardiChargeCode yardiCode = EntityFactory.create(YardiChargeCode.class);
            yardiCode.yardiChargeCode().setValue("rrent");
            code.yardiChargeCodes().add(yardiCode);
        }
    }

    public List<ARCode> getARCodes() {
        return codes;
    }

    private ARCode createARCode(String name, ARCode.Type type, int glCode, boolean reserved) {
        ARCode code = EntityFactory.create(ARCode.class);

        code.name().setValue(name);
        code.type().setValue(type);
        code.glCode().codeId().setValue(glCode);
        code.reserved().setValue(reserved);

        codes.add(code);
        return code;
    }
}
