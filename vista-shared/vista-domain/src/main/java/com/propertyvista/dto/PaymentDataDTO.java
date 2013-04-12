/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-12
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease.Status;

@Transient
public interface PaymentDataDTO extends IEntity {

    @I18n
    enum PaymentSelect {
    
        New,
    
        Profiled;
    
        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    AddressSimple propertyAddress();

    IPrimitive<String> propertyCode();

    IPrimitive<String> unitNumber();

    IPrimitive<String> leaseId();

    IPrimitive<Status> leaseStatus();

    IPrimitive<Boolean> electronicPaymentsAllowed();

    IPrimitiveSet<PaymentType> allowedPaymentTypes();

    // UI-only (control organization) members:

    @NotNull
    @Transient
    IPrimitive<PaymentSelect> selectPaymentMethod();

    @NotNull
    @Transient
    LeasePaymentMethod profiledPaymentMethod();

    @Transient
    IPrimitive<Boolean> addThisPaymentMethodToProfile();
}
