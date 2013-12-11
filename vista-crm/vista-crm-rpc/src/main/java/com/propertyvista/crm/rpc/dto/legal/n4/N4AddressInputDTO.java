/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.legal.n4;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressSimple;

// TODO this is temporary for v1.2.2.2. hopefully this data will be received from building and policy
// we need this to get fill 'from' and agents field on n4 form, which can be different
// when I'm writing this (11-12-2013), mailing address of agent is set up in policy,
// and in the future 'form' field should be set up for building
@Transient
public interface N4AddressInputDTO extends IEntity {

    // this is filled from N4Policy can can be overridden by user
    @NotNull
    IPrimitive<String> companyName();

    // this is filled from N4Policy can can be overridden by user
    @NotNull
    AddressSimple mailingAddress();

    @NotNull
    IPrimitive<String> buildingOwnerName();

    @NotNull
    AddressSimple buildingOwnerMailingAddress();

    IPrimitive<Boolean> buildingOwnerSameAsLandlord();

}
