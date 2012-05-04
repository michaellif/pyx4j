/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.domain.payment.pad;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadDebitRecord extends IEntity {

    @Owner
    @JoinColumn
    PadBatch padBatch();

    @Override
    @Indexed
    @OrderColumn
    IPrimitive<Key> id();

    // A unique value to represent the client/cardholder
    @Length(29)
    IPrimitive<String> clientId();

    IPrimitive<BigDecimal> amount();

    @Length(3)
    IPrimitive<String> bankId();

    @Length(5)
    IPrimitive<String> branchTransitNumber();

    @Length(12)
    IPrimitive<String> accountNumber();

    //A unique value to represent the transaction/payment
    @Length(15)
    IPrimitive<String> transactionId();
}
