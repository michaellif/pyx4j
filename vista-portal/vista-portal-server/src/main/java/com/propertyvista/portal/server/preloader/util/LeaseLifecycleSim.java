/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;
import java.util.Random;

import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.misc.VistaTODO;

public class LeaseLifecycleSim {

    public static final long DAY = 1000L * 60L * 60L * 24L;

    public static final long MONTH = 1000L * 60L * 60L * 24L * 30L;

    public static final long YEAR = 1000L * 60L * 60L * 24L * 365L;

    private final static Random RND = new Random(1);

    // TODO define these as customizable parameters via builder
    private long minReserveTerm = 0L;

    private long maxReserveTerm = 1000L * 60L * 60L * 24L * 60L; // 60 days

    private static final long MIN_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L; // approx 1 Year

    private static final long MAX_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L * 5L; // approx 5 Years

    private static final long MIN_NOTICE_TERM = 1000L * 60L * 60L * 24L * 31L;

    private static final long MAX_NOTICE_TERM = 1000L * 60L * 60L * 24L * 60L;

    private long minAvailableTerm = 0;

    private long maxAvailableTerm = 1000L * 60L * 60L * 24L * 30L;

    private boolean hasImmideateApproval = false;

    private PriorityQueue<LeaseEventContainer> events;

    private LogicalDate simStart;

    private LogicalDate simEnd;

    private boolean runBilling = false;

    private TenantAgent tenantAgent = new DefaultTenantAgent();

    private LeaseLifecycleSim() {
        this.events = new PriorityQueue<LeaseLifecycleSim.LeaseEventContainer>(10, new Comparator<LeaseEventContainer>() {
            @Override
            public int compare(LeaseEventContainer arg0, LeaseEventContainer arg1) {
                int cmp = arg0.date().compareTo(arg1.date());

                if (cmp == 0) {
                    return arg0.priority().compareTo(arg1.priority());
                } else {
                    return cmp;
                }
            }
        });
    }

    public static LeaseLifecycleSimBuilder sim() {

        return new LeaseLifecycleSimBuilder();

    }

    public void generateRandomLeaseLifeCycle(Lease lease) {
        if (lease.unit()._availableForRent().isNull()) {
            return;
        }

        LogicalDate reservedOn = add(max(simStart, lease.unit()._availableForRent().getValue()), rndBetween(minAvailableTerm, maxAvailableTerm));

        LogicalDate leaseFrom = add(reservedOn, rndBetween(minReserveTerm, maxReserveTerm));
        LogicalDate leaseTo = add(leaseFrom, rndBetween(MIN_LEASE_TERM, MAX_LEASE_TERM));

        lease.leaseFrom().setValue(leaseFrom);
        lease.leaseTo().setValue(leaseTo);

        setUpBillableItemsEffectiveTime(lease);

        clearEvents();

        queueEvent(reservedOn, new Create(lease));
        queueEvent(max(leaseFrom, sub(leaseTo, rndBetween(MIN_NOTICE_TERM, MAX_NOTICE_TERM))), new Notice(lease));
        queueEvent(leaseTo, new Complete(lease));

        try {
            while (hasNextEvent()) {
                processNextEvent();
            }
        } finally {
            cleanUp();
        }
    }

    private void setUpBillableItemsEffectiveTime(Lease lease) {
        // TODO something more sophisticated is probably required here

        lease.version().leaseProducts().serviceItem().effectiveDate().setValue(lease.leaseFrom().getValue());

        for (BillableItem item : lease.version().leaseProducts().featureItems()) {
            item.effectiveDate().setValue(lease.leaseFrom().getValue());
        }
    }

    private void clearEvents() {
        events.clear();
    }

    private void queueEvent(LogicalDate fireOn, LeaseEvent event) {
        events.add(new LeaseEventContainer(fireOn, event));
    }

    private void processNextEvent() {
        LeaseEventContainer container = events.poll();
        Persistence.service().setTransactionSystemTime(container.date());
        container.event().exec();
    }

    private boolean hasNextEvent() {
        return !events.isEmpty() && events.peek().date().before(simEnd);
    }

    // EVENTS

    private class Create extends AbstractLeaseEvent {

        public Create(Lease lease) {
            super(-1, lease);
        }

