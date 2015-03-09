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
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("RestrictionsPolicy")
@LowestApplicableNode(value = Building.class)
public interface RestrictionsPolicy extends Policy, TenantsAccessiblePolicy {

    @Caption(name = "Occupants per Bedroom", description = "Number of Occupants (N) per Bedroom in formula: Bedrooms x N = Occupants per Unit")
    IPrimitive<Double> occupantsPerBedRoom();

    @Caption(description = "Maximum allowed parking spots")
    IPrimitive<Integer> maxParkingSpots();

    @Caption(description = "Maximum allowed lockers")
    IPrimitive<Integer> maxLockers();

    @Caption(description = "Maximum allowed pets quantity")
    IPrimitive<Integer> maxPets();

    // Majority:

    @NotNull
    IPrimitive<Integer> ageOfMajority();

    @MemberColumn(notNull = true)
    IPrimitive<Boolean> enforceAgeOfMajority();

    @MemberColumn(notNull = true)
    @Caption(name = "Matured Occupants are Applicants", description = "Some landlords force all matured occupants (over 18-19) to be on LEASE and therefore anyone OVER Age of Majority MUST be Applicant/Co-Applicant and cannot be Dependent")
    IPrimitive<Boolean> maturedOccupantsAreApplicants();

    // Financial:

    @MemberColumn(notNull = true)
    @Caption(name = "Guarantors are optional", description = "If Guarantors are not necessary")
    IPrimitive<Boolean> noNeedGuarantors();

    @NotNull
    @Caption(description = "Minimal duration of employment (in months) to forcing previous employment information")
    IPrimitive<Integer> minEmploymentDuration();

    @Caption(description = "If not set - unlimited")
    IPrimitive<Integer> maxNumberOfEmployments();

    // Miscellaneous:

    @NotNull
    IPrimitive<Integer> yearsToForcingPreviousAddress();

    @MemberColumn(notNull = true)
    IPrimitive<Boolean> emergencyContactsIsMandatory();

    @NotNull
    IPrimitive<Integer> emergencyContactsNumber();

    @MemberColumn(notNull = true)
    IPrimitive<Boolean> referenceSourceIsMandatory();
}
