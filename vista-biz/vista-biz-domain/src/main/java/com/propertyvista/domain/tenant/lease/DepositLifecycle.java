/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.BillingAccount;

public interface DepositLifecycle extends IEntity {

    @I18n
    @XmlType(name = "DepositStatus")
    public enum DepositStatus {
        Created, Processed, Paid, Refunded;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Override
    @Indexed
    @ToString
    IPrimitive<Key> id();

    @Owner
    @ReadOnly
    @Detached
    @JoinColumn
    BillingAccount billingAccount();

    @OrderColumn()
    IPrimitive<Integer> orderInParent();

    // -----------------------------------------------------------------

    @NotNull
    IPrimitive<DepositStatus> status();

    IPrimitive<LogicalDate> depositDate();

    IPrimitive<LogicalDate> refundDate();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> currentAmount();

    @Owned
    @Detached
    IList<DepositInterestAdjustment> interestAdjustments();
}
