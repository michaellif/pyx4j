/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;

public interface PaymentMethodFacade {

    LeasePaymentMethod persistLeasePaymentMethod(Building building, LeasePaymentMethod paymentMethod);

    void deleteLeasePaymentMethod(LeasePaymentMethod paymentMethod);

    List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseTermParticipant<?> participant);

    List<LeasePaymentMethod> retrieveLeasePaymentMethods(Customer customer);

    InsurancePaymentMethod persistInsurancePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId);

    InsurancePaymentMethod retrieveInsurancePaymentMethod(Tenant tenantId);
}
