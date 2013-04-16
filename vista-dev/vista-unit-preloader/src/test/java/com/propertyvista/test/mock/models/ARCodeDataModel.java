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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.test.mock.MockDataModel;

public class ARCodeDataModel extends MockDataModel<ARCode> {

    public ARCodeDataModel() {
    }

    @Override
    protected void generate() {

        generateARCode("Regular Residential Unit", ARCode.Type.Residential, 5110, 1, true, new String[] { "rrent" });
        generateARCode("Ocean View Residential Unit", ARCode.Type.Residential, 5110, 1, false);
        generateARCode("Regular Short Term Residential Unit", ARCode.Type.ResidentialShortTerm, 5110, 1, false);
        generateARCode("Regular Commercial Unit", ARCode.Type.Commercial, 5110, 1, false);

        generateARCode("Outdoor Parking", ARCode.Type.Parking, 5110, 1, false, new String[] { "routpark" });
        generateARCode("Indoor Parking", ARCode.Type.Parking, 5110, 1, false, new String[] { "rinpark" });
        generateARCode("Cat", ARCode.Type.Pet, 5930, 1, false);
        generateARCode("Dog", ARCode.Type.Pet, 5930, 1, false);
        generateARCode("Small Locker", ARCode.Type.Locker, 5110, 1, false);
        generateARCode("Medium Locker", ARCode.Type.Locker, 5110, 1, false);
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
        generateARCode("Booking", ARCode.Type.OneTime, 5934, 1, false);
        generateARCode("Legal Charge", ARCode.Type.AccountCharge, 0, 1, false, new String[] { "rlegal" });
        generateARCode("Superintendent", ARCode.Type.AccountCredit, 0, 1, false, new String[] { "rsuper" });

        generateARCode("Unknown External Credit", ARCode.Type.ExternalCredit, 0, 1, false);
        generateARCode("Unknown External Charge", ARCode.Type.ExternalCharge, 0, 1, false);

        generateARCode("Deposit", ARCode.Type.Deposit, 0, 1, true);
        generateARCode("LatePayment", ARCode.Type.LatePayment, 0, 1, true);
        generateARCode("NSF", ARCode.Type.NSF, 0, 1, true, new String[] { "rnsffee" });

        generateARCode("Account Credit", ARCode.Type.AccountCredit, 0, 1, true);
        generateARCode("Account Charge", ARCode.Type.AccountCharge, 0, 1, true);

        generateARCode("Carry Forward Charge", ARCode.Type.CarryForwardCharge, 0, 1, true);

        //rlmrint rintpay rlmr rbaddebt rsuper rfree
    }

    private void generateARCode(String name, ARCode.Type codeType, int glCodeId, int glCategoryId, boolean reserved) {
        generateARCode(name, codeType, glCodeId, glCategoryId, reserved, null);
    }

    private void generateARCode(String name, ARCode.Type codeType, int glCodeId, int glCategoryId, boolean reserved, String[] yardiChargeCodes) {

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

    }

}
