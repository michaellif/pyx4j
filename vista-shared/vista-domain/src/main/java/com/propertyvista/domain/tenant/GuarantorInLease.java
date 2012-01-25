/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTableOrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;

@ToStringFormat("{0} - {1}, {2}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface GuarantorInLease extends IBoundToApplication {

    @Owner
    @Detached
    @NotNull
    @Indexed
    @JoinColumn
    Lease lease();

    interface OrderInLeaseId extends ColumnId {

    }

    @JoinTableOrderColumn(OrderInLeaseId.class)
    IPrimitive<Integer> orderInLease();

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    Guarantor guarantor();

    @Override
    @NotNull
    @Indexed
    @Detached
    Application application();

    @ToString(index = 2)
    @NotNull
    IPrimitive<PersonRelationship> relationship();

}
