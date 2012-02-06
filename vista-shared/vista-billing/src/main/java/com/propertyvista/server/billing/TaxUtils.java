/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxUtils {

    public static List<BillChargeTax> calculateTaxes(final BigDecimal baseAmount, List<Tax> taxes) {
        List<BillChargeTax> chargeTaxes = new ArrayList<BillChargeTax>();
        BigDecimal interimAmount = baseAmount;
        for (Tax tax : taxes) {
            if (!tax.compound().getValue()) {
                BillChargeTax chargeTax = EntityFactory.create(BillChargeTax.class);
                chargeTax.tax().set(tax);
                chargeTax.amount().setValue(baseAmount.multiply(new BigDecimal(tax.rate().getValue())).setScale(2, RoundingMode.HALF_UP));
                chargeTaxes.add(chargeTax);
                interimAmount = interimAmount.add(chargeTax.amount().getValue());
            }
        }
        for (Tax tax : taxes) {
            if (tax.compound().getValue()) {
                BillChargeTax chargeTax = EntityFactory.create(BillChargeTax.class);
                chargeTax.tax().set(tax);
                chargeTax.amount().setValue(interimAmount.multiply(new BigDecimal(tax.rate().getValue())).setScale(2, RoundingMode.HALF_UP));
                chargeTaxes.add(chargeTax);
                interimAmount = interimAmount.add(chargeTax.amount().getValue());
            }
        }
        return chargeTaxes;
    }

}
