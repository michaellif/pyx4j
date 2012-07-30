/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;

import com.propertyvista.domain.media.Document;
import com.propertyvista.domain.tenant.Guarantor_2;
import com.propertyvista.domain.tenant.Tenant_2;
import com.propertyvista.domain.tenant.lease.Lease.Term;
import com.propertyvista.domain.tenant.lease.LeaseAgreement.LeaseAgreementV;

public interface LeaseAgreement extends IVersionedEntity<LeaseAgreementV> {

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    Lease_2 lease();

    @OrderColumn
    IPrimitive<Integer> orderInOwner();

    @NotNull
    @ReadOnly
    @MemberColumn(name = "leaseTerm")
    IPrimitive<Term> term();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseFrom();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseTo();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> actualLeaseTo();

    @Detached
    // should be loaded in service when necessary!..
    IList<Document> documents();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> approvalDate();

    public interface LeaseAgreementV extends IVersionData<LeaseAgreement> {

        @Owned
        @Detached
        IList<Tenant_2> tenants();

        @Owned
        @Detached
        IList<Guarantor_2> guarantors();

        @EmbeddedEntity
        LeaseProducts leaseProducts();
    }
}
