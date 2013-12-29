/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.generator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.financial.tax.Tax;

public class TaxGenerator {

    public static Tax createTax(int i) {
        Tax tax = EntityFactory.create(Tax.class);
        Random randomGenerator = new Random();
        DecimalFormat format = new DecimalFormat("#.##");

        tax.authority().setValue("Authority #" + i);
        tax.name().setValue("Tax #" + i);
        tax.rate().setValue(new BigDecimal(randomGenerator.nextDouble() * 10));
        tax.compound().setValue(randomGenerator.nextBoolean());

        return tax;
    }
}
