/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.PreauthorizedPayment;

@Transient
public interface PreauthorizedPaymentListDTO extends IEntity {

    @Transient
    public interface ListItemDTO extends PreauthorizedPayment {
    }

    @Caption(name = "Pre-Authorized Payments")
    IList<ListItemDTO> preauthorizedPayments();

    @Caption(name = "Your current automated payment date")
    IPrimitive<LogicalDate> currentPaymentDate();

    @Caption(name = "Your next automated payment date")
    IPrimitive<LogicalDate> nextPaymentDate();

    IPrimitive<Boolean> isMoveOutWithinNextBillingCycle();
}
