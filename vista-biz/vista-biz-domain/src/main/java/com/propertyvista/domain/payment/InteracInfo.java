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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IPrimitive;

@ToStringFormat("{0}, {1}")
@DiscriminatorValue("InteracInfo")
public interface InteracInfo extends PaymentDetails {

    //TODO
    IPrimitive<Key> incomingInteracTransaction();

    @ToString(index = 0)
    @Caption(name = "Bank Number")
    @Deprecated
    IPrimitive<String> bankNo();

    @Caption(name = "Transit Number")
    @Deprecated
    IPrimitive<String> transitNo();

    @ToString(index = 1)
    @Caption(name = "Account Number")
    @Deprecated
    IPrimitive<String> accountNo();
}
