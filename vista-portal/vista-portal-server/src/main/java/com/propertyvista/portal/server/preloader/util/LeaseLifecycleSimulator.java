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
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;

public class LeaseLifecycleSimulator {

    private static final Logger log = LoggerFactory.getLogger(LeaseLifecycleSimulator.class);

    private final static boolean debug = false;

    private long minReserveTerm = 0L;

    private long maxReserveTerm = 1000L * 60L * 60L * 24L * 60L; // 60 days

    private static final long MIN_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L; // approx 1 Year

    private static final long MAX_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L * 5L; // approx 5 Years

    private static final long MIN_NOTICE_TERM = 1000L * 60L * 60L * 24L * 31L;

    private static final long MAX_NOTICE_TERM = 1000L * 60L * 60L * 24L * 130L;

    private long minAvailableTerm = 0;

    private long maxAvailableTerm = 1000L * 60L * 60L * 24L * 30L;

    private boolean hasImmideateApproval = false;

    private PriorityQueue<LeaseEventContainer> events;

    private LogicalDate simStart;

    private LogicalDate simEnd;

    private boolean runBilling = false;

    private LogicalDate leaseTo;

    private final int maintenanceRequestsPerMonth = 0;

    private List<MaintenanceRequestCategory> issueClassifications;

    public int numOfBills = -1;

    public int numOfPayments = -1;

    private Random random;

    private TenantAgent tenantAgent;

