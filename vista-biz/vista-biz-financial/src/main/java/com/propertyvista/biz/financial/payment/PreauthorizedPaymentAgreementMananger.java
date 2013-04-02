/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.Tenant;

class PreauthorizedPaymentAgreementMananger {

    PreauthorizedPayment persistPreauthorizedPayment(PreauthorizedPayment preauthorizedPayment, Tenant tenantId) {
        preauthorizedPayment.tenant().set(tenantId);
        Persistence.service().merge(preauthorizedPayment);
        return null;
    }

    void deletePreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId) {
        PreauthorizedPayment preauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPaymentId.getPrimaryKey());
        preauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
        Persistence.service().merge(preauthorizedPayment);
        new ScheduledPaymentsManager().cancelScheduledPayments(preauthorizedPayment);
    }

    List<PreauthorizedPayment> retrievePreauthorizedPayments(Tenant tenantId) {
        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().query(criteria);
    }

    void deletePreauthorizedPayments(LeasePaymentMethod paymentMethod) {
        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
        criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);

        for (PreauthorizedPayment preauthorizedPayment : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
            deletePreauthorizedPayment(preauthorizedPayment);
        }

    }
}
