/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseApplicationRestrictionsPolicyDataModel extends MockDataModel<RestrictionsPolicy> {

    @Override
    protected void generate() {
        RestrictionsPolicy policy = EntityFactory.create(RestrictionsPolicy.class);

        policy.maturedOccupantsAreApplicants().setValue(false);
        policy.occupantsPerBedRoom().setValue(2d);
        policy.maxParkingSpots().setValue(1);
        policy.maxLockers().setValue(1);
        policy.maxPets().setValue(1);

        policy.enforceAgeOfMajority().setValue(true);
        policy.ageOfMajority().setValue(18);

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);

    }

}
