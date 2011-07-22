/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-14
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.ptapp.dto;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.portal.domain.ptapp.Pet;
import com.propertyvista.portal.domain.ptapp.PetChargeRule;

@Transient
public interface PetsDTO extends IEntity {

    @Owned
    @Length(3)
    IList<Pet> pets();

    @Transient
    PetChargeRule petChargeRule();

    @Transient
    IPrimitive<Integer> petsMaximum();

    /**
     * This limit is expressed in Kg.
     */
    @Transient
    IPrimitive<Integer> petWeightMaximum();
}
