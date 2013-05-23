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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.Tenant;

class PreauthorizedPaymentAgreementMananger {

    PreauthorizedPayment persistPreauthorizedPayment(PreauthorizedPayment preauthorizedPayment, Tenant tenantId) {
        preauthorizedPayment.tenant().set(tenantId);
        // Creates a new version of PAP if values changed and there are payments created
        if (!preauthorizedPayment.id().isNull()) {
            PreauthorizedPayment origPreauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPayment.getPrimaryKey());

            if (!EntityGraph.fullyEqualValues(origPreauthorizedPayment, preauthorizedPayment)) {
                // If tenant modifies PAP after cut off date - original will be used in this cycle and a new one in next cycle.
                LogicalDate cutOffDate = ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(tenantId.lease());
                if (SystemDateManager.getDate().after(cutOffDate)) {
                    origPreauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
                    origPreauthorizedPayment.expiring().setValue(cutOffDate);
                    Persistence.service().merge(origPreauthorizedPayment);

                    preauthorizedPayment = EntityGraph.businessDuplicate(preauthorizedPayment);
                } else {
                    boolean hasPaymentRecords = false;
                    {
                        EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
                        criteria.eq(criteria.proto().preauthorizedPayment(), preauthorizedPayment);
                        hasPaymentRecords = Persistence.service().count(criteria) > 0;
                    }
                    if (hasPaymentRecords) {
                        origPreauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
                        Persistence.service().merge(origPreauthorizedPayment);

                        preauthorizedPayment = EntityGraph.businessDuplicate(preauthorizedPayment);
                    }
                }
            }
        }

        Persistence.service().merge(preauthorizedPayment);
        return null;
    }

    //If Tenant removes PAP - payment will NOT be canceled.
    void deletePreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId) {
        PreauthorizedPayment preauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPaymentId.getPrimaryKey());
        preauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
        Persistence.service().merge(preauthorizedPayment);
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
            new ScheduledPaymentsManager().cancelScheduledPayments(preauthorizedPayment);
        }

    }
}
