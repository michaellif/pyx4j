/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-23
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@Table(prefix = "payment")
@DiscriminatorValue("CashInfo")
public interface CashInfo extends PaymentDetails {

    @ToString(index = 0)
    @NotNull
    @Format("#,##0.00")
    @Caption(name = "Received Amount")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> receivedAmount();

    @Format("#,##0.00")
    @Caption(name = "Change Amount")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> changeAmount();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> notes();
}
