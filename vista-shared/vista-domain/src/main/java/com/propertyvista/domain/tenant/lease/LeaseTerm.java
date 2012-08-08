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

import javax.xml.bind.annotation.XmlType;

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
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.media.Document;
import com.propertyvista.domain.tenant.Guarantor2;
import com.propertyvista.domain.tenant.Tenant2;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;

@ToStringFormat("{0} - {1}, {2}")
public interface LeaseTerm extends IVersionedEntity<LeaseTermV> {

    @I18n(context = "Lease Term Type")
    @XmlType(name = "LeaseTermType")
    public enum Type {

        Fixed,

        FixedEx,

        Periodic;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "Lease Term Status")
    @XmlType(name = "LeaseTermStatus")
    public enum Status {

        Offer,

        FixedEx,

        Periodic;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // ------------------------------------------------------------

    @NotNull
    @ReadOnly
    @ToString(index = 2)
    @MemberColumn(name = "leaseTermType")
    IPrimitive<Type> type();

    @NotNull
    @Format("MM/dd/yyyy")
    @ToString(index = 0)
    IPrimitive<LogicalDate> termFrom();

    @NotNull
    @Format("MM/dd/yyyy")
    @ToString(index = 1)
    IPrimitive<LogicalDate> termTo();

    @Detached
    // should be loaded in service when necessary!..
    IList<Document> documents();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> approvalDate();

    public interface LeaseTermV extends IVersionData<LeaseTerm> {

        @Owned
        @Detached
        IList<Tenant2> tenants();

        @Owned
        @Detached
        IList<Guarantor2> guarantors();

        @EmbeddedEntity
        LeaseProducts leaseProducts();
    }

    // internals:   -----------------------------------------------

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    Lease2 lease();

    @OrderColumn
    IPrimitive<Integer> orderInOwner();
}
