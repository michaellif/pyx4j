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
package com.propertyvista.biz.financial.preload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.tax.Tax;

public class TaxesDataModel {

    private final List<Tax> taxes;

    private final LocationsDataModel locationsDataModel;

    public TaxesDataModel(LocationsDataModel locationsDataModel) {
        this.locationsDataModel = locationsDataModel;
        taxes = new ArrayList<Tax>();
    }

    public void generate(boolean persist) {
        generateTax("HST", "BC", new BigDecimal("0.12"), false);

        generateTax("GST", "AB", new BigDecimal("0.05"), false);

        generateTax("GST", "SK", new BigDecimal("0.05"), false);
        generateTax("PST", "SK", new BigDecimal("0.05"), false);

        generateTax("GST", "MB", new BigDecimal("0.05"), false);
        generateTax("PST", "MB", new BigDecimal("0.07"), false);

        generateTax("HST", "ON", new BigDecimal("0.05"), false);

        generateTax("QST", "QC", new BigDecimal("0.05"), false);
        generateTax("PST", "QC", new BigDecimal("0.095"), false);

        generateTax("HST", "NL", new BigDecimal("0.13"), false);

        generateTax("HST", "NS", new BigDecimal("0.15"), false);

        generateTax("HST", "NB", new BigDecimal("0.13"), false);

        generateTax("GST", "PE", new BigDecimal("0.05"), false);
        generateTax("PST", "PE", new BigDecimal("0.1"), false);

        generateTax("GST", "NT", new BigDecimal("0.05"), false);

        generateTax("GST", "NU", new BigDecimal("0.05"), false);

        generateTax("GST", "YT", new BigDecimal("0.05"), false);

        if (persist) {
            Persistence.service().persist(taxes);
        }
    }

    private void generateTax(String name, String authority, BigDecimal rate, Boolean compound) {
        Tax tax = EntityFactory.create(Tax.class);
        tax.name().setValue(name);
        tax.authority().setValue(authority);
        tax.policyNode().set(locationsDataModel.provincesMap.get(authority));
        tax.rate().setValue(rate);
        tax.compound().setValue(compound);
        taxes.add(tax);
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

}
