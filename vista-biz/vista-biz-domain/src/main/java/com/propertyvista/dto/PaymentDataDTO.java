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

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.financial.AllowedPaymentsSetup;
import com.propertyvista.domain.payment.LeasePaymentMethod;
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

    @Transient
    AddressSimple address();

    @Editor(type = EditorType.label)
    IPrimitive<String> propertyCode();

    @Editor(type = EditorType.label)
    IPrimitive<String> unitNumber();

    @Editor(type = EditorType.label)
    IPrimitive<String> leaseId();

    @Editor(type = EditorType.label)
    IPrimitive<Status> leaseStatus();

    AllowedPaymentsSetup allowedPaymentsSetup();

    // UI-only (control organization) members:

    @NotNull
    @Transient
    IPrimitive<PaymentSelect> selectPaymentMethod();

    @NotNull
    @Transient
    LeasePaymentMethod profiledPaymentMethod();

    @Transient
    IPrimitive<Boolean> storeInProfile();
}