        @Override
        public void exec() {
            leaseFacade().initLease(lease);

            // TODO change that to Employee Agent Decision
            queueEvent(hasImmideateApproval ? now() : rndBetween(now(), lease.leaseFrom().getValue()), new ApproveApplication(lease));
        }
    }

    private class ApproveApplication extends AbstractLeaseEvent {

        public ApproveApplication(Lease lease) {
            super(0, lease);
        }

        @Override
        public void exec() {
            leaseFacade().approveApplication(lease, null, "simulation");

            if (!VistaTODO.removedForProduction) {
                BillingFacade billingFacade = ServerSideFactory.create(BillingFacade.class);
                billingFacade.confirmBill(billingFacade.getLatestBill(lease));
            }

            queueEvent(lease.leaseFrom().getValue(), new Activate(lease));
        }
    }

    private class Activate extends AbstractLeaseEvent {

        public Activate(Lease lease) {
            super(0, lease);
        }

        @Override
        public void exec() {
            leaseFacade().activate(lease.getPrimaryKey());

            if (runBilling) {
                queueReccurentBilling();
            }

            queueTenantActions();
        }

        private void queueReccurentBilling() {
            // TODO the day must be fetched from the facade
            Calendar cal = Calendar.getInstance();
            cal.setTime(now());

            LogicalDate firstBillingDay;
            if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
                cal.add(Calendar.MONTH, 1);
            }
            cal.set(Calendar.DAY_OF_MONTH, 15);
            firstBillingDay = new LogicalDate(cal.getTime());
            queueEvent(firstBillingDay, new RunBillingRecurrent(lease));
        }

        private void queueTenantActions() {
            Calendar cal = Calendar.getInstance();
            cal.setTime(now());

            if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
                cal.add(Calendar.MONTH, 1);
            }

