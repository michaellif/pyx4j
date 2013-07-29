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
package com.propertyvista.server.common.util;

import java.math.BigDecimal;
import java.util.Iterator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;

public final class PaymentsSummaryHelper {

    public interface PaymentsSummarySnapshotHook {

        /**
         * @param summmary
         * @return if the snapshot taking should continue
         */
        boolean onPaymentsSummarySnapshotTaken(PaymentsSummary summmary);

        /**
         * @param caught
         * @return if the snapshot taking should continue
         */
        boolean onPaymentsSummarySnapshotFailed(Throwable caught);

    }

    private class PaymentTypeMapper {

        private final Path[] memberMap;

        public PaymentTypeMapper() {
            memberMap = new Path[PaymentType.values().length];
            PaymentsSummary proto = EntityFactory.create(PaymentsSummary.class);

            bind(PaymentType.Cash, proto.cash());
            bind(PaymentType.Check, proto.check());
            bind(PaymentType.Echeck, proto.eCheck());
            bind(PaymentType.CreditCard, proto.cc());
            bind(PaymentType.DirectBanking, proto.eft());
            bind(PaymentType.Interac, proto.interac());
        }

        public IPrimitive<BigDecimal> getMember(PaymentsSummary summary, PaymentType type) {
            return (IPrimitive<BigDecimal>) summary.getMember(memberMap[type.ordinal()]);
        }

        protected final void bind(PaymentType type, IPrimitive<BigDecimal> member) {
            memberMap[type.ordinal()] = member.getPath();
        }
    }

    private final PaymentTypeMapper paymentTypeMapper;

    private final PaymentsSummarySnapshotHook paymentsSummarySnapshotHook;

    public PaymentsSummaryHelper() {
        this(null);
    }

