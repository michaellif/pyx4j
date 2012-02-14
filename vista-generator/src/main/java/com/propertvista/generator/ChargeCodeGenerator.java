/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertvista.generator;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.math.RandomUtils;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.offering.ChargeCode;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class ChargeCodeGenerator {

    public static ChargeCode createChargeCode(int k, List<Tax> taxes, List<GlCode> glCodes) {

        ChargeCode chargeCode = EntityFactory.create(ChargeCode.class);
        int j = RandomUtils.nextInt(5) + 1;

        for (int i = 0; i < j; i++) {
            chargeCode.taxes().add(RandomUtil.random(taxes));
        }
        chargeCode.name().setValue("Name #" + k);
        chargeCode.glCode().set(RandomUtil.random(glCodes));

        return chargeCode;
    }

    public static Tax createTax(int i) {
        Tax tax = EntityFactory.create(Tax.class);
        Random randomGenerator = new Random();
        DecimalFormat format = new DecimalFormat("#.##");

        tax.authority().setValue("Authority #" + i);
        tax.name().setValue("Tax #" + i);
        tax.rate().setValue(Double.valueOf(format.format(randomGenerator.nextDouble() * 10)));
        tax.compound().setValue(randomGenerator.nextBoolean());

        return tax;
    }

    public static GlCode createGlCode() {
        GlCode glCode = EntityFactory.create(GlCode.class);
        Random randomGenerator = new Random();

        glCode.glId().setValue(Integer.valueOf((int) (randomGenerator.nextDouble() * 10000)));
        glCode.description().setValue("description...");
        return glCode;
    }

    public static LeaseAdjustmentReason createLeaseAdjustmentReason(int k, List<Tax> taxes, List<GlCode> glCodes) {

        LeaseAdjustmentReason leaseAdjustmentReason = EntityFactory.create(LeaseAdjustmentReason.class);
        int j = RandomUtils.nextInt(5) + 1;

        for (int i = 0; i < j; i++) {
            leaseAdjustmentReason.taxes().add(RandomUtil.random(taxes));
        }
        leaseAdjustmentReason.glCode().set(RandomUtil.random(glCodes)); //name is set in preloader

        return leaseAdjustmentReason;
    }

}
