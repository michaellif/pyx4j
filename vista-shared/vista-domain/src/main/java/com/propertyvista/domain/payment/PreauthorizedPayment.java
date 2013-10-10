/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-13
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Tenant;

@Caption(name = "Pre-Authorized Payment")
public interface PreauthorizedPayment extends IEntity {

    public interface PreauthorizedPaymentCoveredItem extends IEntity {

        @NotNull
        // TODO make ReadOnly with BillableItem versioning
        //@ReadOnly
        @ToString(index = 0)
        BillableItem billableItem();

        @NotNull
        @ToString(index = 1)
        @Format("#,##0.00")
        @Editor(type = EditorType.money)
        IPrimitive<BigDecimal> amount();

        // -----------------------------

        @Owner
        @ReadOnly
        @Detached
        @NotNull
        @JoinColumn
        @MemberColumn(notNull = true)
        @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
        PreauthorizedPayment pap();

        @OrderColumn
        IPrimitive<Integer> orderId();
    }

    @Owned
    IList<PreauthorizedPaymentCoveredItem> coveredItems();

    @NotNull
    IPrimitive<Boolean> isDeleted();

    // set to getNextScheduledPreauthorizedPaymentDate during creation
    IPrimitive<LogicalDate> effectiveFrom();

    // Set when tenant modifies PAP after cut off date to cutOffDate or when is called suspendPreauthorizedPayment to cutOffDate date -1 so it will not work for next PAP
    IPrimitive<LogicalDate> expiring();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(notNull = true)
    LeasePaymentMethod paymentMethod();

    @Length(40)
    IPrimitive<String> comments();

    // internals: -------------------------------------------------------------

    @Owner
    @ReadOnly
    @Detached
    @NotNull
    @JoinColumn
    @MemberColumn(notNull = true)
    Tenant tenant();

    @Detached
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    PreauthorizedPayment reviewOfPap();

    // billingCycleStartDate  when 
    IPrimitive<LogicalDate> updatedByTenant();

    IPrimitive<LogicalDate> updatedBySystem();

    @ReadOnly
    @Detached(level = AttachLevel.ToStringMembers)
    AbstractPmcUser createdBy();

    @Timestamp(Timestamp.Update.Created)
    @Editor(type = EditorType.label)
    IPrimitive<Date> creationDate();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();
}
