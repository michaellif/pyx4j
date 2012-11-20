/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.lease.Tenant;

class TenantSurePayments {

    static InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        EntityQueryCriteria<InsurancePaymentMethod> criteria = EntityQueryCriteria.create(InsurancePaymentMethod.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().retrieve(criteria);
    }

    static InsurancePaymentMethod updatePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        return null;
    }

    static InsuranceTenantSureTransaction preAuthorization(InsuranceTenantSureTransaction transaction) {
        return transaction;
    }

    public static void compleateTransaction(InsuranceTenantSureTransaction transaction) {
        // TODO Auto-generated method stub

    }
}