            queueEvent(new LogicalDate(cal.getTime()), new TenantsAction(lease));
        }
    }

    private class TenantsAction extends AbstractLeaseEvent {

        public TenantsAction(Lease lease) {
            super(5, lease);
        }

        @Override
        public void exec() {
            if (runBilling) {
                performRandomPayment();
            }
            scheduleNextAction();
        }

        private void scheduleNextAction() {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(now());
            cal.add(Calendar.MONTH, 1);
            if (!cal.getTime().after(lease.leaseTo().getValue())) {
                queueEvent(new LogicalDate(cal.getTime()), new TenantsAction(lease));
            }
        }

        private void performRandomPayment() {

            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);

            if (bill != null && !bill.totalDueAmount().getValue().equals(BigDecimal.ZERO)) {
                BigDecimal amount = tenantAgent.pay(bill);
                PaymentRecord payment = receivePayment(amount);
                if (payment != null) {
                    ServerSideFactory.create(ARFacade.class).postPayment(payment);
                }
            }
        }

        private PaymentRecord receivePayment(BigDecimal amount) {
            if (amount == null) {
                return null;
            } else {
                PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
                paymentRecord.createdDate().setValue(now());
                paymentRecord.receivedDate().setValue(now());
                paymentRecord.targetDate().setValue(now());
                paymentRecord.finalizeDate().setValue(now());
                paymentRecord.lastStatusChangeDate().setValue(now());
                paymentRecord.amount().setValue(amount);
                paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
                paymentRecord.billingAccount().set(lease.billingAccount());
                paymentRecord.paymentMethod().set(createPaymentMethod(lease.version().tenants().get(0)));
                paymentRecord.leaseParticipant().set(lease.version().tenants().get(0));

                Persistence.service().persist(paymentRecord.paymentMethod());
                Persistence.service().persist(paymentRecord);
                Persistence.service().commit();
                return paymentRecord;
            }
        }

        public PaymentMethod createPaymentMethod(LeaseParticipant tenant) {
            PaymentMethod m = EntityFactory.create(PaymentMethod.class);
            m.type().setValue(PaymentType.CreditCard);

            // create new payment method details:
            CreditCardInfo details = EntityFactory.create(CreditCardInfo.class);
            details.cardType().setValue(CreditCardType.MasterCard);
            details.numberRefference().setValue(CommonsStringUtils.d00(RandomUtil.randomInt(99)) + CommonsStringUtils.d00(RandomUtil.randomInt(99)));
            details.nameOn().setValue(tenant.customer().person().name().getStringView());
            details.expiryDate().setValue(RandomUtil.randomLogicalDate(2012, 2015));
            m.details().set(details);

            m.leaseParticipant().set(tenant);
            m.isOneTimePayment().setValue(Boolean.TRUE);
            m.sameAsCurrent().setValue(Boolean.FALSE);
            m.billingAddress().set(CommonsGenerator.createAddress());
            m.phone().setValue(CommonsGenerator.createPhone());

            return m;
        }

    }

    private class RunBillingRecurrent extends AbstractLeaseEvent {

        public RunBillingRecurrent(Lease lease) {
            super(1, lease);
        }

        @Override
        public void exec() {
            if (!VistaTODO.removedForProduction) {
                if (now().before(lease.leaseTo().getValue()) & lease.version().status().getValue() != Lease.Status.Completed) {

                    // TODO THIS is REALLY CREEPY part, talk to Michael about adding API to the facade that lets check when billing is allowed to run **********
                    // the following code is copies the calculations inside the billing facade privates 
                    Persistence.service().retrieve(lease.billingAccount());
                    Bill lastBill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);

                    Calendar billingPeriodStartDate = new GregorianCalendar();
                    billingPeriodStartDate.setTime(lastBill.billingPeriodStartDate().getValue());
                    billingPeriodStartDate.add(Calendar.MONTH, 1);
                    if (billingPeriodStartDate.getTime().after(lease.leaseTo().getValue())) {
                        // lease ended
                        return;
                    }
                    LogicalDate billingCycleDay = calculateBillingCycleTargetExecutionDate(lease.billingAccount().billingType(), new LogicalDate(
                            billingPeriodStartDate.getTime()));
                    // CREEPY PART ENDS HERE *******************************************************************************************************************

                    if (now().equals(billingCycleDay)) {
                        BillingFacade billing = ServerSideFactory.create(BillingFacade.class);
                        billing.runBilling(lease);
                        billing.confirmBill(billing.getLatestBill(lease));

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now());
                        cal.add(Calendar.MONTH, 1);
                        LogicalDate nextRun = new LogicalDate(cal.getTime());
                        queueEvent(nextRun, new RunBillingRecurrent(lease));

                    } else {
                        queueEvent(billingCycleDay, new RunBillingRecurrent(lease));
                    }

                }
            }
        }
    }

    private class Notice extends AbstractLeaseEvent {

        public Notice(Lease lease) {
            super(0, lease);
        }

        @Override
        public void exec() {
            leaseFacade().createCompletionEvent(lease.getPrimaryKey(), CompletionType.Notice, now(), lease.leaseTo().getValue());
        }
    }

    private class Complete extends AbstractLeaseEvent {

        public Complete(Lease lease) {
            super(500, lease);
        }

        @Override
        public void exec() {
            leaseFacade().complete(lease.getPrimaryKey());
        }
    }

    // UTILITY FUNCTIONS
    // FIXME copied form BillingCycleManager, find some other better way to do it
    private static LogicalDate calculateBillingCycleTargetExecutionDate(BillingType cycle, LogicalDate billingCycleStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingCycleStartDate);
        calendar.add(Calendar.DATE, -cycle.paymentFrequency().getValue().getBillRunTargetDayOffset());
        return new LogicalDate(calendar.getTime());
    }

    private static LeaseFacade leaseFacade() {
        return ServerSideFactory.create(LeaseFacade.class);
    }

    private static long rndBetween(long min, long max) {
        assert min <= max;
        if (max == min) {
            return min;
        } else {
            return min + Math.abs(RND.nextLong()) % (max - min);
        }
    }

    private static LogicalDate rndBetween(LogicalDate min, LogicalDate max) {
        return new LogicalDate(rndBetween(min.getTime(), max.getTime()));
    }

    private static LogicalDate add(LogicalDate date, long term) {
        return new LogicalDate(date.getTime() + term);
    }

    private static LogicalDate sub(LogicalDate date, long term) {
        return add(date, -term);
    }

    private static LogicalDate max(LogicalDate a, LogicalDate b) {
        return a.after(b) ? a : b;
    }

    private static void cleanUp() {
        Persistence.service().setTransactionSystemTime(null);
    }

    private static LogicalDate now() {
        return new LogicalDate(Persistence.service().getTransactionSystemTime());
    }

    private static LogicalDate toDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
            return new LogicalDate(formatter.parse(date));
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

    public interface LeaseEvent {

        void exec();

        Integer priority();
    }

    public abstract class AbstractLeaseEvent implements LeaseEvent {

        private final int priority;

        protected final Lease lease;

        public AbstractLeaseEvent(int priority, Lease lease) {
            this.priority = priority;
            this.lease = lease;
        }

        @Override
        public Integer priority() {
            return priority;
        }

    }

    public static class LeaseEventContainer {

        private final LeaseEvent event;

        private final LogicalDate date;

        public LeaseEventContainer(LogicalDate date, LeaseEvent event) {
            this.event = event;
            this.date = new LogicalDate(date);
        }

        public LogicalDate date() {
            return date;
        }

        public Integer priority() {
            return event.priority();
        }

        public LeaseEvent event() {
            return event;
        }
    }

    public static class DefaultTenantAgent implements TenantAgent {

        @Override
        public BigDecimal pay(Bill bill) {
            if (bill.totalDueAmount().getValue().compareTo(BigDecimal.ZERO) < 0) {
                return null;
            }
            if (RND.nextDouble() < 0.66) {
                if (RND.nextDouble() < 0.5) {
                    // pay just a part of the bill                    
                    BigDecimal part = new BigDecimal(RND.nextDouble());
                    return bill.totalDueAmount().getValue().multiply(part);
                } else {
                    // don't pay at all
                    return null;
                }
            } else {
                // pay everything
                return bill.totalDueAmount().getValue();
            }
        }
    }

    public static class LeaseLifecycleSimBuilder {

        private final LeaseLifecycleSim leaseLifecycleSim;

        public LeaseLifecycleSimBuilder() {
            this.leaseLifecycleSim = new LeaseLifecycleSim();
            this.leaseLifecycleSim.runBilling = false;
        }

        public LeaseLifecycleSim create() {
            if (leaseLifecycleSim.simStart == null) {
                throw new IllegalStateException("simulation start was not set");
            }
            if (leaseLifecycleSim.simEnd == null) {
                leaseLifecycleSim.simEnd = new LogicalDate();
            }
            return leaseLifecycleSim;
        }

        public LeaseLifecycleSim attachTennant(TenantAgent tenantAgent) {
            this.leaseLifecycleSim.tenantAgent = tenantAgent;
            return this.leaseLifecycleSim;
        }

        public LeaseLifecycleSimBuilder start(LogicalDate start) {
            leaseLifecycleSim.simStart = start;
            return this;
        }

        public LeaseLifecycleSimBuilder start(String simStart) {
            return start(toDate(simStart));
        }

        public LeaseLifecycleSimBuilder end(LogicalDate end) {
            leaseLifecycleSim.simEnd = end;
            return this;
        }

        public LeaseLifecycleSimBuilder end(String simEnd) {
            return end(toDate(simEnd));
        }

        public LeaseLifecycleSimBuilder simulateBilling() {
            leaseLifecycleSim.runBilling = true;
            return this;
        }

        // TODO create some kind of TimeSpan class and use it
        /**
         * Set the minimum and maximum term that will pass until lease will become reserved
         * 
         * @param min
         *            minimum time
         * @param max
         *            maximum time
         */
        public LeaseLifecycleSimBuilder availabilityTermConstraints(long min, long max) {
            assert min <= max;
            leaseLifecycleSim.minAvailableTerm = min;
            leaseLifecycleSim.maxAvailableTerm = max;
            return this;
        }

        /**
         * Set the time constraints for the period between lease reservation and lease activation
         * 
         * @param min
         *            minimum time
         * @param max
         *            maximum time
         */
        public LeaseLifecycleSimBuilder reservedTermConstraints(long min, long max) {
            assert min <= max;
            leaseLifecycleSim.minReserveTerm = min;
            leaseLifecycleSim.maxReserveTerm = max;
            return this;
        }

        /**
         * Approve lease after it has been created, don't make random waiting period between approval and lease from date
         */
        public LeaseLifecycleSimBuilder approveImmidately() {
            leaseLifecycleSim.hasImmideateApproval = true;
            return this;
        }

    }

}
