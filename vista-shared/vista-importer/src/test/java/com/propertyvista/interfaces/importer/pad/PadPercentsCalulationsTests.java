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

public class PadPercentsCalulationsTests {

    private PadFileModel createModelPercent(String percent) {
        PadFileModel model = EntityFactory.create(PadFileModel.class);
        model.percent().setValue(percent);
        return model;
    }

    private void assertEquals(BigDecimal expected, BigDecimal actual) {
        Assert.assertEquals(expected.setScale(4, BigDecimal.ROUND_HALF_UP).toString(), actual.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
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

}
