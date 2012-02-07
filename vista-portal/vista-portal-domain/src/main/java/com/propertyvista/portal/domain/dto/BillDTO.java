/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.charges.ChargeLine;

@Transient
public interface BillDTO extends IEntity {

    @I18n
    public enum BillType {

        Bill, Payment;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    IPrimitive<BillType> type();

    IList<ChargeLine> charges();

    IPrimitive<LogicalDate> dueDate();

    IPrimitive<LogicalDate> paidOn();

    IPrimitive<LogicalDate> fromDate();

    IPrimitive<LogicalDate> toDate();

    IPrimitive<LogicalDate> prevPaymentDate();

    @Format("#0.00")
    @Editor(type = EditorType.money_new)
    IPrimitive<BigDecimal> prevTotal();

    PaymentMethodDTO paymentMethod();

    IPrimitive<Boolean> preAuthorized();

    IPrimitive<String> transactionID();

    @Format("#0.00")
    @Editor(type = EditorType.money_new)
    IPrimitive<BigDecimal> total();
}
