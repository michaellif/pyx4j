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
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("RestrictionsPolicy")
@LowestApplicableNode(value = Building.class)
public interface RestrictionsPolicy extends Policy, TenantsAccessiblePolicy {

    @Caption(name = "Occupants over 18 are Applicants", description = "Some landlords force all occupants over 18 to be on LEASE and therefore anyone OVER 18 MUST be Applicant/Co-Applicant and cannot be Dependent")
    IPrimitive<Boolean> occupantsOver18areApplicants();

    @Caption(name = "Occupants per Bedroom", description = "Number of Occupants (N) per Bedroom in formula: Bedrooms x N = OccupantsPerUnit")
    IPrimitive<Double> occupantsPerBedRoom();

    @Caption(description = "Maximum allowed parking spots")
    IPrimitive<Integer> maxParkingSpots();

    @Caption(description = "Maximum allowed lockers")
    IPrimitive<Integer> maxLockers();

    @Caption(description = "Maximum allowed pets quantity")
    IPrimitive<Integer> maxPets();

    @NotNull
    IPrimitive<Boolean> enforceAgeOfMajority();

    @NotNull
    IPrimitive<Integer> ageOfMajority();
}
