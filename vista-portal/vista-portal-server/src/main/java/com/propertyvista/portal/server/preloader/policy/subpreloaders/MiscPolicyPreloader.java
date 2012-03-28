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
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.MiscPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class MiscPolicyPreloader extends AbstractPolicyPreloader<MiscPolicy> {

    private static final double OCCUPANTS_PER_BEDROOM = 2d;

    private static final int MAX_PETS = 4;

    private static final int MAX_PARKING_SPOTS = 3;

    private static final boolean ONE_MONTH_DEPOSIT = false;

    private static final boolean OCCUPANTS_OVER_18_ARE_APPLICANTS = false;

    private static final LogicalDate YEAR_RANGE_START = new LogicalDate(1800 - 1900, 1, 1);

    private static final int YEAR_RANGE_SPAN = 5;

    public MiscPolicyPreloader() {
        super(MiscPolicy.class);
    }

    @Override
    protected MiscPolicy createPolicy(StringBuilder log) {
        MiscPolicy misc = EntityFactory.create(MiscPolicy.class);

        misc.occupantsOver18areApplicants().setValue(OCCUPANTS_OVER_18_ARE_APPLICANTS);
        misc.occupantsPerBedRoom().setValue(OCCUPANTS_PER_BEDROOM);
        misc.oneMonthDeposit().setValue(ONE_MONTH_DEPOSIT);
        misc.maxParkingSpots().setValue(MAX_PARKING_SPOTS);
        misc.maxPets().setValue(MAX_PETS);
        misc.yearRangeStart().setValue(YEAR_RANGE_START);
        misc.yearRangeFutureSpan().setValue(YEAR_RANGE_SPAN);

        log.append(misc.getStringView());

        return misc;
    }
}
