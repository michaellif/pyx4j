/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.payment.PaymentMethod;

@Transient
public interface PaymentDataDTO extends IEntity {

    @I18n
    public enum PaymentStatus {

        Pending, Confirmed;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    };

    IPrimitive<LogicalDate> paidOn();

    @Format("$#,#00.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> total();

    IPrimitive<String> transactionId();

    IPrimitive<PaymentStatus> status();

    PaymentMethod paymentMethod();

}
