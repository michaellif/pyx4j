/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.pad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.interfaces.importer.model.PadProcessorInformation.PadProcessingStatus;

public class PadPercentsCalulationsTests {

    private final static Logger log = LoggerFactory.getLogger(PadPercentsCalulationsTests.class);

    private PadFileModel createModelPercent(String percent) {
        PadFileModel model = EntityFactory.create(PadFileModel.class);
        model.percent().setValue(percent);
        return model;
    }

    static void print(List<PadFileModel> leasePadEntities) {
        for (PadFileModel model : leasePadEntities) {
            print(model);
        }
    }

    static void print(PadFileModel m) {
        log.info(SimpleMessageFormat.format("account:{0} {1}% status:{2}", m.accountNumber(), m._processorInformation().percent(), m._processorInformation()
                .status()));
    }

    private void assertEquals(BigDecimal expected, BigDecimal actual) {
        Assert.assertEquals(expected.setScale(4).toString(), (actual == null) ? null : actual.toString());
    }

    @Test
    public void test_30_60() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelPercent("33.333"));
        leasePadEntities.add(createModelPercent("66.666"));
        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.3333"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.6667"), leasePadEntities.get(1)._processorInformation().percent().getValue());
    }

    private PadFileModel createModelFull(String account, String chargeCode, String percent, double estimatedCharge) {
        PadFileModel model = EntityFactory.create(PadFileModel.class);
        model.percent().setValue(percent);

        model.bankId().setValue("1");
        model.transitNumber().setValue("1");
        model.accountNumber().setValue(account);

        model.chargeCode().setValue(chargeCode);
        model.estimatedCharge().setValue(Double.toString(estimatedCharge));

        return model;
    }

    private PadFileModel createModelFullNoPap(String account, String chargeCode, String percent, double estimatedCharge) {
        PadFileModel model = createModelFull(account, chargeCode, percent, estimatedCharge);
        model.papApplicable().setValue(Boolean.FALSE);
        return model;
    }

    @Test
    public void testRent_30_60() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "33.333", 1000));
        leasePadEntities.add(createModelFull("2", "rent", "66.666", 1000));
        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.3333"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.6667"), leasePadEntities.get(1)._processorInformation().percent().getValue());
    }

    @Test
    public void testRentDiscount() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", null, 1000));
        leasePadEntities.add(createModelFull("1", "rasuper", null, -500));
        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("1.00"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(1)._processorInformation().status().getValue());
    }

    @Test
    public void testRentDiscountError() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rasuper", null, -500));
        leasePadEntities.add(createModelFull("2", "rent", null, 1000));
        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        //print(leasePadEntities);
        Assert.assertEquals(PadProcessingStatus.invalidResultingValues, leasePadEntities.get(0)._processorInformation().status().getValue());
        Assert.assertEquals(PadProcessingStatus.anotherRecordInvalid, leasePadEntities.get(1)._processorInformation().status().getValue());
    }

    @Test
    public void testRentParking_30_60() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "50", 1000));
        leasePadEntities.add(createModelFull("2", "rent", "50", 1000));

        leasePadEntities.add(createModelFull("1", "park", "100", 100));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.5455"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.4545"), leasePadEntities.get(1)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(2)._processorInformation().status().getValue());
    }

    @Test
    public void testRentNoParking() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "100", 1000));
        leasePadEntities.add(createModelFullNoPap("1", "park", null, 1000));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.50"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        Assert.assertEquals(PadProcessingStatus.notUsedForACH, leasePadEntities.get(1)._processorInformation().status().getValue());
    }

    @Test
    public void testRentParking_Merged() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "90", 1000));
        leasePadEntities.add(createModelFull("1", "park", "100", 100));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.9091"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(1)._processorInformation().status().getValue());
    }

    @Test
    public void testUninitializedChargeSplitCase1() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", null, 1000));
        leasePadEntities.add(createModelFull("2", "rent", null, 1000));
        leasePadEntities.add(createModelFull("1", "park", "100", 100));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("1.0000"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.ignoredUinitializedChargeSplit, leasePadEntities.get(1)._processorInformation().status().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(2)._processorInformation().status().getValue());
    }

    @Test
    public void testUninitializedChargeSplitCase2() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "park", "100", 100));
        leasePadEntities.add(createModelFull("2", "park", null, 100));
        leasePadEntities.add(createModelFull("1", "rent", null, 1000));
        leasePadEntities.add(createModelFull("2", "rent", null, 1000));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("1.0000"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.ignoredUinitializedChargeSplit, leasePadEntities.get(1)._processorInformation().status().getValue());
        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(2)._processorInformation().status().getValue());
        Assert.assertEquals(PadProcessingStatus.ignoredUinitializedChargeSplit, leasePadEntities.get(3)._processorInformation().status().getValue());
    }

    @Test
    public void testUninitializedChargeSplitCase3A() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "park", "100", 100));
        leasePadEntities.add(createModelFull("2", "park", null, 100));
        leasePadEntities.add(createModelFull("1", "rent", "50", 1000));
        leasePadEntities.add(createModelFull("2", "rent", "50", 1000));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.5455"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.ignoredUinitializedChargeSplit, leasePadEntities.get(1)._processorInformation().status().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(2)._processorInformation().status().getValue());

        Assert.assertNull("record accepted, but was " + leasePadEntities.get(3)._processorInformation().status().getValue(), leasePadEntities.get(3)
                ._processorInformation().status().getValue());
        assertEquals(new BigDecimal("0.4545"), leasePadEntities.get(3)._processorInformation().percent().getValue());

    }

    // Same as A only "park" for second account
    @Test
    public void testUninitializedChargeSplitCase3B() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "park", null, 100));
        leasePadEntities.add(createModelFull("2", "park", "100", 100));
        leasePadEntities.add(createModelFull("1", "rent", "50", 1000));
        leasePadEntities.add(createModelFull("2", "rent", "50", 1000));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        //print(leasePadEntities);

        Assert.assertEquals(PadProcessingStatus.ignoredUinitializedChargeSplit, leasePadEntities.get(0)._processorInformation().status().getValue());

        assertEquals(new BigDecimal("0.5455"), leasePadEntities.get(1)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.4545"), leasePadEntities.get(2)._processorInformation().percent().getValue());
        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(3)._processorInformation().status().getValue());

    }
}
