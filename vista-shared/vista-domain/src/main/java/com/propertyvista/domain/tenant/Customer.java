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
import com.pyx4j.entity.annotations.DiscriminatorValue;
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

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.TenantUserHolder;

@DiscriminatorValue("Tenant")
public interface Customer extends IEntity, PersonScreeningHolder, TenantUserHolder {

    @NotNull
    IPrimitive<String> customerId();

    @Override
    @NotNull
    @ReadOnly
    @Detached
    @MemberColumn(name = "user_id")
    TenantUser user();

    @Override
    @ToString(index = 0)
    @EmbeddedEntity
    Person person();

    @Owned
// TODO : commented because of strange behavior of with @Owned - entities duplicated on loading/saving...  
//    @Detached
    @Length(3)
    IList<EmergencyContact> emergencyContacts();

    @Timestamp
    IPrimitive<Date> updated();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Override
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<PersonScreening> _PersonScreenings();

    /**
     * This used to enforce Data access
     */
    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = Tenant.class)
    ISet<Tenant> _tenantInLease();
}
