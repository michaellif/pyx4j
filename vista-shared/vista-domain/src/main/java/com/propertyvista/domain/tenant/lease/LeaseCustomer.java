/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.Customer;

@ToStringFormat("{0}, {1}")
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface LeaseCustomer<E extends LeaseParticipant<?>> extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    @Indexed(uniqueConstraint = true, group = { "discriminator+lc,1" })
    Lease lease();

    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @ToString(index = 1)
    @Indexed(uniqueConstraint = true, group = { "discriminator+lc,2" })
    Customer customer();

    @NotNull
    @ToString(index = 0)
    @Caption(name = "Id")
    @Length(14)
    @Indexed(uniqueConstraint = true, group = { "discriminator+id,1" })
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> participantId();

    @Detached(level = AttachLevel.Detached)
    ISet<E> leaseParticipants();
}
