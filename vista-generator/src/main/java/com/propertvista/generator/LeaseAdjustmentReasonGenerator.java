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

import java.util.List;

import org.apache.commons.lang.math.RandomUtils;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentReasonGenerator {

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