    private LeaseLifecycleSimulator(Random random) {
        this.random = random;
        this.tenantAgent = new DefaultTenantAgent(new Random(1));

        this.events = new PriorityQueue<LeaseLifecycleSimulator.LeaseEventContainer>(10, new Comparator<LeaseEventContainer>() {
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
        leaseTo = null;

        issueClassifications = CacheService.get(LeaseLifecycleSimulator.class.getName() + "issueClassifications");
        if (issueClassifications == null) {
            EntityQueryCriteria<MaintenanceRequestCategory> crit = EntityQueryCriteria.create(MaintenanceRequestCategory.class);
            crit.add(PropertyCriterion.isNull(crit.proto().subCategories()));
            issueClassifications = Persistence.service().query(crit);
            CacheService.put(LeaseLifecycleSimulator.class.getName() + "issueClassifications", issueClassifications);
        }
    }

    public static LeaseLifecycleSimulatorBuilder sim(Random random) {
        return new LeaseLifecycleSimulatorBuilder(random);
    }

    public void generateRandomLifeCycle(Lease lease) {
        if (debug) {
            log.info("-- Start new RandomLifeCycle for Lease {} from {}", lease.getPrimaryKey(), simStart);
        }

        SystemDateManager.setDate(simStart);
        if (lease.unit()._availableForRent().isNull()) {
            if (!ServerSideFactory.create(OccupancyFacade.class).isScopeAvailableAvailable(lease.unit().getPrimaryKey())) {
                SystemDateManager.resetDate();
                throw new IllegalStateException("lease simulation cannot be started because the unit is not available");
            } else {
                ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(lease.unit().getPrimaryKey());
            }
        }

        LogicalDate reservedOn = add(max(simStart, lease.unit()._availableForRent().getValue()),
                LeaseLifecycleSimulatorUtils.rndBetween(random, minAvailableTerm, maxAvailableTerm));

        LogicalDate leaseFrom = add(reservedOn, LeaseLifecycleSimulatorUtils.rndBetween(random, minReserveTerm, maxReserveTerm));
        lease.currentTerm().termFrom().setValue(leaseFrom);

        LogicalDate leaseTo = this.leaseTo != null ? this.leaseTo : add(leaseFrom,
                LeaseLifecycleSimulatorUtils.rndBetween(random, MIN_LEASE_TERM, MAX_LEASE_TERM));
        lease.currentTerm().termTo().setValue(leaseTo);
        lease.expectedMoveIn().setValue(leaseFrom);

        lease.creationDate().setValue(reservedOn);

        setUpBillableItemsEffectiveTime(lease);

        clearEvents();

        queueEvent(reservedOn, new Begin(lease));
        queueEvent(max(leaseFrom, sub(leaseTo, LeaseLifecycleSimulatorUtils.rndBetween(random, MIN_NOTICE_TERM, MAX_NOTICE_TERM))), new Notice(lease));
        queueEvent(leaseTo, new MoveOut(lease));
        queueEvent(DateUtils.daysAdd(leaseTo, 1), new Complete(lease));

        queueMaintenanceRequests(lease);

        try {
            while (hasNextEvent()) {
                processNextEvent();
            }
        } finally {
            cleanUp();
        }
    }

    private void queueMaintenanceRequests(Lease lease) {
        GregorianCalendar current = new GregorianCalendar();
        current.setTime(lease.currentTerm().termFrom().getValue());
        LogicalDate end = lease.currentTerm().termTo().getValue();
        while (current.getTime().before(end)) {
            for (int i = 0; i < maintenanceRequestsPerMonth; ++i) {
                queueEvent(new LogicalDate(current.getTime()), new MaintenanceRequestSubmission(lease));
                current.add(Calendar.DAY_OF_MONTH, 1);
            }
            current.set(Calendar.DAY_OF_MONTH, current.getActualMaximum(Calendar.DAY_OF_MONTH));
            current.add(Calendar.MONTH, 1);
        }
    }

    private void setUpBillableItemsEffectiveTime(Lease lease) {
        // TODO something more sophisticated is probably required here

        lease.currentTerm().version().leaseProducts().serviceItem().effectiveDate().setValue(lease.currentTerm().termFrom().getValue());

        for (BillableItem item : lease.currentTerm().version().leaseProducts().featureItems()) {
            item.effectiveDate().setValue(lease.currentTerm().termFrom().getValue());
        }
    }

    private void clearEvents() {
        events.clear();
    }

    private void queueEvent(LogicalDate fireOn, LeaseEvent event) {
        events.add(new LeaseEventContainer(fireOn, event));
        if (debug) {
            log.info("QueueEvent: {} {}", fireOn, event.getClass().getSimpleName());
        }
    }

    private void processNextEvent() {
        LeaseEventContainer container = events.poll();
        SystemDateManager.setDate(container.date());
        if (debug) {
            log.info("ProcessEvent: {} {}", container.date(), container.event().getClass().getSimpleName());
        }
        container.event().exec();
    }

    private boolean hasNextEvent() {
        return !events.isEmpty() && (events.peek().date().before(simEnd) | events.peek().date().equals(simEnd));
    }

    // EVENTS
    private class MaintenanceRequestSubmission extends AbstractLeaseEvent {

        public MaintenanceRequestSubmission(Lease lease) {
            super(1, lease);
        }

        @Override
        public void exec() {
            MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequest(lease.unit().building());
            maintenanceRequest.reporter().set(lease.leaseParticipants().iterator().next().<Tenant> cast());
            maintenanceRequest.unit().set(lease.unit());
            maintenanceRequest.submitted().setValue(SystemDateManager.getDate());
            maintenanceRequest.updated().setValue(SystemDateManager.getDate());
            maintenanceRequest.description().setValue(RandomUtil.randomLetters(50));
            maintenanceRequest.category().set(issueClassifications.get(RandomUtil.randomInt(issueClassifications.size())));
            Persistence.service().persist(maintenanceRequest);
        }
    }

    private class Begin extends AbstractLeaseEvent {

        public Begin(Lease lease) {
            super(-1, lease);
        }

        @Override
        public void exec() {
            lease.status().setValue(Status.Application);
            lease.currentTerm().type().setValue((random.nextInt() % 10 < 7) ? Type.Fixed : Type.FixedEx);
            lease = ServerSideFactory.create(LeaseFacade.class).persist(lease);
            Persistence.service().retrieveMember(lease.leaseParticipants());

            LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().get(0);

            if (!mainTenant.leaseParticipant().customer().paymentMethods().isEmpty()) {
                // new approach:
                PreauthorizedPayment pap = EntityFactory.create(PreauthorizedPayment.class);

                pap.paymentMethod().set(mainTenant.leaseParticipant().customer().paymentMethods().iterator().next());

                PreauthorizedPaymentCoveredItem papItem = EntityFactory.create(PreauthorizedPaymentCoveredItem.class);
                papItem.billableItem().set(lease.currentTerm().version().leaseProducts().serviceItem());
                papItem.amount().setValue(papItem.billableItem().agreedPrice().getValue());
                pap.coveredItems().add(papItem);
                pap.comments().setValue("Default preauthorization...");

                pap.tenant().set(mainTenant.leaseParticipant());
                Persistence.service().persist(pap);
            }

            Persistence.service().merge(mainTenant.leaseParticipant());

            for (LeaseTermTenant participant : lease.currentTerm().version().tenants()) {
                participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
            }
            for (LeaseTermGuarantor participant : lease.currentTerm().version().guarantors()) {
                participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
            }

            if (debug) {
                log.info("" + now() + " begined lease: " + lease.leaseId().getValue() + " " + lease.currentTerm().termFrom().getValue() + " - "
                        + lease.currentTerm().termTo().getValue());
                log.debug(lease.toString());
                log.info("***");
            }

            // TODO change that to Employee Agent Decision
            hasImmideateApproval = true; // till now..
            queueEvent(hasImmideateApproval ? now() : LeaseLifecycleSimulatorUtils.rndBetween(random, now(), lease.leaseFrom().getValue()),
                    new ApproveApplication(lease));
        }
    }

    private class ApproveApplication extends AbstractLeaseEvent {

        public ApproveApplication(Lease lease) {
            super(0, lease);
        }

        @Override
        public void exec() {
            ServerSideFactory.create(LeaseFacade.class).approve(lease, null, "simulation");

            if (debug) {
                System.out.println("" + now() + " approved lease: " + lease.leaseId().getValue() + " " + lease.currentTerm().termFrom().getValue() + " - "
                        + lease.currentTerm().termTo().getValue());
                System.out.println("***");
            }

            queueEvent(lease.currentTerm().termFrom().getValue(), new Activate(lease));
        }
    }

    private class Activate extends AbstractLeaseEvent {

        public Activate(Lease lease) {
            super(0, lease);
        }

        @Override
        public void exec() {
            ServerSideFactory.create(LeaseFacade.class).activate(lease);
            if (debug) {
                System.out.println("" + now() + " activated lease: " + lease.leaseId().getValue() + " " + lease.currentTerm().termFrom().getValue() + " - "
                        + lease.currentTerm().termTo().getValue());
                System.out.println("***");
            }

            if (runBilling) {
                queueReccurentBilling();
            }

            queueTenantActions();
        }

        private void queueReccurentBilling() {
            Bill lastBill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);

            LogicalDate billingRunDay = ServerSideFactory.create(BillingFacade.class).getNextBillBillingCycle(lease).targetBillExecutionDate().getValue();
            if (billingRunDay.before(lease.currentTerm().termFrom().getValue())) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(billingRunDay);
                cal.add(Calendar.MONTH, 1);
                billingRunDay = new LogicalDate(cal.getTime());
            }
            queueEvent(billingRunDay, new RunBillingRecurrent(lease));

//            new RunBillingRecurrent(lease).exec();
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
            if (runBilling & numOfPayments != 0) {
                --numOfPayments;
                performRandomPayment();
            }
            scheduleNextAction();
        }

        private void scheduleNextAction() {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(now());
            cal.add(Calendar.MONTH, 1);
            if (!cal.getTime().after(lease.currentTerm().termTo().getValue())) {
                queueEvent(new LogicalDate(cal.getTime()), new TenantsAction(lease));
            }
        }

        private void performRandomPayment() {

            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);

            if (bill != null && bill.totalDueAmount().getValue().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal amount = tenantAgent.pay(bill);
                if (debug) {
                    System.out.println("" + now() + " payed " + amount);
                }
                PaymentRecord payment = receivePayment(amount);
                if (payment != null) {
                    try {
                        ServerSideFactory.create(ARFacade.class).postPayment(payment, null);
                    } catch (ARException e) {
                        throw new Error(e);
                    }
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
                paymentRecord.paymentMethod().set(createPaymentMethod(lease.currentTerm().version().tenants().get(0)));
                paymentRecord.leaseTermParticipant().set(lease.currentTerm().version().tenants().get(0));

                Persistence.service().persist(paymentRecord.paymentMethod());
                Persistence.service().persist(paymentRecord);
                return paymentRecord;
            }
        }

        public LeasePaymentMethod createPaymentMethod(LeaseTermParticipant tenant) {
            LeasePaymentMethod m = EntityFactory.create(LeasePaymentMethod.class);
            m.type().setValue(PaymentType.CreditCard);

            // create new payment method details:
            CreditCardInfo details = EntityFactory.create(CreditCardInfo.class);
            details.cardType().setValue(CreditCardType.MasterCard);
            details.card().newNumber().setValue("00" + CommonsStringUtils.d00(RandomUtil.randomInt(99)) + CommonsStringUtils.d00(RandomUtil.randomInt(99)));
            details.card().obfuscatedNumber().setValue(DomainUtil.obfuscateCreditCardNumber(details.card().newNumber().getValue()));

            details.nameOn().setValue(tenant.leaseParticipant().customer().person().name().getStringView());
            details.expiryDate().setValue(RandomUtil.randomLogicalDate(2012, 2015));
            m.details().set(details);

            m.customer().set(tenant.leaseParticipant().customer());
            m.isProfiledMethod().setValue(Boolean.FALSE);
            m.sameAsCurrent().setValue(Boolean.FALSE);
            m.billingAddress().set(CommonsGenerator.createAddressSimple());

            return m;
        }

    }

