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

import com.pyx4j.entity.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
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

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public interface Customer extends IEntity {

    @NotNull
    @Length(14)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> customerId();

    @NotNull
    @ReadOnly(allowOverrideNull = true)
    @Detached
    @Indexed
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

    @Owned
    @Detached
    IList<CustomerPicture> pictures();

    @Timestamp
    IPrimitive<Date> updated();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    CustomerScreening personScreening();

    // TODO @Owned(cascade = {})
    // Warning: cascade enabled only for tests/development preload to work!
    @Owned
    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    IList<LeasePaymentMethod> paymentMethods();

    /**
     * This used to enforce Data access
     */
    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = LeaseParticipant.class)
    ISet<LeaseParticipant> _tenantInLease();

    @Owned
    @Detached(level = AttachLevel.Detached)
    CustomerAcceptedTerms signedTerms();

    IPrimitive<String> portalRegistrationToken();

    @NotNull
    IPrimitive<Boolean> registeredInPortal();
}
