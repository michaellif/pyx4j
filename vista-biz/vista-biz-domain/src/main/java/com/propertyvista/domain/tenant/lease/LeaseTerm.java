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

import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;

@ToStringFormat("{0} - {1}, {2}")
public interface LeaseTerm extends IVersionedEntity<LeaseTermV> {

    @I18n(context = "Lease Term Type")
    @XmlType(name = "LeaseTermType")
    public enum Type {

        Fixed,

        @Translate("Fixed with Extension")
        FixedEx,

        Periodic;

        public static EnumSet<Type> renew() {
            return EnumSet.of(Fixed, Periodic);
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "Lease Term Status")
    @XmlType(name = "LeaseTermStatus")
    public enum Status {

        Current,

        Historic,

        Offer,

        AcceptedOffer;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // ------------------------------------------------------------

    AptUnit unit();

    /**
     * The <MITS:Description> node is the primary key of the person record. The <CustomerID> node is the tenant code related to the person. When a Unit Transfer
     * happens, a new person record (MITS:Description node) will be created with the new property info, unit info, etc., but it will still point to that same
     * tenant code
     */
    IPrimitive<String> yardiLeasePk();

    @NotNull
    @ReadOnly
    @ToString(index = 2)
    @MemberColumn(name = "leaseTermType")
    IPrimitive<Type> type();

    @NotNull
    @ToString(index = 3)
    @MemberColumn(name = "leaseTermStatus")
    IPrimitive<Status> status();

    @NotNull
    @ToString(index = 0)
    IPrimitive<LogicalDate> termFrom();

    @ToString(index = 1)
    IPrimitive<LogicalDate> termTo();

    IPrimitive<LogicalDate> actualTermTo();

    @ReadOnly
    @Timestamp(Update.Created)
    @Editor(type = EditorType.label)
    IPrimitive<LogicalDate> creationDate();

    public interface LeaseTermV extends IVersionData<LeaseTerm> {

        @Owned
        @Detached
        IList<LeaseTermTenant> tenants();

        @Owned
        @Detached
        IList<LeaseTermGuarantor> guarantors();

        @EmbeddedEntity
        LeaseProducts leaseProducts();

        @Detached
        IList<BuildingUtility> utilities();

        @Owned(cascade = {})
        @Detached(level = AttachLevel.Detached)
        LeaseTermAgreementDocument agreementDocument();
    }

    // internals:   -----------------------------------------------

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    @Indexed
    Lease lease();

    @OrderColumn
    IPrimitive<Integer> orderInOwner();
}
