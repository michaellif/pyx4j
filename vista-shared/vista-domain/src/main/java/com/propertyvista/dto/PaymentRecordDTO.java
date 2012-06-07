/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-02
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

@Transient
@ExtendsDBO
public interface PaymentRecordDTO extends PaymentRecord {

    @I18n
    enum PaymentSelect {

        New,

        Profiled;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    IPrimitive<String> propertyCode();

    IPrimitive<String> unitNumber();

    IPrimitive<String> leaseId();

    IPrimitive<Status> leaseStatus();

    IList<LeaseParticipant> participants();

    @NotNull
    IPrimitive<PaymentSelect> paymentSelect();

    IPrimitive<Boolean> addThisPaymentMethodToProfile();

    @NotNull
    PaymentMethod profiledPaymentMethod();
}
