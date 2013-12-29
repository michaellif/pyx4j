/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.policy.policies.DatesPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class DatesPolicyPreloader extends AbstractPolicyPreloader<DatesPolicy> {

    private static final LogicalDate YEAR_RANGE_START = new LogicalDate(20, 0, 1);

    private static final int YEAR_RANGE_SPAN = 5;

    public DatesPolicyPreloader() {
        super(DatesPolicy.class);
    }

    @Override
    protected DatesPolicy createPolicy(StringBuilder log) {
        DatesPolicy misc = EntityFactory.create(DatesPolicy.class);

        misc.yearRangeStart().setValue(YEAR_RANGE_START);
        misc.yearRangeFutureSpan().setValue(YEAR_RANGE_SPAN);

        log.append(misc.getStringView());

        return misc;
    }
}
