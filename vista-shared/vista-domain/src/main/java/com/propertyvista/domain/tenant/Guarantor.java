/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.TenantUserHolder;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

@DiscriminatorValue("Guarantor")
public interface Guarantor extends LeaseParticipant, TenantUserHolder {

    @Override
    @NotNull
    @ReadOnly
    @Detached
    @MemberColumn(name = "user_id")
    @Deprecated
    CustomerUser user();

    @NotNull
    @ToString(index = 1)
    IPrimitive<PersonRelationship> relationship();

    /**
     * Who invited this Guarantor to lease
     */
    Tenant tenant();
}
