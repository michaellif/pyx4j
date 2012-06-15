/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.CustomerUser;

public interface Customer extends IEntity {

    @NotNull
    IPrimitive<String> customerId();

    @NotNull
    @ReadOnly(allowOverrideNull = true)
    @Detached
    @MemberColumn(name = "user_id")
    @Owned(cascade = {})
    CustomerUser user();

    @ToString(index = 0)
    @EmbeddedEntity
    Person person();

    @Owned
    @Detached
    @Length(3)
    IList<EmergencyContact> emergencyContacts();

    @Timestamp
    IPrimitive<Date> updated();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<PersonScreening> _PersonScreenings();

    @Owned
    @Detached(level = AttachLevel.Detached)
    IList<PaymentMethod> paymentMethods();

    /**
     * This used to enforce Data access
     */
    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = Tenant.class)
    ISet<Tenant> _tenantInLease();
}
