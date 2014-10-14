/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.test.mock.MockDataModel;

public class RestrictionsPolicyDataModel extends MockDataModel<RestrictionsPolicy> {

    private RestrictionsPolicy policy;

    public RestrictionsPolicyDataModel() {
    }

    @Override
    protected void generate() {
        policy = EntityFactory.create(RestrictionsPolicy.class);
        policy.occupantsPerBedRoom().setValue(Double.MAX_VALUE);
        policy.maxParkingSpots().setValue(Integer.MAX_VALUE);
        policy.maxLockers().setValue(Integer.MAX_VALUE);
        policy.maxPets().setValue(Integer.MAX_VALUE);
        policy.maxPets().setValue(Integer.MAX_VALUE);
        policy.ageOfMajority().setValue(0);
        policy.enforceAgeOfMajority().setValue(false);
        policy.maturedOccupantsAreApplicants().setValue(false);
        policy.noNeedGuarantors().setValue(false);
        policy.yearsToForcingPreviousAddress().setValue(3);
        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());
        Persistence.service().persist(policy);
        addItem(policy);
    }

}
