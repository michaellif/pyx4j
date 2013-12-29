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
package com.propertyvista.biz.financial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.InvoiceChargeTax;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxCalculationTest extends VistaDBTestBase {

    public void testRegularTaxCalculation() {
        List<Tax> taxes = new ArrayList<Tax>();

        Tax tax = EntityFactory.create(Tax.class);
        tax.rate().setValue(new BigDecimal("0.07"));
        tax.name().setValue("GST");
        tax.compound().setValue(false);
        taxes.add(tax);

        tax = EntityFactory.create(Tax.class);
        tax.rate().setValue(new BigDecimal("0.05"));
        tax.name().setValue("PST");
        tax.compound().setValue(false);
        taxes.add(tax);

        List<InvoiceChargeTax> chargeTaxes = TaxUtils.calculateTaxes(new BigDecimal("100.00"), taxes);

        assertEquals(new BigDecimal("7.00"), chargeTaxes.get(0).amount().getValue());
        assertEquals(new BigDecimal("5.00"), chargeTaxes.get(1).amount().getValue());
    }

    public void testCompaundTaxCalculation() {
        List<Tax> taxes1 = new ArrayList<Tax>();
        List<Tax> taxes2 = new ArrayList<Tax>();

        Tax gst = EntityFactory.create(Tax.class);
        gst.rate().setValue(new BigDecimal("0.1"));
        gst.name().setValue("GST");
        gst.compound().setValue(false);

        Tax pst = EntityFactory.create(Tax.class);
        pst.rate().setValue(new BigDecimal("0.05"));
        pst.name().setValue("PST");
        pst.compound().setValue(true);

        taxes1.add(gst);
        taxes1.add(pst);

        taxes2.add(pst);
        taxes2.add(gst);

        List<InvoiceChargeTax> chargeTaxes = TaxUtils.calculateTaxes(new BigDecimal("100.04"), taxes1);

        assertEquals(new BigDecimal("10.00"), chargeTaxes.get(0).amount().getValue());
        assertEquals("GST", chargeTaxes.get(0).tax().name().getValue());
        assertEquals(new BigDecimal("5.50"), chargeTaxes.get(1).amount().getValue());
        assertEquals("PST", chargeTaxes.get(1).tax().name().getValue());

        chargeTaxes = TaxUtils.calculateTaxes(new BigDecimal("100.04"), taxes2);

        assertEquals(new BigDecimal("10.00"), chargeTaxes.get(0).amount().getValue());
        assertEquals("GST", chargeTaxes.get(0).tax().name().getValue());
        assertEquals(new BigDecimal("5.50"), chargeTaxes.get(1).amount().getValue());
        assertEquals("PST", chargeTaxes.get(1).tax().name().getValue());

    }

    public static void main(String args[]) {
        {
            double price = 100.04;
            double tax1 = price * 0.10;
            double tax1Total = price + tax1;
            double tax2 = tax1Total * 0.05;
            double tax2Total = tax2 + tax1Total;
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            System.out.println("Price: " + currencyFormat.format(price));
            System.out.println("Tax1: " + currencyFormat.format(tax1));
            System.out.println("Total after Tax1: " + currencyFormat.format(tax1Total));
            System.out.println("Tax2: " + currencyFormat.format(tax2));
            System.out.println("Total after Tax1 and Tax2: " + currencyFormat.format(tax2Total));
            System.out.println("");
        }

        {
            BigDecimal price = new BigDecimal("100.04");
            BigDecimal discountPercent = new BigDecimal("0.10");
            BigDecimal discount = price.multiply(discountPercent);
            discount = discount.setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = price.add(discount);
            total = total.setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxPercent = new BigDecimal("0.05");
            BigDecimal tax = total.multiply(taxPercent);
            tax = tax.setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxedTotal = total.add(tax);
            taxedTotal = taxedTotal.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Price: " + price);
            System.out.println("Tax1: " + discount);
            System.out.println("Total after Tax1: " + total);
            System.out.println("Tax2: " + tax);
            System.out.println("Total after Tax1 and Tax2: " + taxedTotal);
        }
    }
}
