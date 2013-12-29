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

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.test.mock.MockDataModel;

public class TaxesDataModel extends MockDataModel<Tax> {

    public TaxesDataModel() {
    }

    @Override
    protected void generate() {

        addItem(generateTax("HST", "BC", new BigDecimal("0.12"), false));

        addItem(generateTax("GST", "AB", new BigDecimal("0.05"), false));

        addItem(generateTax("GST", "SK", new BigDecimal("0.05"), false));
        addItem(generateTax("PST", "SK", new BigDecimal("0.05"), false));

        addItem(generateTax("GST", "MB", new BigDecimal("0.05"), false));
        addItem(generateTax("PST", "MB", new BigDecimal("0.07"), false));

        addItem(generateTax("HST", "ON", new BigDecimal("0.05"), false));

        addItem(generateTax("QST", "QC", new BigDecimal("0.05"), false));
        addItem(generateTax("PST", "QC", new BigDecimal("0.095"), false));

        addItem(generateTax("HST", "NL", new BigDecimal("0.13"), false));

        addItem(generateTax("HST", "NS", new BigDecimal("0.15"), false));

        addItem(generateTax("HST", "NB", new BigDecimal("0.13"), false));

        addItem(generateTax("GST", "PE", new BigDecimal("0.05"), false));
        addItem(generateTax("PST", "PE", new BigDecimal("0.1"), false));

        addItem(generateTax("GST", "NT", new BigDecimal("0.05"), false));

        addItem(generateTax("GST", "NU", new BigDecimal("0.05"), false));

        addItem(generateTax("GST", "YT", new BigDecimal("0.05"), false));

        Persistence.service().persist(getAllItems());

    }

    private Tax generateTax(String name, String authority, BigDecimal rate, Boolean compound) {
        Tax tax = EntityFactory.create(Tax.class);
        tax.name().setValue(name);
        tax.authority().setValue(authority);
        tax.policyNode().set(getDataModel(LocationsDataModel.class).provincesMap.get(authority));
        tax.rate().setValue(rate);
        tax.compound().setValue(compound);
        return tax;
    }

}
