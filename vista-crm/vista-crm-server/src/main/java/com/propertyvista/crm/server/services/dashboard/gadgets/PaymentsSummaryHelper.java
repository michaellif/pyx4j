/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentsSummaryHelper {

    private class PaymentTypeMapper {

        private final Path[] memberMap;

        public PaymentTypeMapper() {
            memberMap = new Path[PaymentType.values().length];
            PaymentsSummary proto = EntityFactory.create(PaymentsSummary.class);

            bind(PaymentType.Cash, proto.cash());
            bind(PaymentType.Echeck, proto.eCheque());
            bind(PaymentType.CreditCard, proto.cc());
            bind(PaymentType.EFT, proto.eft());
            bind(PaymentType.Interac, proto.interac());

        }

        public IPrimitive<BigDecimal> member(PaymentsSummary summary, PaymentType type) {
            return (IPrimitive<BigDecimal>) summary.getMember(memberMap[type.ordinal()]);
        }

        protected final void bind(PaymentType type, IPrimitive<BigDecimal> member) {
            memberMap[type.ordinal()] = member.getPath();
        }
    }

    private final PaymentTypeMapper paymentTypeMapper;

    public PaymentsSummaryHelper() {
        paymentTypeMapper = new PaymentTypeMapper();
    }

    /**
     * Takes snapshot of summary of payment records selected by the following parameters
     * 
     * @param merchantAccount
     * @param paymentStatus
     * @param snapshotDay
     * 
     * @return
     */
    public PaymentsSummary calculateSummary(MerchantAccount merchantAccount, PaymentRecord.PaymentStatus paymentStatus, LogicalDate snapshotDay) {

        PaymentsSummary summary = initPaymentsSummary(snapshotDay);

        ICursorIterator<PaymentRecord> i = Persistence.service().query(null, makeCriteria(merchantAccount, paymentStatus, snapshotDay), AttachLevel.Attached);
        while (i.hasNext()) {
            PaymentRecord r = i.next();
            PaymentType paymentType = r.paymentMethod().type().getValue();
            IPrimitive<BigDecimal> amountMember = paymentTypeMapper.member(summary, paymentType);
            amountMember.setValue(amountMember.getValue().add(r.amount().getValue()));
        }

        return summary;
    }

    private EntityQueryCriteria<PaymentRecord> makeCriteria(MerchantAccount merchantAccount, PaymentRecord.PaymentStatus paymentStatus, LogicalDate snapshotDay) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        // TODO merchantAccount
        criteria.add(PropertyCriterion.eq(criteria.proto().lastStatusChangeDate(), snapshotDay));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), paymentStatus));
        return criteria;
    }

    private PaymentsSummary initPaymentsSummary(LogicalDate snapshotDay) {
        PaymentsSummary summary = EntityFactory.create(PaymentsSummary.class);
        for (PaymentType type : PaymentType.values()) {
            paymentTypeMapper.member(summary, type).setValue(new BigDecimal("0.00"));
        }
        summary.timestamp().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        summary.snapshotDay().setValue(snapshotDay);

        return summary;
    }
}
