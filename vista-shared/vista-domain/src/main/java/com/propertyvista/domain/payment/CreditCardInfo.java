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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface CreditCardInfo extends IEntity {

    @NotNull
    @Caption(name = "Card Number")
    @MemberColumn(name = "cardNumber")
    @Transient
    IPrimitive<String> number();

    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    IPrimitive<String> numberRefference();

    @NotNull
    @Caption(name = "Expiry Date")
    @Editor(type = EditorType.monthyearpicker)
    IPrimitive<LogicalDate> expiryDate();

    @NotNull
    @Caption(name = "Card Security Code")
    @Transient
    IPrimitive<String> securityCode();

    @NotNull
    @Caption(name = "Name On Card")
    IPrimitive<String> name();

    IPrimitive<String> bankPhone();
}
