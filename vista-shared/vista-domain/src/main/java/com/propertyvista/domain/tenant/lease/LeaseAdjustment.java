/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.GeneratedValue;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;

//TODO pending adjustment  should post by batch
@ToStringFormat("{0}, ${1}")
public interface LeaseAdjustment extends IEntity {

    @I18n(context = "LeaseAdjustment Status")
    @XmlType(name = "LeaseAdjustmentStatus")
    public enum Status {
        draft, submited;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "LeaseAdjustment ExecutionType")
    @XmlType(name = "LeaseAdjustmentExecutionType")
    public enum ExecutionType {
        pending, immediate;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "LeaseAdjustment TaxType")
    @XmlType(name = "LeaseAdjustmentTaxType")
    public enum TaxType {
        percent, value;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @GeneratedValue(type = GeneratedValue.GenerationType.randomUUID)
    IPrimitive<String> uid();

    @NotNull
    IPrimitive<Status> status();

    @Owner
    @ReadOnly
    @Detached
    @JoinColumn
    BillingAccount billingAccount();

    @NotNull
    IPrimitive<ExecutionType> executionType();

    @NotNull
    @ToString(index = 0)
    ARCode code();

    @NotNull
    @ToString(index = 1)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    /*
     * In case of overwriteDefaultTax == false,
     * Tax value should be recalculated by means of current LeaseAdjustmentPolicy.
     * so, in case of default(policed) tax - this member effectively becomes transient field.
     */
    @NotNull
    @ToString(index = 2)
    @Format("#,##0.00")
    IPrimitive<BigDecimal> tax();

    @NotNull
    IPrimitive<TaxType> taxType();

    IPrimitive<Boolean> overwriteDefaultTax();

    @Transient
    @Caption(name = "Total")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> _total();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> description();

    IPrimitive<LogicalDate> receivedDate();

    IPrimitive<LogicalDate> targetDate();

    @Caption(name = "Last Updated")
    @Timestamp
    IPrimitive<LogicalDate> updated();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> created();

    Employee createdBy();

    // internals:
    interface OrderId extends ColumnId {
    }

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();
}