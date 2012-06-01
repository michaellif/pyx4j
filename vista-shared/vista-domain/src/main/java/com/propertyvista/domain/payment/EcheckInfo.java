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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@Table(prefix = "payment")
@DiscriminatorValue("EcheckInfo")
public interface EcheckInfo extends PaymentDetails {

    // No need for Caledon
    @NotNull
    @Caption(name = "Name On Account")
    IPrimitive<String> nameOn();

    @NotNull
    @Length(3)
    @ToString(index = 0)
    IPrimitive<String> bankId();

    @NotNull
    @Length(5)
    IPrimitive<String> branchTransitNumber();

    @NotNull
    @Length(12)
    @ToString(index = 1)
    @Caption(name = "Account Number")
    IPrimitive<String> accountNo();
}
