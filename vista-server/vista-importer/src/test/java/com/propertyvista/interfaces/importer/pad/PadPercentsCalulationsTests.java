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
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.interfaces.importer.model.PadProcessorInformation;
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
        log.info(SimpleMessageFormat.format("account:{0} {1}% {2}$ status:{3}", m.accountNumber(), // 
                m._processorInformation().percent(), //
                m._processorInformation().chargeEftAmount(), //
                m._processorInformation().status()));
    }

    private void assertEquals(BigDecimal expected, BigDecimal actual) {
        Assert.assertEquals(expected.setScale(PadProcessorInformation.PERCENT_SCALE).toString(), (actual == null) ? null : actual.toString());
    }

    private void assertEquals(int scale, BigDecimal expected, BigDecimal actual) {
        Assert.assertEquals(expected.setScale(scale).toString(), (actual == null) ? null : actual.toString());
    }

    private BigDecimal createBigDecimalFromDouble(int scale, String string) {
        double percentNotRounded = (Double.parseDouble(string)) / 100.0;
        //System.out.println(string + " -> " + percentNotRounded);
        return new BigDecimal(percentNotRounded).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    @Test
    public void testBigDecimal() {
        assertEquals(4, new BigDecimal("0.3333"), createBigDecimalFromDouble(4, "33.333"));
        assertEquals(4, new BigDecimal("0.6667"), createBigDecimalFromDouble(4, "66.666"));

        assertEquals(6, new BigDecimal("0.333333"), createBigDecimalFromDouble(6, "33.33333"));
        assertEquals(6, new BigDecimal("0.666667"), createBigDecimalFromDouble(6, "66.66666"));
    }

    @Test
    public void test_30_60() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelPercent("33.33333"));
        leasePadEntities.add(createModelPercent("66.66666"));
        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.333333"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.666667"), leasePadEntities.get(1)._processorInformation().percent().getValue());
    }

    private PadFileModel createModelFull(String account, String chargeCode, String percent, String estimatedCharge) {
        PadFileModel model = EntityFactory.create(PadFileModel.class);
        model.percent().setValue(percent);

        model.bankId().setValue("1");
        model.transitNumber().setValue("1");
        model.accountNumber().setValue(account);

        model.chargeCode().setValue(chargeCode);
        model.estimatedCharge().setValue(estimatedCharge);

        return model;
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

    private PadFileModel createModelFullNonRecurring(String account, String chargeCode, String percent, double estimatedCharge) {
        PadFileModel model = createModelFull(account, chargeCode, percent, estimatedCharge);
        model.recurringEFT().setValue(Boolean.FALSE);
        return model;
    }

    @Test
    public void testRent_30_60() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "33.33333", 1000));
        leasePadEntities.add(createModelFull("2", "rent", "66.66666", 1000));
        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.333333"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.666667"), leasePadEntities.get(1)._processorInformation().percent().getValue());
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

        assertEquals(new BigDecimal("0.545455"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.454545"), leasePadEntities.get(1)._processorInformation().percent().getValue());

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

        assertEquals(new BigDecimal("0.909091"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(1)._processorInformation().status().getValue());
    }

    @Test
    public void testYardiImportSingel() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "33.28411622", 799.18));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.332841"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("266.00"), leasePadEntities.get(0)._processorInformation().chargeEftAmount().getValue());
    }

    @Test
    public void testYardiRounding_3X_A() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "29.03225806", "1588.75"));
        leasePadEntities.add(createModelFull("2", "rent", "37.09708891", "1588.75"));
        leasePadEntities.add(createModelFull("3", "rent", "33.87065303", "1588.75"));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        print(leasePadEntities);

        assertEquals(2, new BigDecimal("461.25"), leasePadEntities.get(0)._processorInformation().accountEftAmountTotal().getValue());
        assertEquals(2, new BigDecimal("589.38"), leasePadEntities.get(1)._processorInformation().accountEftAmountTotal().getValue());
        assertEquals(2, new BigDecimal("538.12"), leasePadEntities.get(2)._processorInformation().accountEftAmountTotal().getValue());
    }

    @Test
    public void testYardiRounding_50_50_A() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "50", 1000.01));
        leasePadEntities.add(createModelFull("2", "rent", "50", 1000.01));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.50"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("500.00"), leasePadEntities.get(0)._processorInformation().chargeEftAmount().getValue());
        assertEquals(new BigDecimal("0.50"), leasePadEntities.get(1)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("500.01"), leasePadEntities.get(1)._processorInformation().chargeEftAmount().getValue());
    }

    //TODO to fix in out model
    @Test
    @Ignore
    public void testYardiRounding_50_50_B() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "50", 1208.55));
        leasePadEntities.add(createModelFull("2", "rent", "50", 1208.55));

        leasePadEntities.add(createModelFull("1", "park", "50", 57.51));
        leasePadEntities.add(createModelFull("2", "park", "50", 57.51));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.50"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("633.03"), leasePadEntities.get(0)._processorInformation().accountEftAmountTotal().getValue());
        assertEquals(new BigDecimal("0.50"), leasePadEntities.get(1)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("633.03"), leasePadEntities.get(1)._processorInformation().accountEftAmountTotal().getValue());
    }

    @Test
    public void testYardiRounding_3X_50_50() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "50", 1076.25));
        leasePadEntities.add(createModelFull("2", "rent", "50", 1076.25));

        leasePadEntities.add(createModelFull("1", "rinpark", "50", 76.87));
        leasePadEntities.add(createModelFull("2", "rinpark", "50", 76.87));

        leasePadEntities.add(createModelFull("1", "raircon", "50", 10.0));
        leasePadEntities.add(createModelFull("2", "raircon", "50", 10.0));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.50"), leasePadEntities.get(0)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("581.55"), leasePadEntities.get(0)._processorInformation().accountEftAmountTotal().getValue());
        assertEquals(new BigDecimal("0.50"), leasePadEntities.get(1)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("581.57"), leasePadEntities.get(1)._processorInformation().accountEftAmountTotal().getValue());
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

        assertEquals(new BigDecimal("0.545455"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.ignoredUinitializedChargeSplit, leasePadEntities.get(1)._processorInformation().status().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(2)._processorInformation().status().getValue());

        Assert.assertNull("record accepted, but was " + leasePadEntities.get(3)._processorInformation().status().getValue(), leasePadEntities.get(3)
                ._processorInformation().status().getValue());
        assertEquals(new BigDecimal("0.454545"), leasePadEntities.get(3)._processorInformation().percent().getValue());

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

        assertEquals(new BigDecimal("0.545455"), leasePadEntities.get(1)._processorInformation().percent().getValue());
        assertEquals(new BigDecimal("0.454545"), leasePadEntities.get(2)._processorInformation().percent().getValue());
        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(3)._processorInformation().status().getValue());

    }

    // One of the accounts is Non Recurring
    @Test
    public void testUninitializedChargeSplitCase4A() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFullNonRecurring("1", "rent", null, 1000));
        leasePadEntities.add(createModelFull("2", "rent", "100", 1000));
        leasePadEntities.add(createModelFullNonRecurring("1", "park", null, 100));
        leasePadEntities.add(createModelFull("2", "park", null, 100));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        Assert.assertEquals(PadProcessingStatus.notUsedForACH, leasePadEntities.get(0)._processorInformation().status().getValue());

        assertEquals(new BigDecimal("1.0"), leasePadEntities.get(1)._processorInformation().percent().getValue());
        assertEquals(2, new BigDecimal("1100.0"), leasePadEntities.get(1)._processorInformation().calulatedEftTotalAmount().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(3)._processorInformation().status().getValue());
    }

    @Test
    @Ignore
    public void testUninitializedChargeSplitCase5A_2X_50_50() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "50", 1271.81));
        leasePadEntities.add(createModelFull("2", "rent", "50", 1271.81));

        leasePadEntities.add(createModelFull("1", "rinpark", null, 30));
        leasePadEntities.add(createModelFull("2", "rinpark", null, 30));

        leasePadEntities.add(createModelFull("1", "rinpark", null, 30));
        leasePadEntities.add(createModelFull("2", "rinpark", null, 30));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(2, new BigDecimal("635.90"), leasePadEntities.get(0)._processorInformation().accountEftAmountTotal().getValue());
        assertEquals(2, new BigDecimal("635.90"), leasePadEntities.get(1)._processorInformation().accountEftAmountTotal().getValue());
    }
}
