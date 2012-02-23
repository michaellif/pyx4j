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
package com.propertyvista.server.billing.preload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.ChargeCode;
import com.propertyvista.domain.financial.tax.Tax;

public class ChargeCodesDataModel {

    private final List<ChargeCode> chargeCodes;

    private final TaxesDataModel taxesDataModel;

    public ChargeCodesDataModel(TaxesDataModel taxesDataModel) {
        this.taxesDataModel = taxesDataModel;
        chargeCodes = new ArrayList<ChargeCode>();
    }

    public void generate(boolean persist) {
        generateChargeCode("CC1", Arrays.asList(new Tax[] { taxesDataModel.getTaxes().get(0) }));
        Persistence.service().persist(chargeCodes);
    }

    private void generateChargeCode(String name, List<Tax> taxes) {
        ChargeCode chargeCode = EntityFactory.create(ChargeCode.class);
        chargeCode.name().setValue(name);
        chargeCode.taxes().addAll(taxes);
        chargeCodes.add(chargeCode);
    }

    public List<ChargeCode> getChargeCodes() {
        return chargeCodes;
    }

}
