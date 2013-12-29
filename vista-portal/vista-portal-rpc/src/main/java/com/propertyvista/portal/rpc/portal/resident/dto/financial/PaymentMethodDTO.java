/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-15
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto.financial;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

@Transient
public interface PaymentMethodDTO extends IEntity {

    LeasePaymentMethod paymentMethod();

    IPrimitive<Boolean> electronicPaymentsAllowed();

    IPrimitiveSet<PaymentType> allowedPaymentTypes();

    IPrimitiveSet<CreditCardType> allowedCardTypes();

    IPrimitiveSet<CreditCardType> convenienceFeeApplicableCardTypes();
}
