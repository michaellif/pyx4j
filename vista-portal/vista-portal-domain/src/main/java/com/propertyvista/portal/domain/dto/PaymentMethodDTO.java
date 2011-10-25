/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 9, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.PaymentType;

@Transient
public interface PaymentMethodDTO extends CreditCardInfo {
//TODO to be finalized
    IPrimitive<PaymentType> type();

    @EmbeddedEntity
    AddressStructured billingAddress();

    @EmbeddedEntity
    Phone phone();

    IPrimitive<String> nameOnAccount();

    IPrimitive<Boolean> primary();

}
