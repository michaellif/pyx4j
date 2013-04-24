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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.interfaces.importer.model.PadProcessorInformation.PadProcessingStatus;

public class PadPercentsCalulationsTests {

    private PadFileModel createModelPercent(String percent) {
        PadFileModel model = EntityFactory.create(PadFileModel.class);
        model.percent().setValue(percent);
        return model;
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

        model.chargeId().setValue(chargeCode);
        model.estimatedCharge().setValue(Double.toString(estimatedCharge));

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
    public void testRentParking_Merged() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", "90", 1000));
        leasePadEntities.add(createModelFull("1", "park", "100", 100));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("0.9091"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(1)._processorInformation().status().getValue());
    }

    @Test
    public void testUninitializedChargeCplit() {
        List<PadFileModel> leasePadEntities = new ArrayList<PadFileModel>();
        leasePadEntities.add(createModelFull("1", "rent", null, 1000));
        leasePadEntities.add(createModelFull("2", "rent", null, 1000));
        leasePadEntities.add(createModelFull("1", "park", "100", 100));

        TenantPadProcessor.calulateLeasePercents(leasePadEntities);

        assertEquals(new BigDecimal("1.0000"), leasePadEntities.get(0)._processorInformation().percent().getValue());

        Assert.assertEquals(PadProcessingStatus.ignoredUinitializedChargeSplit, leasePadEntities.get(1)._processorInformation().status().getValue());

        Assert.assertEquals(PadProcessingStatus.mergedWithAnotherRecord, leasePadEntities.get(2)._processorInformation().status().getValue());
    }
}
