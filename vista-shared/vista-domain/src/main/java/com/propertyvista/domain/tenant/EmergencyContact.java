/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.person.Person;

public interface EmergencyContact extends Person {

    @NotNull
    @ReadOnly
    @MemberColumn(notNull = true)
    @Owner
    @JoinColumn
    @Indexed
    Customer customer();

    @OrderColumn
    IPrimitive<Integer> orderInCustomer();

    @EmbeddedEntity
    AddressSimple address();
}
