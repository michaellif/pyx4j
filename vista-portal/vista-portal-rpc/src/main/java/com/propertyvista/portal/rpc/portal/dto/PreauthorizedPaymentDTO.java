/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.dto;

import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitiveSet;

import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.dto.PaymentDataDTO;

@Transient
@ExtendsDBO(PreauthorizedPayment.class)
public interface PreauthorizedPaymentDTO extends PaymentDataDTO, PreauthorizedPayment {

    IPrimitiveSet<AmountType> allowedAmountTypes();
}
