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

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.test.mock.MockDataModel;

public class ARCodeDataModel extends MockDataModel<ARCode> {

    public static enum Code {
        rent, outdoorParking, indoorParking, smallLocker, largeLocker, catRent, dogRent, booking, legalFee, superintendentDiscount, nsf;
    }

    private final Map<Code, ARCode> codeMap;

    public ARCodeDataModel() {
        codeMap = new HashMap<Code, ARCode>();
    }

    @Override
    protected void generate() {

        ARCode code = generateARCode("Regular Residential Unit", ARCode.Type.Residential, 5110, 1, true, new String[] { "rrent" });
        codeMap.put(Code.rent, code);

        generateARCode("Ocean View Residential Unit", ARCode.Type.Residential, 5110, 1, false);
        generateARCode("Regular Short Term Residential Unit", ARCode.Type.ResidentialShortTerm, 5110, 1, false);
        generateARCode("Regular Commercial Unit", ARCode.Type.Commercial, 5110, 1, false);

        code = generateARCode("Outdoor Parking", ARCode.Type.Parking, 5110, 1, false, new String[] { "routpark" });
        codeMap.put(Code.outdoorParking, code);
        code = generateARCode("Indoor Parking", ARCode.Type.Parking, 5110, 1, false, new String[] { "rinpark" });
        codeMap.put(Code.indoorParking, code);

        code = generateARCode("Cat", ARCode.Type.Pet, 5930, 1, false);
        codeMap.put(Code.catRent, code);

        code = generateARCode("Dog", ARCode.Type.Pet, 5930, 1, false);
        codeMap.put(Code.dogRent, code);

        code = generateARCode("Small Locker", ARCode.Type.Locker, 5110, 1, false, new String[] { "rslocker" });
        codeMap.put(Code.smallLocker, code);

        code = generateARCode("Medium Locker", ARCode.Type.Locker, 5110, 1, false, new String[] { "rmlocker" });
        codeMap.put(Code.largeLocker, code);

        generateARCode("Large Locker", ARCode.Type.Locker, 5110, 1, false);
        generateARCode("Fitness", ARCode.Type.AddOn, 5110, 1, false);
        generateARCode("Pool", ARCode.Type.AddOn, 5110, 1, false);
        generateARCode("Furnished", ARCode.Type.AddOn, 6110, 1, false);
        generateARCode("Key", ARCode.Type.AddOn, 6240, 1, false);
        generateARCode("Access Card", ARCode.Type.AddOn, 6240, 1, false);
        generateARCode("Cable", ARCode.Type.AddOn, 5110, 1, false);
        generateARCode("Water", ARCode.Type.Utility, 5999, 1, false);
        generateARCode("Gas", ARCode.Type.Utility, 5997, 1, false);
        generateARCode("Hydro", ARCode.Type.Utility, 5998, 1, false);

        code = generateARCode("Booking", ARCode.Type.OneTime, 5934, 1, false);
        codeMap.put(Code.booking, code);

        code = generateARCode("Legal Charge", ARCode.Type.AccountCharge, 0, 1, false, new String[] { "rlegal" });
        codeMap.put(Code.legalFee, code);

        code = generateARCode("Superintendent", ARCode.Type.AccountCredit, 0, 1, false, new String[] { "rsuper" });
        codeMap.put(Code.superintendentDiscount, code);

        // for default ar codes reserved() must be true here
        generateARCode("Unknown External Credit", ARCode.Type.ExternalCredit, 0, 1, true);
        generateARCode("Unknown External Charge", ARCode.Type.ExternalCharge, 0, 1, true);

        generateARCode("Deposit", ARCode.Type.Deposit, 0, 1, true);
        generateARCode("LatePayment", ARCode.Type.LatePayment, 0, 1, true);

        code = generateARCode("NSF", ARCode.Type.NSF, 0, 1, true, new String[] { "rnsffee" });
        codeMap.put(Code.nsf, code);

        generateARCode("Account Credit", ARCode.Type.AccountCredit, 0, 1, true);
        generateARCode("Account Charge", ARCode.Type.AccountCharge, 0, 1, true);

        generateARCode("Carry Forward Charge", ARCode.Type.CarryForwardCredit, 0, 1, true);
        generateARCode("Carry Forward Charge", ARCode.Type.CarryForwardCharge, 0, 1, true);

        generateARCode("Payment", ARCode.Type.Payment, 0, 1, true);

        //rlmrint rintpay rlmr rbaddebt rsuper rfree
    }

    public ARCode getARCode(Code code) {
        return codeMap.get(code);
    }

    private ARCode generateARCode(String name, ARCode.Type codeType, int glCodeId, int glCategoryId, boolean reserved) {
        return generateARCode(name, codeType, glCodeId, glCategoryId, reserved, null);
    }

    private ARCode generateARCode(String name, ARCode.Type codeType, int glCodeId, int glCategoryId, boolean reserved, String[] yardiChargeCodes) {

        ARCode code = EntityFactory.create(ARCode.class);
        code.name().setValue(name);
        code.type().setValue(codeType);
        code.glCode().set(getDataModel(GLCodeDataModel.class).addGLCode(name, glCodeId, glCategoryId, reserved));
        code.reserved().setValue(reserved);

        if (yardiChargeCodes != null) {
            for (String string : yardiChargeCodes) {
                YardiChargeCode yardiChargeCode = EntityFactory.create(YardiChargeCode.class);
                yardiChargeCode.yardiChargeCode().setValue(string);
                code.yardiChargeCodes().add(yardiChargeCode);
            }
        }

        Persistence.service().persist(code);

        addItem(code);

        return code;

    }

}
