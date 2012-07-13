/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class RestrictionsPolicyPreloader extends AbstractPolicyPreloader<RestrictionsPolicy> {

    private static final double OCCUPANTS_PER_BEDROOM = 2d;

    private static final int MAX_PETS = 4;

    private static final int MAX_LOCKERS = 2;

    private static final int MAX_PARKING_SPOTS = 3;

    private static final boolean OCCUPANTS_OVER_18_ARE_APPLICANTS = false;

    public RestrictionsPolicyPreloader() {
        super(RestrictionsPolicy.class);
    }

    @Override
    protected RestrictionsPolicy createPolicy(StringBuilder log) {
        RestrictionsPolicy misc = EntityFactory.create(RestrictionsPolicy.class);

        misc.occupantsOver18areApplicants().setValue(OCCUPANTS_OVER_18_ARE_APPLICANTS);
        misc.occupantsPerBedRoom().setValue(OCCUPANTS_PER_BEDROOM);
        misc.maxParkingSpots().setValue(MAX_PARKING_SPOTS);
        misc.maxLockers().setValue(MAX_LOCKERS);
        misc.maxPets().setValue(MAX_PETS);

        log.append(misc.getStringView());

        return misc;

    }

}
