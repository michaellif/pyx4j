/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 29, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.UnitPolicy;

@DiscriminatorValue("PetPolicy")
public interface PetPolicy extends UnitPolicy {

    /**
     * Max pet weight in pounds.
     */
    IPrimitive<Double> maxPetWeight();

    /**
     * Max number of pets.
     */
    IPrimitive<Integer> maxNumberOfPets();

    /**
     * Some equation that directs how to calculate the charge for pet according to the pet type, weight etc...
     */
    IPrimitive<String> petChargeEquation();
}
