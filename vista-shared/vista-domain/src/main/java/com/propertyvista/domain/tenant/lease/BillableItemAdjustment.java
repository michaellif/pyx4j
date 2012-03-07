/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;

public interface BillableItemAdjustment extends IEntity {

    @I18n
    @XmlType(name = "AdjustmentType")
    enum AdjustmentType {
        percentage, monetary;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "ChargeType")
    enum ChargeType {
        negotiation, discount, priceCorrection;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    /**
     * postLease - applied during renewal
     * 
     */
    @I18n
    @XmlType(name = "TermType")
    enum TermType {
        postLease, inLease, oneTime;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Caption(name = "Last Updated")
    @Timestamp
    IPrimitive<LogicalDate> updated();

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    BillableItem billableItem();

    interface OrderId extends ColumnId {
    }

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();

    @NotNull
    @ToString(index = 0)
    IPrimitive<AdjustmentType> adjustmentType();

    @NotNull
    IPrimitive<ChargeType> chargeType();

    @NotNull
    IPrimitive<TermType> termType();

    IPrimitive<String> description();

    /*
     * for percentage - percentage
     * for monetary - amount
     */
    @NotNull
    @Format("#0.00")
    @ToString(index = 1)
    @MemberColumn(name = "adjustmentValue")
    IPrimitive<BigDecimal> value();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> effectiveDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> expirationDate();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createdWhen();

    Employee createdBy();
}