    public PaymentsSummaryHelper(PaymentsSummarySnapshotHook paymentsSummarySnapshotHook) {
        this.paymentsSummarySnapshotHook = paymentsSummarySnapshotHook;
        this.paymentTypeMapper = new PaymentTypeMapper();
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
    public PaymentsSummary calculateSummary(Building building, PaymentRecord.PaymentStatus paymentStatus, LogicalDate snapshotDay) {
        if (building == null || building.isNull()) {
            throw new IllegalArgumentException("building is a mandatory argument");
        }
        if (paymentStatus == null) {
            throw new IllegalArgumentException("paymentStatus is a mandatory argument");
        }
        if (snapshotDay == null) {
            throw new IllegalArgumentException("snapshotDay is a amandatory argument");
        }

        PaymentsSummary summary = initPaymentsSummary(snapshotDay);

        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount().lease().unit().building(), building));
        criteria.add(PropertyCriterion.eq(criteria.proto().lastStatusChangeDate(), snapshotDay));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), paymentStatus));

        ICursorIterator<PaymentRecord> i = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (i.hasNext()) {
                PaymentRecord r = i.next();
                Persistence.service().retrieve(r.merchantAccount());
                PaymentType paymentType = r.paymentMethod().type().getValue();
                IPrimitive<BigDecimal> amountMember = paymentTypeMapper.getMember(summary, paymentType);
                amountMember.setValue(amountMember.getValue().add(r.amount().getValue()));
            }
        } finally {
            i.close();
        }

        summary.status().setValue(paymentStatus);
        summary.building().set(building);
        return summary;
    }

    public PaymentsSummary calculateSummary(MerchantAccount merchantAccount, PaymentRecord.PaymentStatus paymentStatus, LogicalDate snapshotDay) {
        if (merchantAccount == null || merchantAccount.isNull()) {
            throw new IllegalArgumentException("merchantAccount is a mandatory argument");
        }
        if (paymentStatus == null) {
            throw new IllegalArgumentException("paymentStatus is a mandatory argument");
        }
        if (snapshotDay == null) {
            throw new IllegalArgumentException("snapshotDay is a amandatory argument");
        }

        PaymentsSummary summary = initPaymentsSummary(snapshotDay);

        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccount(), merchantAccount));
        criteria.add(PropertyCriterion.eq(criteria.proto().lastStatusChangeDate(), snapshotDay));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), paymentStatus));

        ICursorIterator<PaymentRecord> i = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (i.hasNext()) {
                PaymentRecord r = i.next();
                Persistence.service().retrieve(r.merchantAccount());
                PaymentType paymentType = r.paymentMethod().type().getValue();
                IPrimitive<BigDecimal> amountMember = paymentTypeMapper.getMember(summary, paymentType);
                amountMember.setValue(amountMember.getValue().add(r.amount().getValue()));
            }
        } finally {
            i.close();
        }

        summary.status().setValue(paymentStatus);
        summary.merchantAccount().set(merchantAccount);
        return summary;
    }

    /**
     * @return <code>true</code> if the summary has payments, else <code>false</code>
     */
    public boolean hasPayments(PaymentsSummary summary) {
        final BigDecimal ZERO = new BigDecimal("0.00"); // not using BigDecimal.ZERO because i'm not sure about percision
        // TODO refactor this access to private value memberMap 
        for (Path memberPath : paymentTypeMapper.memberMap) {
            if (!summary.getMember(memberPath).getValue().equals(ZERO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * takes snapshots of payments summary for every merchant account and every payment status, runs hook on after finishing every snapshot, if a snapshot
     * with the same key (merchant account, payment status, snapshotDay) already exists it's updated (overridden with new values)
     * 
     * @deprecated if used generates too many records in database per day... if used as intended (every day) will bomb the database with A LOT of records
     */
    @Deprecated
    public void takePaymentsSummarySnapshots(final LogicalDate snapshotDay) {

        final Iterator<MerchantAccount> merchantAccountsIterator = Persistence.service().query(null, EntityQueryCriteria.create(MerchantAccount.class),
                AttachLevel.IdOnly);

        boolean shouldRun = true;
        while (merchantAccountsIterator.hasNext() & shouldRun) {
            for (final PaymentRecord.PaymentStatus paymentStatus : PaymentStatus.values()) {
                try {
                    PaymentsSummary summary = new UnitOfWork().execute(new Executable<PaymentsSummary, Throwable>() {
                        @Override
                        public PaymentsSummary execute() throws Throwable {
                            MerchantAccount merchantAccount = merchantAccountsIterator.next();

                            PaymentsSummary currentPaymentsSummary = retrieveSummary(merchantAccount, paymentStatus, snapshotDay);
                            PaymentsSummary updatedPaymentsSummary = calculateSummary(merchantAccount, paymentStatus, snapshotDay);

                            boolean isUpdated = true;
                            if (currentPaymentsSummary != null) {
                                if (EntityGraph.fullyEqualValues(currentPaymentsSummary, updatedPaymentsSummary)) {
                                    updatedPaymentsSummary.setPrimaryKey(currentPaymentsSummary.getPrimaryKey());
                                } else {
                                    isUpdated = false;
                                }
                            }
                            if (isUpdated) {
                                Persistence.service().persist(updatedPaymentsSummary);
                            }
                            return updatedPaymentsSummary;
                        }
                    });

                    if (paymentsSummarySnapshotHook != null) {
                        shouldRun = paymentsSummarySnapshotHook.onPaymentsSummarySnapshotTaken(summary);
                    }

                } catch (Throwable e) {
                    if (paymentsSummarySnapshotHook != null) {
                        shouldRun = paymentsSummarySnapshotHook.onPaymentsSummarySnapshotFailed(e);
                    }
                }

            }
        }

    }

    private PaymentsSummary retrieveSummary(MerchantAccount merchantAccount, PaymentRecord.PaymentStatus paymentStatus, LogicalDate snapshotDay) {
        EntityQueryCriteria<PaymentsSummary> criteria = EntityQueryCriteria.create(PaymentsSummary.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccount(), merchantAccount));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), paymentStatus));
        criteria.add(PropertyCriterion.eq(criteria.proto().snapshotDay(), snapshotDay));
        return Persistence.service().retrieve(criteria);
    }

    private PaymentsSummary initPaymentsSummary(LogicalDate snapshotDay) {
        PaymentsSummary summary = EntityFactory.create(PaymentsSummary.class);

        for (PaymentType type : PaymentType.values()) {
            paymentTypeMapper.getMember(summary, type).setValue(new BigDecimal("0.00"));
        }
        summary.snapshotDay().setValue(snapshotDay);

        return summary;
    }
}
