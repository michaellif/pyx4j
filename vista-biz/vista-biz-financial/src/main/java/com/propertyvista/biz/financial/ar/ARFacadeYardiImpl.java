/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.system.YardiProcessFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.financial.yardi.YardiCharge;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARFacadeYardiImpl implements ARFacade {

    private static final I18n i18n = I18n.get(ARFacadeYardiImpl.class);

    private static final Logger log = LoggerFactory.getLogger(ARFacadeYardiImpl.class);

    @Override
    public boolean validatePayment(PaymentRecord payment) throws ARException {
        YardiReceipt receipt = createReceipt(payment);

        try {
            ServerSideFactory.create(YardiProcessFacade.class).validateReceipt(receipt);
        } catch (RemoteException e) {
            throw new ARException("Receipt validation is failed due to communication failure with Yardi", e);
        } catch (YardiServiceException e) {
            throw new ARException("Receipt validation is failed", e);
        }

        return true;
    }

    @Override
    public void postPayment(PaymentRecord payment) throws ARException {
        YardiReceipt receipt = createReceipt(payment);
        Persistence.service().persist(receipt);

        Persistence.ensureRetrieve(payment.billingAccount(), AttachLevel.Attached);
        Persistence.service().retrieve(payment.billingAccount().lease());
        ServerSideFactory.create(YardiProcessFacade.class).updateLease(payment.billingAccount().lease());

        try {
            ServerSideFactory.create(YardiProcessFacade.class).postReceipt(receipt);
        } catch (RemoteException e) {
            throw new ARException("Posting receipt to Yardi is failed due to communication failure", e);
        } catch (YardiServiceException e) {
            throw new ARException("Posting receipt to Yardi is failed", e);
        }

        Persistence.service().commit();
    }

    private YardiReceipt createReceipt(PaymentRecord paymentRecord) {
        YardiReceipt receipt = EntityFactory.create(YardiReceipt.class);
        receipt.paymentRecord().set(paymentRecord);
        receipt.amount().setValue(paymentRecord.amount().getValue().negate());
        receipt.billingAccount().set(paymentRecord.billingAccount());
        receipt.description().setValue(i18n.tr("Payment Received - Thank You"));
        receipt.claimed().setValue(false);
        receipt.postDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        return receipt;
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        YardiReceiptReversal reversal = EntityFactory.create(YardiReceiptReversal.class);
        reversal.paymentRecord().set(paymentRecord);
        reversal.amount().setValue(paymentRecord.amount().getValue());
        reversal.billingAccount().set(paymentRecord.billingAccount());
        reversal.description().setValue(i18n.tr("Payment from ''{0}'' was rejected", paymentRecord.createdDate().getValue().toString()));
        reversal.taxTotal().setValue(BigDecimal.ZERO);
        reversal.claimed().setValue(false);
        reversal.postDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        reversal.applyNSF().setValue(applyNSF);

        Persistence.service().persist(reversal);

        try {
            ServerSideFactory.create(YardiProcessFacade.class).postReceiptReversal(reversal);
        } catch (Throwable e) {
            log.debug("handling Yardi.postReceipt error", e);
            Validate.isTrue(!reversal.claimed().getValue(), "postReceipt is schedule to nightly process");
        }

        Persistence.service().commit();

        try {
            Persistence.service().retrieve(paymentRecord.billingAccount().lease());
            ServerSideFactory.create(YardiProcessFacade.class).updateLease(paymentRecord.billingAccount().lease());
        } catch (Throwable ignoreDataRetrivalFromYardy) {
            log.debug("ignoreDataRetrivalFromYardy", ignoreDataRetrivalFromYardy);
        }

    }

    @Override
    public BigDecimal getCurrentBalance(BillingAccount billingAccount) {
        return calculateTotal(getYardiCharges(billingAccount), getYardiPayments(billingAccount));
    }

    @Override
    public TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        TransactionHistoryDTO th = EntityFactory.create(TransactionHistoryDTO.class);
        List<YardiCharge> charges = getYardiCharges(billingAccount);
        List<YardiPayment> payments = getYardiPayments(billingAccount);
        th.lineItems().addAll(charges);
        th.lineItems().addAll(payments);
        th.currentBalanceAmount().setValue(calculateTotal(charges, payments));
        th.issueDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        return th;
    }

    private List<YardiCharge> getYardiCharges(BillingAccount billingAccount) {
        EntityQueryCriteria<YardiCharge> criteria = EntityQueryCriteria.create(YardiCharge.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().id());
        return Persistence.service().query(criteria);
    }

    private List<YardiPayment> getYardiPayments(BillingAccount billingAccount) {
        EntityQueryCriteria<YardiPayment> criteria = EntityQueryCriteria.create(YardiPayment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().id());
        return Persistence.service().query(criteria);
    }

    private BigDecimal calculateTotal(List<YardiCharge> charges, List<YardiPayment> payments) {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceLineItem item : charges) {
            total = total.add(item.amount().getValue());
        }
        for (InvoiceLineItem item : payments) {
            total = total.add(item.amount().getValue());
        }
        return total;
    }

    @Override
    public void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {
        throw new UnsupportedOperationException();

    }

    @Override
    public DebitCreditLink createHardLink(PaymentRecord paymentRecord, InvoiceDebit debit, BigDecimal amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeHardLink(DebitCreditLink link) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void postDepositRefund(Deposit deposit) {
        throw new UnsupportedOperationException();

    }

    @Override
    public List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BuildingArrearsSnapshot getArrearsSnapshot(Building buildingStub, LogicalDate asOf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArrearsHistory(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArrearsHistory(Building building) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InvoiceLineItem> getLatestBillingActivity(BillingAccount billingAccount) {
        // get latest payments (for the last 3 months)
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        LogicalDate dateFrom = new LogicalDate(cal.getTime());
        EntityQueryCriteria<InvoicePayment> criteria = EntityQueryCriteria.create(InvoicePayment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.gt(criteria.proto().postDate(), dateFrom));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().paymentRecord()));
        return new ArrayList<InvoiceLineItem>(Persistence.service().query(criteria));
    }

    @Override
    public Map<LeaseTermTenant, BigDecimal> getPADBalance(BillingAccount billingAccount, BillingCycle cycle) {
        // TODO Auto-generated method stub
        return null;
    }
}