    private class RunBillingRecurrent extends AbstractLeaseEvent {

        public RunBillingRecurrent(Lease lease) {
            super(1, lease);
        }

        @Override
        public void exec() {
            if (now().before(lease.currentTerm().termFrom().getValue())) {
                return;
            }
            numOfBills--;
            if (numOfBills != 0) {

                if (now().before(lease.currentTerm().termTo().getValue()) & lease.status().getValue() != Lease.Status.Completed) {

                    Bill lastBill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);
                    if (lastBill.billingPeriodStartDate().isNull() | lastBill.billingPeriodEndDate().getValue().compareTo(lease.leaseTo().getValue()) >= 0) {
                        // lease ended
                        return;
                    }

                    LogicalDate billingRunDay = ServerSideFactory.create(BillingFacade.class).getNextBillBillingCycle(lease).targetBillExecutionDate()
                            .getValue();
                    if (now().equals(billingRunDay) & (now().compareTo(lease.leaseTo().getValue()) < 0)
                            & (now().compareTo(lease.expectedMoveOut().getValue()) < 0)) {
                        BillingFacade billing = ServerSideFactory.create(BillingFacade.class);
                        try {
                            billing.runBilling(lease);
                            billing.confirmBill(billing.getLatestBill(lease));
                        } catch (Exception e) {
                            // TODO re-think LeaseSimulation logic (make the same as billing unit test!!!????) 
                            log.error("Error", e);
                        }

                        if (debug) {
                            System.out.println("" + now() + " executed run billing lease: " + lease.leaseId().getValue() + " "
                                    + lease.currentTerm().termFrom().getValue() + " - " + lease.currentTerm().termTo().getValue());
                            System.out.println("***");
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now());
                        cal.add(Calendar.MONTH, 1);

                        LogicalDate nextRun = new LogicalDate(cal.getTime());
                        queueEvent(nextRun, new RunBillingRecurrent(lease));

                    } else {
                        queueEvent(billingRunDay, new RunBillingRecurrent(lease));
                    }
                }
            }
        }
    }

    private class Notice extends AbstractLeaseEvent {

        public Notice(Lease lease) {
            super(400, lease);
        }

        @Override
        public void exec() {
            lease = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(lease, CompletionType.Notice, now(), lease.currentTerm().termTo().getValue(),
                    null);
        }
    }

    private class MoveOut extends AbstractLeaseEvent {

        public MoveOut(Lease lease) {
            super(500, lease);
        }

        @Override
        public void exec() {
            lease = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            ServerSideFactory.create(LeaseFacade.class).moveOut(lease, new LogicalDate(SystemDateManager.getDate()));
        }
    }

    private class Complete extends AbstractLeaseEvent {

        public Complete(Lease lease) {
            super(600, lease);
        }

        @Override
        public void exec() {
            lease = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            ServerSideFactory.create(LeaseFacade.class).complete(lease);
        }
    }

    // UTILITY FUNCTIONS
    // FIXME copied form BillingCycleManager, find some other and better way to do it
    private static LogicalDate calculateBillingCycleTargetExecutionDate(int targetDayOffset, LogicalDate billingCycleStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingCycleStartDate);
        calendar.add(Calendar.DATE, targetDayOffset);
        return new LogicalDate(calendar.getTime());
    }

    @Deprecated
    private static LogicalDate add(LogicalDate date, long term) {
        // TODO get rid of this (it doesn't work with daylight savings)
        return new LogicalDate(date.getTime() + term);
    }

    @Deprecated
    private static LogicalDate sub(LogicalDate date, long term) {
        // TODO get rid of this (it doesn't work with daylight savings)
        return add(date, -term);
    }

    private static LogicalDate max(LogicalDate a, LogicalDate b) {
        return a.after(b) ? a : b;
    }

    private static void cleanUp() {
        SystemDateManager.resetDate();
    }

    private static LogicalDate now() {
        return new LogicalDate(SystemDateManager.getDate());
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

        protected Lease lease;

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

        private final Random random;

        public DefaultTenantAgent(Random random) {
            this.random = random;
        }

        @Override
        public BigDecimal pay(Bill bill) {
            if (bill.totalDueAmount().getValue().compareTo(BigDecimal.ZERO) < 0) {
                return null;
            }
            if (random.nextDouble() < 0.66) {
                if (random.nextDouble() < 0.5) {
                    // pay just a part of the bill
                    BigDecimal part = new BigDecimal(0.1d + random.nextDouble());
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

    public static class LeaseLifecycleSimulatorBuilder {

        private final LeaseLifecycleSimulator leaseLifecycleSim;

        public LeaseLifecycleSimulatorBuilder(Random random) {
            this.leaseLifecycleSim = new LeaseLifecycleSimulator(random);
            this.leaseLifecycleSim.runBilling = false;
        }

        public LeaseLifecycleSimulator create() {
            if (leaseLifecycleSim.simStart == null) {
                throw new IllegalStateException("simulation start was not set");
            }
            if (leaseLifecycleSim.simEnd == null) {
                leaseLifecycleSim.simEnd = new LogicalDate();
            }
            return leaseLifecycleSim;
        }

        public LeaseLifecycleSimulator attachTennant(TenantAgent tenantAgent) {
            this.leaseLifecycleSim.tenantAgent = tenantAgent;
            return this.leaseLifecycleSim;
        }

        public LeaseLifecycleSimulatorBuilder start(LogicalDate start) {
            leaseLifecycleSim.simStart = start;
            return this;
        }

        public LeaseLifecycleSimulatorBuilder start(String simStart) {
            return start(toDate(simStart));
        }

        /** inclusive */
        public LeaseLifecycleSimulatorBuilder end(LogicalDate end) {
            leaseLifecycleSim.simEnd = end;
            return this;
        }

        /** inclusive */
        public LeaseLifecycleSimulatorBuilder end(String simEnd) {
            return end(toDate(simEnd));
        }

        public LeaseLifecycleSimulatorBuilder leaseTo(LogicalDate leaseTo) {
            leaseLifecycleSim.leaseTo = leaseTo;
            return this;
        }

        public LeaseLifecycleSimulatorBuilder leaseTo(String leaseTo) {
            return leaseTo(toDate(leaseTo));
        }

        public LeaseLifecycleSimulatorBuilder simulateBilling() {
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
        public LeaseLifecycleSimulatorBuilder availabilityTermConstraints(long min, long max) {
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
        public LeaseLifecycleSimulatorBuilder reservedTermConstraints(long min, long max) {
            assert min <= max;
            leaseLifecycleSim.minReserveTerm = min;
            leaseLifecycleSim.maxReserveTerm = max;
            return this;
        }

        /**
         * Approve lease after it has been created, don't make random waiting period between approval and lease from date
         */
        public LeaseLifecycleSimulatorBuilder approveImmidately() {
            leaseLifecycleSim.hasImmideateApproval = true;
            return this;
        }

        public void setNumOfBillsAndPayments(int i) {
            leaseLifecycleSim.numOfBills = i;
            leaseLifecycleSim.numOfPayments = i;
        }

    }

}
