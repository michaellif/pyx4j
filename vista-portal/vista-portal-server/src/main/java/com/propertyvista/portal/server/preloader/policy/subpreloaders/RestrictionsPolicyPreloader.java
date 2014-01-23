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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class RestrictionsPolicyPreloader extends AbstractPolicyPreloader<RestrictionsPolicy> {

    private static final double OCCUPANTS_PER_BEDROOM = 2d;

    private static final int MAX_PETS = 4;

    private static final int MAX_LOCKERS = 2;

    private static final int MAX_PARKING_SPOTS = 3;

    private static final boolean OCCUPANTS_OVER_18_ARE_APPLICANTS = true;

    private Integer ageOfMajority;

    private String provinceCode;

    public RestrictionsPolicyPreloader() {
        super(RestrictionsPolicy.class);
        this.ageOfMajority = null;
    }

    public RestrictionsPolicyPreloader ageOfMajority(int ageOfMajority) {
        this.ageOfMajority = ageOfMajority;
        return this;
    }

    public RestrictionsPolicyPreloader province(String provinceCode) {
        this.provinceCode = provinceCode;
        return this;
    }

    @Override
    public PolicyNode getTopNode() {
        if (provinceCode == null) {
            return super.getTopNode();
        } else {
            EntityQueryCriteria<Province> c = EntityQueryCriteria.create(Province.class);
            c.eq(c.proto().code(), provinceCode);
            Province p = Persistence.service().retrieve(c);
            if (p == null) {
                throw new Error("Province with code '" + provinceCode + "' was not found");
            }
            return p;
        }
    }

    @Override
    protected RestrictionsPolicy createPolicy(StringBuilder log) {
        RestrictionsPolicy policy = EntityFactory.create(RestrictionsPolicy.class);

        policy.maturedOccupantsAreApplicants().setValue(OCCUPANTS_OVER_18_ARE_APPLICANTS);
        policy.occupantsPerBedRoom().setValue(OCCUPANTS_PER_BEDROOM);
        policy.maxParkingSpots().setValue(MAX_PARKING_SPOTS);
        policy.maxLockers().setValue(MAX_LOCKERS);
        policy.maxPets().setValue(MAX_PETS);

        if (ageOfMajority != null) {
            policy.enforceAgeOfMajority().setValue(true);
            policy.ageOfMajority().setValue(ageOfMajority);
        } else {
            policy.enforceAgeOfMajority().setValue(false);
            policy.ageOfMajority().setValue(18);
        }

        log.append(policy.getStringView());

        return policy;
    }
}
