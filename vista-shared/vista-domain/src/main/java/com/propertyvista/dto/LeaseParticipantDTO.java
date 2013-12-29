/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-15
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@Transient
@AbstractEntity
public interface LeaseParticipantDTO<E extends LeaseTermParticipant<?>> extends LeaseParticipant<E> {

    @Override
    Lease lease();

    @Caption(name = "Lease Term")
    LeaseTermV leaseTermV();

    IList<LeasePaymentMethod> paymentMethods();

    IPrimitiveSet<CreditCardType> allowedCardTypes();

    IPrimitive<Boolean> electronicPaymentsAllowed();

    /** Sets the age of majority for validation, null if no validation is required */
    IPrimitive<Integer> ageOfMajority();

    @Detached(level = AttachLevel.CollectionSizeOnly)
    IList<Lease> leasesOfThisCustomer();

    @Detached(level = AttachLevel.CollectionSizeOnly)
    IList<Lease> applicationsOfThisCustomer();
}
