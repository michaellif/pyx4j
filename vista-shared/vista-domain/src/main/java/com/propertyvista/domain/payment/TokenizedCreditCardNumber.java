/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@EmbeddedEntity
@ToStringFormat(value = "{0,choice,null#XXXX XXXX XXXX XXXX|!null#{0}}", nil = "XXXX XXXX XXXX XXXX")
public interface TokenizedCreditCardNumber extends TokenizedNumber {

    @Override
    @Transient(logTransient = true)
    @Length(16)
    IPrimitive<String> number();

    @Override
    @Transient(logTransient = true)
    @Length(16)
    IPrimitive<String> newNumber();

    @Override
    @ToString
    @Length(16)
    IPrimitive<String> obfuscatedNumber();
}
